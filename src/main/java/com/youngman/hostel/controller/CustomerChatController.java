package com.youngman.hostel.controller;

import com.youngman.hostel.common.ApiResponse;
import com.youngman.hostel.dto.ChatMessageSendDTO;
import com.youngman.hostel.entity.CustomerChatMessage;
import com.youngman.hostel.entity.CustomerChatSession;
import com.youngman.hostel.service.CustomerChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/chat")
@RequiredArgsConstructor
public class CustomerChatController {

    private final CustomerChatService customerChatService;

    @PostMapping("/session/open")
    public ApiResponse<CustomerChatSession> open(@RequestParam Long userId) {
        try { return ApiResponse.success(customerChatService.openSession(userId)); }
        catch (Exception e) { return ApiResponse.fail(e.getMessage()); }
    }

    @GetMapping("/session/user/{userId}")
    public ApiResponse<List<CustomerChatSession>> userSessions(@PathVariable Long userId) {
        try { return ApiResponse.success(customerChatService.listUserSessions(userId)); }
        catch (Exception e) { return ApiResponse.fail(e.getMessage()); }
    }

    @GetMapping("/session/list")
    public ApiResponse<List<CustomerChatSession>> allSessions() {
        try { return ApiResponse.success(customerChatService.listAllSessions()); }
        catch (Exception e) { return ApiResponse.fail(e.getMessage()); }
    }

    @PutMapping("/session/assign")
    public ApiResponse<Boolean> assign(@RequestParam Long sessionId, @RequestParam Long adminId) {
        try { return ApiResponse.success(customerChatService.assignSession(sessionId, adminId)); }
        catch (Exception e) { return ApiResponse.fail(e.getMessage()); }
    }

    @PutMapping("/session/close/{sessionId}")
    public ApiResponse<Boolean> close(@PathVariable Long sessionId) {
        try { return ApiResponse.success(customerChatService.closeSession(sessionId)); }
        catch (Exception e) { return ApiResponse.fail(e.getMessage()); }
    }

    @PostMapping("/message/send")
    public ApiResponse<Boolean> send(@RequestBody ChatMessageSendDTO dto) {
        try { return ApiResponse.success(customerChatService.sendMessage(dto)); }
        catch (Exception e) { return ApiResponse.fail(e.getMessage()); }
    }

    @GetMapping("/message/list")
    public ApiResponse<List<CustomerChatMessage>> list(@RequestParam Long sessionId) {
        try { return ApiResponse.success(customerChatService.listMessages(sessionId)); }
        catch (Exception e) { return ApiResponse.fail(e.getMessage()); }
    }

    @PutMapping("/message/read")
    public ApiResponse<Boolean> read(@RequestParam Long sessionId, @RequestParam Integer readerRole) {
        try { return ApiResponse.success(customerChatService.markRead(sessionId, readerRole)); }
        catch (Exception e) { return ApiResponse.fail(e.getMessage()); }
    }
}
