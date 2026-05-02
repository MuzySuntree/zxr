package com.youngman.hostel.controller;

import com.youngman.hostel.common.ApiResponse;
import com.youngman.hostel.entity.UserPaymentSetting;
import com.youngman.hostel.service.UserPaymentSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class UserPaymentSettingController {

    private final UserPaymentSettingService userPaymentSettingService;

    @GetMapping("/user/{userId}")
    public ApiResponse<List<UserPaymentSetting>> list(@PathVariable Long userId) {
        try { return ApiResponse.success(userPaymentSettingService.listByUserId(userId)); }
        catch (Exception e) { return ApiResponse.fail(e.getMessage()); }
    }

    @PostMapping("/save")
    public ApiResponse<Long> save(@RequestBody UserPaymentSetting setting) {
        try { return ApiResponse.success("保存成功", userPaymentSettingService.save(setting)); }
        catch (Exception e) { return ApiResponse.fail(e.getMessage()); }
    }

    @PutMapping("/update")
    public ApiResponse<Boolean> update(@RequestBody UserPaymentSetting setting) {
        try { return ApiResponse.success(userPaymentSettingService.update(setting)); }
        catch (Exception e) { return ApiResponse.fail(e.getMessage()); }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        try { return ApiResponse.success(userPaymentSettingService.delete(id)); }
        catch (Exception e) { return ApiResponse.fail(e.getMessage()); }
    }

    @PutMapping("/default/{id}")
    public ApiResponse<Boolean> setDefault(@PathVariable Long id) {
        try { return ApiResponse.success(userPaymentSettingService.setDefault(id)); }
        catch (Exception e) { return ApiResponse.fail(e.getMessage()); }
    }
}
