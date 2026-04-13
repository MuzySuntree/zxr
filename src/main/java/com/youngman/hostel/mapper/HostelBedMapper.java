package com.youngman.hostel.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.youngman.hostel.entity.HostelBed;
import com.youngman.hostel.vo.HostelBedAvailableVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;

@Mapper
public interface HostelBedMapper extends BaseMapper<HostelBed> {

    List<HostelBedAvailableVO> selectAvailableBeds(@Param("checkInDate") LocalDate checkInDate,
                                                   @Param("checkOutDate") LocalDate checkOutDate);

    List<HostelBedAvailableVO> selectAvailableBedsByUserGender(@Param("checkInDate") LocalDate checkInDate,
                                                               @Param("checkOutDate") LocalDate checkOutDate,
                                                               @Param("userGender") Integer userGender);

    List<HostelBedAvailableVO> selectAvailableBedsByRoomId(@Param("roomId") Long roomId,
                                                           @Param("checkInDate") LocalDate checkInDate,
                                                           @Param("checkOutDate") LocalDate checkOutDate);
}
