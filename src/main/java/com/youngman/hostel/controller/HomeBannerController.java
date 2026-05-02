package com.youngman.hostel.controller;

import com.youngman.hostel.common.ApiResponse;
import com.youngman.hostel.entity.HomeBanner;
import com.youngman.hostel.service.HomeBannerService;
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
@RequestMapping("/banner")
@RequiredArgsConstructor
public class HomeBannerController {
    private static final long MAX_IMAGE_SIZE = 5 * 1024 * 1024;
    private static final Set<String> ALLOWED_EXTENSIONS = Set.of("jpg", "jpeg", "png", "webp");

    private final HomeBannerService homeBannerService;

    @GetMapping("/list")
    public ApiResponse<List<HomeBanner>> list() {
        try {
            return ApiResponse.success(homeBannerService.listAll());
        } catch (Exception e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    @PostMapping("/save")
    public ApiResponse<Long> save(@RequestBody HomeBanner banner) {
        try {
            return ApiResponse.success("保存成功", homeBannerService.save(banner));
        } catch (Exception e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    @PutMapping("/update")
    public ApiResponse<Boolean> update(@RequestBody HomeBanner banner) {
        try {
            return ApiResponse.success(homeBannerService.update(banner));
        } catch (Exception e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        try {
            return ApiResponse.success(homeBannerService.delete(id));
        } catch (Exception e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    @PostMapping("/upload-image")
    public ApiResponse<String> uploadBannerImage(@RequestParam("file") MultipartFile file) {
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
            Path uploadDir = Paths.get("uploads", "banner-images");
            Files.createDirectories(uploadDir);
            Files.copy(file.getInputStream(), uploadDir.resolve(filename), StandardCopyOption.REPLACE_EXISTING);

            return ApiResponse.success("http://127.0.0.1:8080/uploads/banner-images/" + filename);
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
