package com.youngman.hostel.controller;

import com.youngman.hostel.common.ApiResponse;
import com.youngman.hostel.entity.HostelRoom;
import com.youngman.hostel.service.HostelRoomService;
import com.youngman.hostel.vo.RoomOccupancyGenderVO;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * 房间接口
 */
@RestController
@RequestMapping("/room")
@RequiredArgsConstructor
public class HostelRoomController {

    private final HostelRoomService hostelRoomService;

    /** 新增房间 */
    @PostMapping("/save")
    public ApiResponse<Boolean> saveRoom(@RequestBody HostelRoom room) {
        try {
            return ApiResponse.success(hostelRoomService.saveRoom(room));
        } catch (Exception e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    /** 查询房间详情 */
    @GetMapping("/{roomId}")
    public ApiResponse<HostelRoom> getRoomById(@PathVariable Long roomId) {
        try {
            return ApiResponse.success(hostelRoomService.getRoomById(roomId));
        } catch (Exception e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    /** 查询房间列表 */
    @GetMapping("/list")
    public ApiResponse<List<HostelRoom>> listRooms() {
        try {
            return ApiResponse.success(hostelRoomService.listRooms());
        } catch (Exception e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    /** 更新房间 */
    @PutMapping("/update")
    public ApiResponse<Boolean> updateRoom(@RequestBody HostelRoom room) {
        try {
            return ApiResponse.success(hostelRoomService.updateRoom(room));
        } catch (Exception e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    /** 删除房间 */
    @DeleteMapping("/{roomId}")
    public ApiResponse<Boolean> deleteRoom(@PathVariable Long roomId) {
        try {
            return ApiResponse.success(hostelRoomService.deleteRoom(roomId));
        } catch (Exception e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    /** 房间图片上传 */
    @PostMapping("/upload-image")
    public ApiResponse<String> uploadRoomImage(@RequestParam("file") MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                return ApiResponse.fail("请选择图片文件");
            }
            String original = file.getOriginalFilename();
            String ext = original != null && original.contains(".") ? original.substring(original.lastIndexOf('.')) : ".jpg";
            String filename = UUID.randomUUID().toString().replace("-", "") + ext;

            Path uploadDir = Paths.get("uploads", "room-images");
            Files.createDirectories(uploadDir);
            Files.copy(file.getInputStream(), uploadDir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);

            return ApiResponse.success("http://127.0.0.1:8080/uploads/room-images/" + filename);
        } catch (IOException e) {
            return ApiResponse.fail("上传失败: " + e.getMessage());
        }
    }

    /** 查询房间在指定时间段内入住及性别统计 */
    @GetMapping("/occupancy")
    public ApiResponse<RoomOccupancyGenderVO> getRoomOccupancy(
            @RequestParam Long roomId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate checkInDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate checkOutDate) {
        try {
            return ApiResponse.success(hostelRoomService.getRoomOccupancyGender(roomId, checkInDate, checkOutDate));
        } catch (Exception e) {
            return ApiResponse.fail(e.getMessage());
        }
    }
}
