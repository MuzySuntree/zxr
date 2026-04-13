package com.youngman.hostel.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.youngman.hostel.entity.HostelBed;
import com.youngman.hostel.entity.HostelRoom;
import com.youngman.hostel.mapper.HostelBedMapper;
import com.youngman.hostel.mapper.HostelRoomMapper;
import com.youngman.hostel.service.HostelBedService;
import com.youngman.hostel.vo.HostelBedAvailableVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 床位业务实现
 */
@Service
@RequiredArgsConstructor
public class HostelBedServiceImpl implements HostelBedService {

    private final HostelBedMapper hostelBedMapper;
    private final HostelRoomMapper hostelRoomMapper;

    @Override
    public boolean saveBed(HostelBed bed) {
        if (bed == null || bed.getRoomId() == null || bed.getBedNo() == null) {
            throw new IllegalArgumentException("床位信息不完整");
        }
        HostelRoom room = hostelRoomMapper.selectOne(new LambdaQueryWrapper<HostelRoom>()
                .eq(HostelRoom::getId, bed.getRoomId())
                .eq(HostelRoom::getDeleted, 0));
        if (room == null) {
            throw new RuntimeException("所属房间不存在, roomId=" + bed.getRoomId());
        }

        bed.setCreateTime(LocalDateTime.now());
        bed.setUpdateTime(LocalDateTime.now());
        bed.setDeleted(0);
        if (bed.getStatus() == null) {
            bed.setStatus(1);
        }
        return hostelBedMapper.insert(bed) > 0;
    }

    @Override
    public HostelBed getBedById(Long bedId) {
        if (bedId == null) {
            throw new IllegalArgumentException("bedId不能为空");
        }
        HostelBed bed = hostelBedMapper.selectOne(new LambdaQueryWrapper<HostelBed>()
                .eq(HostelBed::getId, bedId)
                .eq(HostelBed::getDeleted, 0));
        if (bed == null) {
            throw new RuntimeException("床位不存在, bedId=" + bedId);
        }
        return bed;
    }

    @Override
    public List<HostelBed> listBeds() {
        return hostelBedMapper.selectList(new LambdaQueryWrapper<HostelBed>()
                .eq(HostelBed::getDeleted, 0)
                .orderByAsc(HostelBed::getRoomId)
                .orderByAsc(HostelBed::getId));
    }

    @Override
    public boolean updateBed(HostelBed bed) {
        if (bed == null || bed.getId() == null) {
            throw new IllegalArgumentException("床位更新参数非法");
        }
        getBedById(bed.getId());

        if (bed.getRoomId() != null) {
            HostelRoom room = hostelRoomMapper.selectOne(new LambdaQueryWrapper<HostelRoom>()
                    .eq(HostelRoom::getId, bed.getRoomId())
                    .eq(HostelRoom::getDeleted, 0));
            if (room == null) {
                throw new RuntimeException("所属房间不存在, roomId=" + bed.getRoomId());
            }
        }

        bed.setUpdateTime(LocalDateTime.now());
        return hostelBedMapper.updateById(bed) > 0;
    }

    @Override
    public boolean deleteBed(Long bedId) {
        getBedById(bedId);
        return hostelBedMapper.update(null, new LambdaUpdateWrapper<HostelBed>()
                .eq(HostelBed::getId, bedId)
                .eq(HostelBed::getDeleted, 0)
                .set(HostelBed::getDeleted, 1)
                .set(HostelBed::getUpdateTime, LocalDateTime.now())) > 0;
    }

    @Override
    public List<HostelBedAvailableVO> getAvailableBeds(LocalDate checkInDate, LocalDate checkOutDate) {
        validateDateRange(checkInDate, checkOutDate);
        return hostelBedMapper.selectAvailableBeds(checkInDate, checkOutDate);
    }

    @Override
    public List<HostelBedAvailableVO> getAvailableBedsByUserGender(LocalDate checkInDate, LocalDate checkOutDate, Integer userGender) {
        validateDateRange(checkInDate, checkOutDate);
        if (userGender == null || (userGender != 1 && userGender != 2)) {
            throw new IllegalArgumentException("userGender 仅支持 1-男 或 2-女");
        }
        return hostelBedMapper.selectAvailableBedsByUserGender(checkInDate, checkOutDate, userGender);
    }

    @Override
    public List<HostelBedAvailableVO> getAvailableBedsByRoomId(Long roomId, LocalDate checkInDate, LocalDate checkOutDate) {
        if (roomId == null) {
            throw new IllegalArgumentException("roomId不能为空");
        }
        validateDateRange(checkInDate, checkOutDate);
        return hostelBedMapper.selectAvailableBedsByRoomId(roomId, checkInDate, checkOutDate);
    }

    private void validateDateRange(LocalDate checkInDate, LocalDate checkOutDate) {
        if (checkInDate == null || checkOutDate == null) {
            throw new IllegalArgumentException("入住和退房日期不能为空");
        }
        if (!checkInDate.isBefore(checkOutDate)) {
            throw new IllegalArgumentException("入住日期必须早于退房日期");
        }
    }
}
