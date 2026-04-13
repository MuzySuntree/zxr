package com.youngman.hostel.controller;

import com.youngman.hostel.common.ApiResponse;
import com.youngman.hostel.entity.HostelBed;
import com.youngman.hostel.service.HostelBedService;
import com.youngman.hostel.vo.HostelBedAvailableVO;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * 床位接口
 */
@RestController
@RequestMapping("/bed")
@RequiredArgsConstructor
public class HostelBedController {

    private final HostelBedService hostelBedService;

    /** 新增床位 */
    @PostMapping("/save")
    public ApiResponse<Boolean> saveBed(@RequestBody HostelBed bed) {
        try {
            return ApiResponse.success(hostelBedService.saveBed(bed));
        } catch (Exception e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    /** 查询床位详情 */
    @GetMapping("/{bedId}")
    public ApiResponse<HostelBed> getBedById(@PathVariable Long bedId) {
        try {
            return ApiResponse.success(hostelBedService.getBedById(bedId));
        } catch (Exception e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    /** 查询床位列表 */
    @GetMapping("/list")
    public ApiResponse<List<HostelBed>> listBeds() {
        try {
            return ApiResponse.success(hostelBedService.listBeds());
        } catch (Exception e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    /** 更新床位 */
    @PutMapping("/update")
    public ApiResponse<Boolean> updateBed(@RequestBody HostelBed bed) {
        try {
            return ApiResponse.success(hostelBedService.updateBed(bed));
        } catch (Exception e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    /** 删除床位 */
    @DeleteMapping("/{bedId}")
    public ApiResponse<Boolean> deleteBed(@PathVariable Long bedId) {
        try {
            return ApiResponse.success(hostelBedService.deleteBed(bedId));
        } catch (Exception e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    /** 查询指定时间段内所有可用床位 */
    @GetMapping("/available")
    public ApiResponse<List<HostelBedAvailableVO>> getAvailableBeds(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate checkInDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate checkOutDate) {
        try {
            return ApiResponse.success(hostelBedService.getAvailableBeds(checkInDate, checkOutDate));
        } catch (Exception e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    /** 按用户性别查询指定时间段内可用床位 */
    @GetMapping("/available/by-gender")
    public ApiResponse<List<HostelBedAvailableVO>> getAvailableBedsByGender(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate checkInDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate checkOutDate,
            @RequestParam Integer userGender) {
        try {
            return ApiResponse.success(hostelBedService.getAvailableBedsByUserGender(checkInDate, checkOutDate, userGender));
        } catch (Exception e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    /** 查询某房间在指定时间段内可用床位 */
    @GetMapping("/available/by-room")
    public ApiResponse<List<HostelBedAvailableVO>> getAvailableBedsByRoom(
            @RequestParam Long roomId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate checkInDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate checkOutDate) {
        try {
            return ApiResponse.success(hostelBedService.getAvailableBedsByRoomId(roomId, checkInDate, checkOutDate));
        } catch (Exception e) {
            return ApiResponse.fail(e.getMessage());
        }
    }
}
