package com.youngman.hostel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("hostel_activity_signup")
public class HostelActivitySignup {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long activityId;
    private Long userId;
    private Integer signupStatus;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
