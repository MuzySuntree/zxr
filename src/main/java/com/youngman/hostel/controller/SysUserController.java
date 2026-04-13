package com.youngman.hostel.controller;

import com.youngman.hostel.common.ApiResponse;
import com.youngman.hostel.dto.UserLoginDTO;
import com.youngman.hostel.dto.UserRegisterDTO;
import com.youngman.hostel.entity.SysUser;
import com.youngman.hostel.service.SysUserService;
import com.youngman.hostel.vo.LoginUserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户接口
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService sysUserService;

    /** 用户注册 */
    @PostMapping("/register")
    public ApiResponse<Long> register(@RequestBody UserRegisterDTO dto) {
        try {
            return ApiResponse.success("注册成功", sysUserService.register(dto));
        } catch (Exception e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    /** 用户登录 */
    @PostMapping("/login")
    public ApiResponse<LoginUserVO> login(@RequestBody UserLoginDTO dto) {
        try {
            return ApiResponse.success("登录成功", sysUserService.login(dto));
        } catch (Exception e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    /** 根据ID查询用户 */
    @GetMapping("/{id}")
    public ApiResponse<SysUser> getById(@PathVariable("id") Long id) {
        try {
            return ApiResponse.success(sysUserService.getUserById(id));
        } catch (Exception e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    /** 查询用户列表 */
    @GetMapping("/list")
    public ApiResponse<List<SysUser>> listUsers() {
        try {
            return ApiResponse.success(sysUserService.listUsers());
        } catch (Exception e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    /** 更新用户 */
    @PutMapping("/update")
    public ApiResponse<Boolean> updateUser(@RequestBody SysUser user) {
        try {
            return ApiResponse.success(sysUserService.updateUser(user));
        } catch (Exception e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    /** 删除用户 */
    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> deleteUser(@PathVariable("id") Long id) {
        try {
            return ApiResponse.success(sysUserService.deleteUser(id));
        } catch (Exception e) {
            return ApiResponse.fail(e.getMessage());
        }
    }
}
