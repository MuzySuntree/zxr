package com.youngman.hostel.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.youngman.hostel.entity.BookingNotice;
import com.youngman.hostel.mapper.BookingNoticeMapper;
import com.youngman.hostel.service.BookingNoticeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 通知业务实现
 */
@Service
@RequiredArgsConstructor
public class BookingNoticeServiceImpl implements BookingNoticeService {

    private final BookingNoticeMapper bookingNoticeMapper;

    @Override
    public Long createNotice(BookingNotice notice) {
        if (notice == null || notice.getUserId() == null || notice.getTitle() == null || notice.getContent() == null) {
            throw new IllegalArgumentException("通知参数不完整");
        }
        notice.setIsRead(notice.getIsRead() == null ? 0 : notice.getIsRead());
        notice.setCreateTime(LocalDateTime.now());
        notice.setUpdateTime(LocalDateTime.now());
        int rows = bookingNoticeMapper.insert(notice);
        if (rows <= 0 || notice.getId() == null) {
            throw new RuntimeException("通知创建失败");
        }
        return notice.getId();
    }

    @Override
    public List<BookingNotice> listUserNotices(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("userId不能为空");
        }
        return bookingNoticeMapper.selectList(new LambdaQueryWrapper<BookingNotice>()
                .eq(BookingNotice::getUserId, userId)
                .orderByDesc(BookingNotice::getId));
    }

    @Override
    public BookingNotice getNoticeById(Long noticeId) {
        if (noticeId == null) {
            throw new IllegalArgumentException("noticeId不能为空");
        }
        BookingNotice notice = bookingNoticeMapper.selectById(noticeId);
        if (notice == null) {
            throw new RuntimeException("通知不存在, noticeId=" + noticeId);
        }
        return notice;
    }

    @Override
    public boolean markAsRead(Long noticeId) {
        getNoticeById(noticeId);
        return bookingNoticeMapper.update(null, new LambdaUpdateWrapper<BookingNotice>()
                .eq(BookingNotice::getId, noticeId)
                .set(BookingNotice::getIsRead, 1)
                .set(BookingNotice::getUpdateTime, LocalDateTime.now())) > 0;
    }
}
