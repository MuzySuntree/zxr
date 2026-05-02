package com.youngman.hostel.dto;

import lombok.Data;

@Data
public class ChatMessageSendDTO {
    private Long sessionId;
    private Long senderId;
    private Integer senderRole;
    private String content;
}
