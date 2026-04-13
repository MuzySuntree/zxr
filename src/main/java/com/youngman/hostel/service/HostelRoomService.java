package com.youngman.hostel.service;

import com.youngman.hostel.entity.HostelRoom;
import com.youngman.hostel.vo.RoomOccupancyGenderVO;

import java.time.LocalDate;
import java.util.List;

/**
 * 房间业务接口
 */
public interface HostelRoomService {

    /**
     * 新增房间
     */
    boolean saveRoom(HostelRoom room);

    /**
     * 根据ID查询房间
     */
    HostelRoom getRoomById(Long roomId);

    /**
     * 查询房间列表
     */
    List<HostelRoom> listRooms();

    /**
     * 更新房间
     */
    boolean updateRoom(HostelRoom room);

    /**
     * 删除房间（逻辑删除）
     */
    boolean deleteRoom(Long roomId);

    /**
     * 查询某房间在指定时间段内的入住人数与性别统计
     */
    RoomOccupancyGenderVO getRoomOccupancyGender(Long roomId, LocalDate checkInDate, LocalDate checkOutDate);
}
