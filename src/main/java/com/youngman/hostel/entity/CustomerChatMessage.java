package com.youngman.hostel.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@TableName("customer_chat_message")
public class CustomerChatMessage {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;
    private Long sessionId;
    private Long senderId;
    private Integer senderRole;
    private String content;
    private Integer isRead;
    private LocalDateTime createTime;
}
