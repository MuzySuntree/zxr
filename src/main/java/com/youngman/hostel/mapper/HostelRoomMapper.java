package com.youngman.hostel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youngman.hostel.entity.HostelRoom;
import com.youngman.hostel.vo.RoomOccupancyGenderVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;

@Mapper
public interface HostelRoomMapper extends BaseMapper<HostelRoom> {

    RoomOccupancyGenderVO selectRoomOccupancyGender(@Param("roomId") Long roomId,
                                                    @Param("checkInDate") LocalDate checkInDate,
                                                    @Param("checkOutDate") LocalDate checkOutDate);
}
