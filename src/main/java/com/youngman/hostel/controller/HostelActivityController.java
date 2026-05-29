package com.youngman.hostel.controller;

import com.youngman.hostel.common.ApiResponse;
import com.youngman.hostel.entity.HostelActivity;
import com.youngman.hostel.service.HostelActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RestController
@RequestMapping("/activity")
@RequiredArgsConstructor
public class HostelActivityController {
    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");

    private final HostelActivityService hostelActivityService;

    @GetMapping("/list")
    public ApiResponse<List<HostelActivity>> list() {
        try {
            return ApiResponse.success(hostelActivityService.listAll());
        } catch (Exception e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    @PostMapping("/save")
    public ApiResponse<Long> save(@RequestBody HostelActivity activity) {
        try {
            return ApiResponse.success("保存成功", hostelActivityService.save(activity));
        } catch (Exception e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    @PutMapping("/update")
    public ApiResponse<Boolean> update(@RequestBody HostelActivity activity) {
        try {
            return ApiResponse.success(hostelActivityService.update(activity));
        } catch (Exception e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        try {
            return ApiResponse.success(hostelActivityService.delete(id));
        } catch (Exception e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    @PutMapping("/recommend/{id}")
    public ApiResponse<Boolean> recommend(@PathVariable Long id, @RequestParam Integer recommend) {
        try {
            return ApiResponse.success(hostelActivityService.setRecommend(id, recommend));
        } catch (Exception e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    @PostMapping("/upload-image")
    public ApiResponse<String> uploadActivityImage(@RequestParam("file") MultipartFile file) {
        try {
            if (file == null || file.isEmpty()) {
                return ApiResponse.fail("请选择图片文件");
            }
            if (file.getSize() > MAX_IMAGE_SIZE) {
                return ApiResponse.fail("图片大小不能超过5MB");
            }
            String originalName = file.getOriginalFilename();
            String extension = getFileExtension(originalName);
            if (!StringUtils.hasText(extension) || !ALLOWED_EXTENSIONS.contains(extension.toLowerCase())) {
                return ApiResponse.fail("仅支持上传 jpg、jpeg、png、webp 格式图片");
            }

            String filename = UUID.randomUUID().toString().replace("-", "") + "." + extension.toLowerCase();
            Path uploadDir = Paths.get("uploads", "activity-images");
            Files.createDirectories(uploadDir);
            Files.copy(file.getInputStream(), uploadDir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);

            return ApiResponse.success("http://127.0.0.1:8080/uploads/activity-images/" + filename);
        } catch (IOException e) {
            return ApiResponse.fail("上传失败: " + e.getMessage());
        }
    }

    private String getFileExtension(String filename) {
        if (!StringUtils.hasText(filename) || !filename.contains(".")) {
            return "";
        }
        return filename.substring(filename.lastIndexOf('.') + 1);
    }
}
