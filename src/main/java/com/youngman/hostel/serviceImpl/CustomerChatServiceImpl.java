package com.youngman.hostel.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.youngman.hostel.dto.ChatMessageSendDTO;
import com.youngman.hostel.entity.CustomerChatMessage;
import com.youngman.hostel.entity.CustomerChatSession;
import com.youngman.hostel.mapper.CustomerChatMessageMapper;
import com.youngman.hostel.mapper.CustomerChatSessionMapper;
import com.youngman.hostel.service.CustomerChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomerChatServiceImpl implements CustomerChatService {

    private final CustomerChatSessionMapper customerChatSessionMapper;
    private final CustomerChatMessageMapper customerChatMessageMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomerChatSession openSession(Long userId) {
        if (userId == null) throw new IllegalArgumentException("userId不能为空");
        CustomerChatSession existed = customerChatSessionMapper.selectOne(new LambdaQueryWrapper<CustomerChatSession>()
                .eq(CustomerChatSession::getUserId, userId)
                .eq(CustomerChatSession::getStatus, 1)
                .orderByDesc(CustomerChatSession::getId)
                .last("limit 1"));
        if (existed != null) return existed;

        CustomerChatSession session = new CustomerChatSession();
        session.setUserId(userId);
        session.setStatus(1);
        session.setCreateTime(LocalDateTime.now());
        session.setUpdateTime(LocalDateTime.now());
        if (customerChatSessionMapper.insert(session) <= 0) throw new RuntimeException("创建会话失败");
        return session;
    }

    @Override
    public List<CustomerChatSession> listUserSessions(Long userId) {
        if (userId == null) throw new IllegalArgumentException("userId不能为空");
        return customerChatSessionMapper.selectList(new LambdaQueryWrapper<CustomerChatSession>()
                .eq(CustomerChatSession::getUserId, userId)
                .orderByDesc(CustomerChatSession::getLastMessageTime)
                .orderByDesc(CustomerChatSession::getId));
    }

    @Override
    public List<CustomerChatSession> listAllSessions() {
        return customerChatSessionMapper.selectList(new LambdaQueryWrapper<CustomerChatSession>()
                .orderByDesc(CustomerChatSession::getLastMessageTime)
                .orderByDesc(CustomerChatSession::getId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean assignSession(Long sessionId, Long adminId) {
        CustomerChatSession session = getSession(sessionId);
        if (adminId == null) throw new IllegalArgumentException("adminId不能为空");
        return customerChatSessionMapper.update(null, new LambdaUpdateWrapper<CustomerChatSession>()
                .eq(CustomerChatSession::getId, session.getId())
                .set(CustomerChatSession::getAdminId, adminId)
                .set(CustomerChatSession::getUpdateTime, LocalDateTime.now())) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean closeSession(Long sessionId) {
        CustomerChatSession session = getSession(sessionId);
        return customerChatSessionMapper.update(null, new LambdaUpdateWrapper<CustomerChatSession>()
                .eq(CustomerChatSession::getId, session.getId())
                .set(CustomerChatSession::getStatus, 2)
                .set(CustomerChatSession::getUpdateTime, LocalDateTime.now())) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean sendMessage(ChatMessageSendDTO dto) {
        validateSendDTO(dto);
        CustomerChatSession session = getSession(dto.getSessionId());
        if (session.getStatus() != 1) throw new RuntimeException("已关闭会话不能发送消息");

        if (dto.getSenderRole() == 1) {
            if (!dto.getSenderId().equals(session.getUserId())) throw new RuntimeException("用户只能在自己的会话发送消息");
        } else {
            if (session.getAdminId() == null) {
                customerChatSessionMapper.update(null, new LambdaUpdateWrapper<CustomerChatSession>()
                        .eq(CustomerChatSession::getId, session.getId())
                        .set(CustomerChatSession::getAdminId, dto.getSenderId())
                        .set(CustomerChatSession::getUpdateTime, LocalDateTime.now()));
            }
        }

        CustomerChatMessage message = new CustomerChatMessage();
        message.setSessionId(dto.getSessionId());
        message.setSenderId(dto.getSenderId());
        message.setSenderRole(dto.getSenderRole());
        message.setContent(dto.getContent().trim());
        message.setIsRead(0);
        message.setCreateTime(LocalDateTime.now());
        if (customerChatMessageMapper.insert(message) <= 0) throw new RuntimeException("发送消息失败");

        customerChatSessionMapper.update(null, new LambdaUpdateWrapper<CustomerChatSession>()
                .eq(CustomerChatSession::getId, dto.getSessionId())
                .set(CustomerChatSession::getLastMessage, message.getContent())
                .set(CustomerChatSession::getLastMessageTime, message.getCreateTime())
                .set(CustomerChatSession::getUpdateTime, LocalDateTime.now()));
        return true;
    }

    @Override
    public List<CustomerChatMessage> listMessages(Long sessionId) {
        getSession(sessionId);
        return customerChatMessageMapper.selectList(new LambdaQueryWrapper<CustomerChatMessage>()
                .eq(CustomerChatMessage::getSessionId, sessionId)
                .orderByAsc(CustomerChatMessage::getCreateTime));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean markRead(Long sessionId, Integer readerRole) {
        getSession(sessionId);
        if (readerRole == null || (readerRole != 1 && readerRole != 2)) throw new IllegalArgumentException("readerRole仅支持1或2");
        int targetRole = readerRole == 1 ? 2 : 1;
        return customerChatMessageMapper.update(null, new LambdaUpdateWrapper<CustomerChatMessage>()
                .eq(CustomerChatMessage::getSessionId, sessionId)
                .eq(CustomerChatMessage::getSenderRole, targetRole)
                .eq(CustomerChatMessage::getIsRead, 0)
                .set(CustomerChatMessage::getIsRead, 1)) >= 0;
    }

    private CustomerChatSession getSession(Long sessionId) {
        if (sessionId == null) throw new IllegalArgumentException("sessionId不能为空");
        CustomerChatSession session = customerChatSessionMapper.selectById(sessionId);
        if (session == null) throw new RuntimeException("会话不存在");
        return session;
    }

    private void validateSendDTO(ChatMessageSendDTO dto) {
        if (dto == null) throw new IllegalArgumentException("发送参数不能为空");
        if (dto.getSessionId() == null || dto.getSenderId() == null) throw new IllegalArgumentException("sessionId和senderId不能为空");
        if (dto.getSenderRole() == null || (dto.getSenderRole() != 1 && dto.getSenderRole() != 2)) throw new IllegalArgumentException("senderRole仅支持1用户或2管理员");
        if (dto.getContent() == null || dto.getContent().trim().isEmpty()) throw new IllegalArgumentException("消息内容不能为空");
        if (dto.getContent().trim().length() > 4000) throw new IllegalArgumentException("消息内容不能超过4000字符");
    }
}
