package com.youngman.hostel.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.youngman.hostel.entity.BookingOrder;
import com.youngman.hostel.entity.HostelBed;
import com.youngman.hostel.entity.HostelRoom;
import com.youngman.hostel.entity.SysUser;
import com.youngman.hostel.mapper.BookingOrderMapper;
import com.youngman.hostel.mapper.HostelBedMapper;
import com.youngman.hostel.mapper.HostelRoomMapper;
import com.youngman.hostel.mapper.SysUserMapper;
import com.youngman.hostel.service.BedAllocationService;
import com.youngman.hostel.service.HostelBedService;
import com.youngman.hostel.service.HostelRoomService;
import com.youngman.hostel.vo.AllocationNoticeVO;
import com.youngman.hostel.vo.HostelBedAvailableVO;
import com.youngman.hostel.vo.RoomOccupancyGenderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 床位分配业务实现
 */
@Service
@RequiredArgsConstructor
public class BedAllocationServiceImpl implements BedAllocationService {

    private static final int ORDER_STATUS_PAID = 2;
    private static final int ORDER_STATUS_ALLOCATED = 3;

    private final BookingOrderMapper bookingOrderMapper;
    private final SysUserMapper sysUserMapper;
    private final HostelBedService hostelBedService;
    private final HostelRoomService hostelRoomService;
    private final HostelRoomMapper hostelRoomMapper;
    private final HostelBedMapper hostelBedMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public AllocationNoticeVO allocateBedForOrder(Long orderId) {
        if (orderId == null) {
            throw new IllegalArgumentException("orderId不能为空");
        }

        BookingOrder order = bookingOrderMapper.selectById(orderId);
        if (order == null || Integer.valueOf(1).equals(order.getDeleted())) {
            throw new RuntimeException("订单不存在, orderId=" + orderId);
        }
        if (!Integer.valueOf(ORDER_STATUS_PAID).equals(order.getOrderStatus())) {
            throw new RuntimeException("只有已支付状态的订单才允许分配床位");
        }

        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getId, order.getUserId())
                .eq(SysUser::getDeleted, 0)
                .last("limit 1"));
        if (user == null) {
            throw new RuntimeException("订单用户不存在, userId=" + order.getUserId());
        }

        List<HostelBedAvailableVO> candidateBeds = hostelBedService.getAvailableBedsByUserGender(
                order.getCheckInDate(),
                order.getCheckOutDate(),
                user.getGender());

        if (candidateBeds == null || candidateBeds.isEmpty()) {
            throw new RuntimeException("当前时间段无可分配床位");
        }

        HostelBedAvailableVO bestBed = chooseBestBedByOccupancy(order, candidateBeds);
        if (bestBed == null) {
            throw new RuntimeException("未能选择可分配床位");
        }

        order.setRoomId(bestBed.getRoomId());
        order.setBedId(bestBed.getBedId());
        order.setAssignTime(LocalDateTime.now());
        order.setOrderStatus(ORDER_STATUS_ALLOCATED);
        order.setUpdateTime(LocalDateTime.now());

        int rows = bookingOrderMapper.updateById(order);
        if (rows <= 0) {
            throw new RuntimeException("更新订单分配信息失败");
        }

        return buildAllocationNotice(order.getId(), bestBed.getRoomId(), bestBed.getBedId());
    }

    @Override
    public HostelBedAvailableVO chooseBestBed(List<HostelBedAvailableVO> candidateBeds) {
        if (candidateBeds == null || candidateBeds.isEmpty()) {
            return null;
        }
        // 兜底策略：按 roomId、bedId 升序选第一个
        return candidateBeds.stream()
                .min(Comparator.comparing(HostelBedAvailableVO::getRoomId)
                        .thenComparing(HostelBedAvailableVO::getBedId))
                .orElse(null);
    }

    @Override
    public AllocationNoticeVO buildAllocationNotice(Long orderId, Long roomId, Long bedId) {
        BookingOrder order = bookingOrderMapper.selectById(orderId);
        if (order == null || Integer.valueOf(1).equals(order.getDeleted())) {
            throw new RuntimeException("订单不存在, orderId=" + orderId);
        }

        HostelRoom room = hostelRoomMapper.selectOne(new LambdaQueryWrapper<HostelRoom>()
                .eq(HostelRoom::getId, roomId)
                .eq(HostelRoom::getDeleted, 0)
                .last("limit 1"));
        if (room == null) {
            throw new RuntimeException("房间不存在, roomId=" + roomId);
        }

        HostelBed bed = hostelBedMapper.selectOne(new LambdaQueryWrapper<HostelBed>()
                .eq(HostelBed::getId, bedId)
                .eq(HostelBed::getDeleted, 0)
                .last("limit 1"));
        if (bed == null) {
            throw new RuntimeException("床位不存在, bedId=" + bedId);
        }

        RoomOccupancyGenderVO occupancy = hostelRoomService.getRoomOccupancyGender(
                roomId,
                order.getCheckInDate(),
                order.getCheckOutDate());

        AllocationNoticeVO vo = new AllocationNoticeVO();
        vo.setOrderId(order.getId());
        vo.setOrderNo(order.getOrderNo());
        vo.setRoomId(room.getId());
        vo.setRoomNo(room.getRoomNo());
        vo.setRoomName(room.getRoomName());
        vo.setBedId(bed.getId());
        vo.setBedNo(bed.getBedNo());
        vo.setOccupiedCount(occupancy == null || occupancy.getOccupiedCount() == null ? 0 : occupancy.getOccupiedCount());
        vo.setMaleCount(occupancy == null || occupancy.getMaleCount() == null ? 0 : occupancy.getMaleCount());
        vo.setFemaleCount(occupancy == null || occupancy.getFemaleCount() == null ? 0 : occupancy.getFemaleCount());
        vo.setMessage("您的订单已成功分配至" + room.getRoomNo() + "房间 " + bed.getBedNo() + "床位。");
        return vo;
    }

    /**
     * 核心选床策略：
     * 1) 优先选择当前时间段已有入住人数最多的房间（提高房间利用率）
     * 2) 人数相同按 roomId 升序
     * 3) 在目标房间里选 bedId 最小的床位
     */
    private HostelBedAvailableVO chooseBestBedByOccupancy(BookingOrder order, List<HostelBedAvailableVO> candidateBeds) {
        Map<Long, List<HostelBedAvailableVO>> roomToBeds = candidateBeds.stream()
                .collect(Collectors.groupingBy(HostelBedAvailableVO::getRoomId));

        Long bestRoomId = null;
        int bestOccupied = -1;

        for (Map.Entry<Long, List<HostelBedAvailableVO>> entry : roomToBeds.entrySet()) {
            Long roomId = entry.getKey();
            RoomOccupancyGenderVO occupancy = hostelRoomService.getRoomOccupancyGender(
                    roomId, order.getCheckInDate(), order.getCheckOutDate());
            int occupied = occupancy == null || occupancy.getOccupiedCount() == null ? 0 : occupancy.getOccupiedCount();

            if (occupied > bestOccupied) {
                bestOccupied = occupied;
                bestRoomId = roomId;
            } else if (occupied == bestOccupied && bestRoomId != null && roomId < bestRoomId) {
                bestRoomId = roomId;
            } else if (occupied == bestOccupied && bestRoomId == null) {
                bestRoomId = roomId;
            }
        }

        if (bestRoomId == null) {
            return chooseBestBed(candidateBeds);
        }
        return roomToBeds.get(bestRoomId).stream()
                .min(Comparator.comparing(HostelBedAvailableVO::getBedId))
                .orElseGet(() -> chooseBestBed(candidateBeds));
    }
}
