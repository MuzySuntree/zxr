package com.youngman.hostel.vo;

import lombok.Data;

/**
 * 登录用户信息
 */
@Data
public class LoginUserVO {

    /** 用户ID */
    private Long userId;

    /** 用户名 */
    private String username;

    /** 真实姓名 */
    private String realName;

    /** 性别 */
    private Integer gender;

    /** 角色 */
    private Integer role;

    /** 登录令牌（可先返回 mockToken） */
    private String token;
}
