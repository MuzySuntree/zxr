package com.youngman.hostel.controller;

import com.youngman.hostel.common.ApiResponse;
import com.youngman.hostel.entity.HostelActivity;
import com.youngman.hostel.service.HostelActivityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/activity")
@RequiredArgsConstructor
public class HostelActivityController {
    private final HostelActivityService hostelActivityService;
    @GetMapping("/list") public ApiResponse<List<HostelActivity>> list(){ try{return ApiResponse.success(hostelActivityService.listAll());}catch(Exception e){return ApiResponse.fail(e.getMessage());}}
    @PostMapping("/save") public ApiResponse<Long> save(@RequestBody HostelActivity a){ try{return ApiResponse.success("保存成功",hostelActivityService.save(a));}catch(Exception e){return ApiResponse.fail(e.getMessage());}}
    @PutMapping("/update") public ApiResponse<Boolean> update(@RequestBody HostelActivity a){ try{return ApiResponse.success(hostelActivityService.update(a));}catch(Exception e){return ApiResponse.fail(e.getMessage());}}
    @DeleteMapping("/{id}") public ApiResponse<Boolean> delete(@PathVariable Long id){ try{return ApiResponse.success(hostelActivityService.delete(id));}catch(Exception e){return ApiResponse.fail(e.getMessage());}}
    @PutMapping("/recommend/{id}") public ApiResponse<Boolean> recommend(@PathVariable Long id,@RequestParam Integer recommend){ try{return ApiResponse.success(hostelActivityService.setRecommend(id,recommend));}catch(Exception e){return ApiResponse.fail(e.getMessage());}}
}
