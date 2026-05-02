package com.youngman.hostel.service;

import com.youngman.hostel.dto.ChatMessageSendDTO;
import com.youngman.hostel.entity.CustomerChatMessage;
import com.youngman.hostel.entity.CustomerChatSession;

import java.util.List;

public interface CustomerChatService {
    CustomerChatSession openSession(Long userId);
    List<CustomerChatSession> listUserSessions(Long userId);
    List<CustomerChatSession> listAllSessions();
    boolean assignSession(Long sessionId, Long adminId);
    boolean closeSession(Long sessionId);
    boolean sendMessage(ChatMessageSendDTO dto);
    List<CustomerChatMessage> listMessages(Long sessionId);
    boolean markRead(Long sessionId, Integer readerRole);
}
