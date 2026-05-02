package com.youngman.hostel.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.youngman.hostel.entity.MessageBoard;
import com.youngman.hostel.mapper.MessageBoardMapper;
import com.youngman.hostel.service.MessageBoardService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MessageBoardServiceImpl implements MessageBoardService {
    private final MessageBoardMapper messageBoardMapper;

    @Override
    public List<MessageBoard> listLatestPublished(int limit) {
        return messageBoardMapper.selectList(new LambdaQueryWrapper<MessageBoard>()
                .eq(MessageBoard::getStatus, 1)
                .eq(MessageBoard::getDeleted, 0)
                .orderByDesc(MessageBoard::getCreateTime)
                .last("limit " + limit));
    }

    @Override
    public List<MessageBoard> listPublished() {
        return listLatestPublished(1000);
    }

    @Override
    public List<MessageBoard> listAdminAll() {
        return messageBoardMapper.selectList(new LambdaQueryWrapper<MessageBoard>()
                .eq(MessageBoard::getDeleted, 0)
                .in(MessageBoard::getStatus, 0, 1, 2)
                .orderByDesc(MessageBoard::getCreateTime));
    }

    @Override
    public Long save(MessageBoard m) {
        if (m == null || m.getUserId() == null || m.getCategory() == null || m.getTitle() == null || m.getContent() == null) {
            throw new IllegalArgumentException("留言参数不完整");
        }
        if (!(m.getCategory() == 1 || m.getCategory() == 2 || m.getCategory() == 3 || m.getCategory() == 4 || m.getCategory() == 9)) {
            throw new IllegalArgumentException("留言分类不合法");
        }
        m.setStatus(0);
        m.setViewCount(0);
        m.setCreateTime(LocalDateTime.now());
        m.setUpdateTime(LocalDateTime.now());
        m.setDeleted(0);
        if (messageBoardMapper.insert(m) <= 0) {
            throw new RuntimeException("发布留言失败");
        }
        return m.getId();
    }

    @Override
    public boolean audit(Long id, Integer status) {
        if (id == null) throw new IllegalArgumentException("id不能为空");
        if (status == null || (status != 1 && status != 2)) throw new IllegalArgumentException("审核状态仅支持1或2");
        return messageBoardMapper.update(null, new LambdaUpdateWrapper<MessageBoard>()
                .eq(MessageBoard::getId, id)
                .eq(MessageBoard::getDeleted, 0)
                .set(MessageBoard::getStatus, status)
                .set(MessageBoard::getUpdateTime, LocalDateTime.now())) > 0;
    }

    @Override
    public boolean delete(Long id) {
        if (id == null) throw new IllegalArgumentException("id不能为空");
        MessageBoard m = new MessageBoard();
        m.setId(id);
        m.setDeleted(1);
        m.setUpdateTime(LocalDateTime.now());
        return messageBoardMapper.updateById(m) > 0;
    }
}
