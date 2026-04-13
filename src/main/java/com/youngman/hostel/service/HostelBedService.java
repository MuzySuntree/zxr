package com.youngman.hostel.service;

import com.youngman.hostel.entity.HostelBed;
import com.youngman.hostel.vo.HostelBedAvailableVO;

import java.time.LocalDate;
import java.util.List;

/**
 * 床位业务接口
 */
public interface HostelBedService {

    /**
     * 新增床位
     */
    boolean saveBed(HostelBed bed);

    /**
     * 根据ID查询床位
     */
    HostelBed getBedById(Long bedId);

    /**
     * 查询床位列表
     */
    List<HostelBed> listBeds();

    /**
     * 更新床位
     */
    boolean updateBed(HostelBed bed);

    /**
     * 删除床位（逻辑删除）
     */
    boolean deleteBed(Long bedId);

    /**
     * 查询指定时间段内所有可用床位
     */
    List<HostelBedAvailableVO> getAvailableBeds(LocalDate checkInDate, LocalDate checkOutDate);

    /**
     * 按用户性别查询指定时间段内可用床位
     */
    List<HostelBedAvailableVO> getAvailableBedsByUserGender(LocalDate checkInDate, LocalDate checkOutDate, Integer userGender);

    /**
     * 查询某个房间在指定时间段内可用床位
     */
    List<HostelBedAvailableVO> getAvailableBedsByRoomId(Long roomId, LocalDate checkInDate, LocalDate checkOutDate);
}
