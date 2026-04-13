package com.youngman.hostel.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.youngman.hostel.entity.HostelRoom;
import com.youngman.hostel.mapper.HostelRoomMapper;
import com.youngman.hostel.service.HostelRoomService;
import com.youngman.hostel.vo.RoomOccupancyGenderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 房间业务实现
 */
@Service
@RequiredArgsConstructor
public class HostelRoomServiceImpl implements HostelRoomService {

    private final HostelRoomMapper hostelRoomMapper;

    @Override
    public boolean saveRoom(HostelRoom room) {
        if (room == null || room.getRoomNo() == null) {
            throw new IllegalArgumentException("房间信息不完整");
        }
        room.setCreateTime(LocalDateTime.now());
        room.setUpdateTime(LocalDateTime.now());
        room.setDeleted(0);
        return hostelRoomMapper.insert(room) > 0;
    }

    @Override
    public HostelRoom getRoomById(Long roomId) {
        if (roomId == null) {
            throw new IllegalArgumentException("roomId不能为空");
        }
        HostelRoom room = hostelRoomMapper.selectOne(new LambdaQueryWrapper<HostelRoom>()
                .eq(HostelRoom::getId, roomId)
                .eq(HostelRoom::getDeleted, 0));
        if (room == null) {
            throw new RuntimeException("房间不存在, id=" + roomId);
        }
        return room;
    }

    @Override
    public List<HostelRoom> listRooms() {
        return hostelRoomMapper.selectList(new LambdaQueryWrapper<HostelRoom>()
                .eq(HostelRoom::getDeleted, 0)
                .orderByAsc(HostelRoom::getId));
    }

    @Override
    public boolean updateRoom(HostelRoom room) {
        if (room == null || room.getId() == null) {
            throw new IllegalArgumentException("房间更新参数非法");
        }
        getRoomById(room.getId());
        room.setUpdateTime(LocalDateTime.now());
        return hostelRoomMapper.updateById(room) > 0;
    }

    @Override
    public boolean deleteRoom(Long roomId) {
        getRoomById(roomId);
        return hostelRoomMapper.update(null, new LambdaUpdateWrapper<HostelRoom>()
                .eq(HostelRoom::getId, roomId)
                .eq(HostelRoom::getDeleted, 0)
                .set(HostelRoom::getDeleted, 1)
                .set(HostelRoom::getUpdateTime, LocalDateTime.now())) > 0;
    }

    @Override
    public RoomOccupancyGenderVO getRoomOccupancyGender(Long roomId, LocalDate checkInDate, LocalDate checkOutDate) {
        if (roomId == null) {
            throw new IllegalArgumentException("roomId不能为空");
        }
        if (checkInDate == null || checkOutDate == null || !checkInDate.isBefore(checkOutDate)) {
            throw new IllegalArgumentException("入住/退房日期不合法");
        }

        HostelRoom room = getRoomById(roomId);
        RoomOccupancyGenderVO vo = hostelRoomMapper.selectRoomOccupancyGender(roomId, checkInDate, checkOutDate);
        if (vo == null) {
            vo = new RoomOccupancyGenderVO();
            vo.setRoomId(room.getId());
            vo.setRoomNo(room.getRoomNo());
            vo.setRoomName(room.getRoomName());
            vo.setOccupiedCount(0);
            vo.setMaleCount(0);
            vo.setFemaleCount(0);
            return vo;
        }
        vo.setOccupiedCount(vo.getOccupiedCount() == null ? 0 : vo.getOccupiedCount());
        vo.setMaleCount(vo.getMaleCount() == null ? 0 : vo.getMaleCount());
        vo.setFemaleCount(vo.getFemaleCount() == null ? 0 : vo.getFemaleCount());
        return vo;
    }
}
