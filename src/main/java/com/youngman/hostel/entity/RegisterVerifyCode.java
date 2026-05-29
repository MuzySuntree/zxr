package com.youngman.hostel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 注册验证码表
 */
@Data
@TableName("register_verify_code")
public class RegisterVerifyCode {

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String phone;

    private String code;

    private LocalDateTime expireTime;

    private Integer used;

    private LocalDateTime createTime;

    private LocalDateTime updateTime;
}
