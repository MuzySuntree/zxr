package com.youngman.hostel.controller;

import com.youngman.hostel.common.ApiResponse;
import com.youngman.hostel.entity.MessageBoard;
import com.youngman.hostel.service.MessageBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/message")
@RequiredArgsConstructor
public class MessageBoardController {
    private final MessageBoardService messageBoardService;

    @GetMapping("/list")
    public ApiResponse<List<MessageBoard>> list() {
        try {
            return ApiResponse.success(messageBoardService.listPublished());
        } catch (Exception e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    @GetMapping("/admin/list")
    public ApiResponse<List<MessageBoard>> adminList() {
        try {
            return ApiResponse.success(messageBoardService.listAdminAll());
        } catch (Exception e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    @PostMapping("/save")
    public ApiResponse<Long> save(@RequestBody MessageBoard message) {
        try {
            return ApiResponse.success("发布成功", messageBoardService.save(message));
        } catch (Exception e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    @PutMapping("/audit/{id}")
    public ApiResponse<Boolean> audit(@PathVariable Long id, @RequestParam Integer status) {
        try {
            return ApiResponse.success(messageBoardService.audit(id, status));
        } catch (Exception e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Boolean> delete(@PathVariable Long id) {
        try {
            return ApiResponse.success(messageBoardService.delete(id));
        } catch (Exception e) {
            return ApiResponse.fail(e.getMessage());
        }
    }
}
