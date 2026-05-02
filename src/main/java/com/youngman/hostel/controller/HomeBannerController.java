package com.youngman.hostel.controller;

import com.youngman.hostel.common.ApiResponse;
import com.youngman.hostel.entity.HomeBanner;
import com.youngman.hostel.service.HomeBannerService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/banner")
@RequiredArgsConstructor
public class HomeBannerController {
    private final HomeBannerService homeBannerService;
    @GetMapping("/list") public ApiResponse<List<HomeBanner>> list(){ try{return ApiResponse.success(homeBannerService.listAll());}catch(Exception e){return ApiResponse.fail(e.getMessage());}}
    @PostMapping("/save") public ApiResponse<Long> save(@RequestBody HomeBanner b){ try{return ApiResponse.success("保存成功",homeBannerService.save(b));}catch(Exception e){return ApiResponse.fail(e.getMessage());}}
    @PutMapping("/update") public ApiResponse<Boolean> update(@RequestBody HomeBanner b){ try{return ApiResponse.success(homeBannerService.update(b));}catch(Exception e){return ApiResponse.fail(e.getMessage());}}
    @DeleteMapping("/{id}") public ApiResponse<Boolean> delete(@PathVariable Long id){ try{return ApiResponse.success(homeBannerService.delete(id));}catch(Exception e){return ApiResponse.fail(e.getMessage());}}
}
