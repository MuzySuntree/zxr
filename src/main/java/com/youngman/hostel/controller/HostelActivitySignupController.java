package com.youngman.hostel.controller;

import com.youngman.hostel.common.ApiResponse;
import com.youngman.hostel.entity.HostelActivity;
import com.youngman.hostel.entity.HostelActivitySignup;
import com.youngman.hostel.service.HostelActivitySignupService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/activity/signup")
@RequiredArgsConstructor
public class HostelActivitySignupController {

    private final HostelActivitySignupService hostelActivitySignupService;

    @PostMapping
    public ApiResponse<Boolean> signup(@RequestParam Long activityId, @RequestParam Long userId) {
        try { return ApiResponse.success(hostelActivitySignupService.signup(activityId, userId)); }
        catch (Exception e) { return ApiResponse.fail(e.getMessage()); }
    }

    @PutMapping("/cancel")
    public ApiResponse<Boolean> cancel(@RequestParam Long activityId, @RequestParam Long userId) {
        try { return ApiResponse.success(hostelActivitySignupService.cancelSignup(activityId, userId)); }
        catch (Exception e) { return ApiResponse.fail(e.getMessage()); }
    }

    @GetMapping("/list")
    public ApiResponse<List<HostelActivitySignup>> listByActivity(@RequestParam Long activityId) {
        try { return ApiResponse.success(hostelActivitySignupService.listByActivityId(activityId)); }
        catch (Exception e) { return ApiResponse.fail(e.getMessage()); }
    }

    @GetMapping("/user/{userId}")
    public ApiResponse<List<HostelActivity>> listByUser(@PathVariable Long userId) {
        try { return ApiResponse.success(hostelActivitySignupService.listUserSignedActivities(userId)); }
        catch (Exception e) { return ApiResponse.fail(e.getMessage()); }
    }
}
