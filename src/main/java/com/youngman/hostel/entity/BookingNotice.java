package com.youngman.hostel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 订单通知实体
 */
@Data
@TableName("booking_notice")
public class BookingNotice {

    /** 主键ID */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /** 接收用户ID */
    private Long userId;

    /** 关联订单ID */
    private Long orderId;

    /** 通知标题 */
    private String title;

    /** 通知内容 */
    private String content;

    /** 是否已读：0-未读，1-已读 */
    private Integer isRead;

    /** 创建时间 */
    private LocalDateTime createTime;

    /** 更新时间 */
    private LocalDateTime updateTime;
}
