package com.youngman.hostel.controller;

import com.youngman.hostel.common.ApiResponse;
import com.youngman.hostel.dto.UserLoginDTO;
import com.youngman.hostel.dto.UserRegisterDTO;
import com.youngman.hostel.entity.SysUser;
import com.youngman.hostel.service.RegisterVerifyCodeService;
import com.youngman.hostel.service.SysUserService;
import com.youngman.hostel.vo.LoginUserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户接口
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class SysUserController {

    private final SysUserService sysUserService;
    private final RegisterVerifyCodeService registerVerifyCodeService;

    /** 发送注册验证码 */
    @PostMapping("/register-code/send")
    public ApiResponse<Map<String, Object>> sendRegisterCode(@RequestParam("phone") String phone) {
        try {
            if (sysUserService.getUserByPhone(phone) != null) {
                return ApiResponse.fail("手机号已被注册");
            }
            String code = registerVerifyCodeService.sendRegisterCode(phone);
            Map<String, Object> data = new HashMap<>();
            data.put("phone", phone);
            data.put("verifyCode", code);
            data.put("expireMinutes", 5);
            return ApiResponse.success("验证码已生成，有效期5分钟", data);
        } catch (Exception e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    /** 校验注册验证码 */
    @GetMapping("/register-code/check")
    public ApiResponse<Boolean> checkRegisterCode(@RequestParam("phone") String phone,
                                                  @RequestParam("code") String code) {
        try {
            return ApiResponse.success(registerVerifyCodeService.checkRegisterCode(phone, code));
        } catch (Exception e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

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
