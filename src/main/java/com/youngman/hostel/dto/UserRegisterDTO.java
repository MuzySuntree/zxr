package com.youngman.hostel.dto;

import lombok.Data;

/**
 * 用户注册参数
 */
@Data
public class UserRegisterDTO {

    /** 用户名 */
    private String username;

    /** 明文密码（毕设演示场景可明文存储） */
    private String password;

    /** 确认密码 */
    private String confirmPassword;

    /** 真实姓名 */
    private String realName;

    /** 性别：1-男，2-女 */
    private Integer gender;

    /** 手机号 */
    private String phone;

    /** 注册验证码 */
    private String verifyCode;
}
