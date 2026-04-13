package com.youngman.hostel.dto;

import lombok.Data;

/**
 * 用户注册参数
 */
@Data
public class UserRegisterDTO {

    /** 用户名 */
    private String username;

    /** 明文密码（serviceImpl 中应转加密后保存） */
    private String password;

    /** 真实姓名 */
    private String realName;

    /** 性别：1-男，2-女，0-未知 */
    private Integer gender;

    /** 手机号 */
    private String phone;

    /** 角色：默认普通用户2；管理员可由后台指定1 */
    private Integer role;
}
