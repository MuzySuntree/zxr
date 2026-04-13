package com.youngman.hostel.controller;

import com.youngman.hostel.common.ApiResponse;
import com.youngman.hostel.entity.BookingNotice;
import com.youngman.hostel.service.BookingNoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 通知接口
 */
@RestController
@RequestMapping("/notice")
@RequiredArgsConstructor
public class BookingNoticeController {

    private final BookingNoticeService bookingNoticeService;

    /** 创建通知 */
    @PostMapping("/save")
    public ApiResponse<Long> saveNotice(@RequestBody BookingNotice notice) {
        try {
            return ApiResponse.success("通知创建成功", bookingNoticeService.createNotice(notice));
        } catch (Exception e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    /** 查询用户通知列表 */
    @GetMapping("/user/{userId}")
    public ApiResponse<List<BookingNotice>> listUserNotices(@PathVariable Long userId) {
        try {
            return ApiResponse.success(bookingNoticeService.listUserNotices(userId));
        } catch (Exception e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    /** 查询通知详情 */
    @GetMapping("/{noticeId}")
    public ApiResponse<BookingNotice> getNoticeById(@PathVariable Long noticeId) {
        try {
            return ApiResponse.success(bookingNoticeService.getNoticeById(noticeId));
        } catch (Exception e) {
            return ApiResponse.fail(e.getMessage());
        }
    }

    /** 标记通知已读 */
    @PutMapping("/read/{noticeId}")
    public ApiResponse<Boolean> markAsRead(@PathVariable Long noticeId) {
        try {
            return ApiResponse.success(bookingNoticeService.markAsRead(noticeId));
        } catch (Exception e) {
            return ApiResponse.fail(e.getMessage());
        }
    }
}
