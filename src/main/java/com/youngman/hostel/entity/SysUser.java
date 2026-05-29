package com.youngman.hostel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableLogic;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 用户表实体
 */
@Data
@TableName("sys_user")
public class SysUser {

    /** 主键ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 用户名 */
    private String username;

    /** 密码（建议存储哈希） */
    private String password;

    /** 真实姓名 */
    private String realName;

    /** 性别：1-男，2-女，0-未知 */
    private Integer gender;

    /** 手机号 */
    private String phone;

    /** 头像URL */
    private String avatar;

    /** 邮箱 */
    private String email;

    /** 身份证号 */
    private String idCard;

    /** 紧急联系人 */
    private String emergencyContact;

    /** 紧急联系人手机号 */
    private String emergencyPhone;

    /** 备注 */
    private String remark;

    /** 角色：1-管理员，2-普通用户 */
    private Integer role;

    /** 状态：1-启用，0-禁用 */
    private Integer status;

    /** 最后登录时间 */
    private LocalDateTime lastLoginTime;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;

    /** 逻辑删除：0-未删除，1-已删除 */
    @TableLogic
    private Integer deleted;
}
