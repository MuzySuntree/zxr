package com.youngman.hostel.service;

import com.youngman.hostel.entity.BookingNotice;

import java.util.List;

/**
 * 通知业务接口
 */
public interface BookingNoticeService {

    /**
     * 创建通知
     */
    Long createNotice(BookingNotice notice);

    /**
     * 查询用户通知列表
     */
    List<BookingNotice> listUserNotices(Long userId);

    /**
     * 查询通知详情
     */
    BookingNotice getNoticeById(Long noticeId);

    /**
     * 标记通知为已读
     */
    boolean markAsRead(Long noticeId);
}
