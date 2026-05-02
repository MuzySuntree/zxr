package com.youngman.hostel.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.youngman.hostel.entity.HostelActivity;
import com.youngman.hostel.entity.HostelActivitySignup;
import com.youngman.hostel.mapper.HostelActivityMapper;
import com.youngman.hostel.mapper.HostelActivitySignupMapper;
import com.youngman.hostel.service.HostelActivitySignupService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class HostelActivitySignupServiceImpl implements HostelActivitySignupService {

    private final HostelActivitySignupMapper hostelActivitySignupMapper;
    private final HostelActivityMapper hostelActivityMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean signup(Long activityId, Long userId) {
        HostelActivity activity = getValidActivityForSignup(activityId);
        if (userId == null) throw new IllegalArgumentException("userId不能为空");

        HostelActivitySignup existed = hostelActivitySignupMapper.selectOne(new LambdaQueryWrapper<HostelActivitySignup>()
                .eq(HostelActivitySignup::getActivityId, activityId)
                .eq(HostelActivitySignup::getUserId, userId)
                .last("limit 1"));

        if (existed != null && Integer.valueOf(1).equals(existed.getSignupStatus())) {
            throw new RuntimeException("您已报名该活动，请勿重复报名");
        }

        checkCapacity(activity);

        if (existed == null) {
            HostelActivitySignup signup = new HostelActivitySignup();
            signup.setActivityId(activityId);
            signup.setUserId(userId);
            signup.setSignupStatus(1);
            signup.setCreateTime(LocalDateTime.now());
            signup.setUpdateTime(LocalDateTime.now());
            if (hostelActivitySignupMapper.insert(signup) <= 0) throw new RuntimeException("报名失败");
        } else {
            hostelActivitySignupMapper.update(null, new LambdaUpdateWrapper<HostelActivitySignup>()
                    .eq(HostelActivitySignup::getId, existed.getId())
                    .set(HostelActivitySignup::getSignupStatus, 1)
                    .set(HostelActivitySignup::getUpdateTime, LocalDateTime.now()));
        }

        increaseJoinedPeople(activityId);
        return true;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean cancelSignup(Long activityId, Long userId) {
        if (activityId == null) throw new IllegalArgumentException("activityId不能为空");
        if (userId == null) throw new IllegalArgumentException("userId不能为空");

        HostelActivitySignup existed = hostelActivitySignupMapper.selectOne(new LambdaQueryWrapper<HostelActivitySignup>()
                .eq(HostelActivitySignup::getActivityId, activityId)
                .eq(HostelActivitySignup::getUserId, userId)
                .last("limit 1"));
        if (existed == null || !Integer.valueOf(1).equals(existed.getSignupStatus())) {
            throw new RuntimeException("当前未报名该活动，无法取消");
        }

        hostelActivitySignupMapper.update(null, new LambdaUpdateWrapper<HostelActivitySignup>()
                .eq(HostelActivitySignup::getId, existed.getId())
                .set(HostelActivitySignup::getSignupStatus, 2)
                .set(HostelActivitySignup::getUpdateTime, LocalDateTime.now()));

        decreaseJoinedPeople(activityId);
        return true;
    }

    @Override
    public List<HostelActivitySignup> listByActivityId(Long activityId) {
        if (activityId == null) throw new IllegalArgumentException("activityId不能为空");
        return hostelActivitySignupMapper.selectList(new LambdaQueryWrapper<HostelActivitySignup>()
                .eq(HostelActivitySignup::getActivityId, activityId)
                .eq(HostelActivitySignup::getSignupStatus, 1)
                .orderByDesc(HostelActivitySignup::getCreateTime));
    }

    @Override
    public List<HostelActivity> listUserSignedActivities(Long userId) {
        if (userId == null) throw new IllegalArgumentException("userId不能为空");
        List<HostelActivitySignup> signups = hostelActivitySignupMapper.selectList(new LambdaQueryWrapper<HostelActivitySignup>()
                .eq(HostelActivitySignup::getUserId, userId)
                .eq(HostelActivitySignup::getSignupStatus, 1)
                .orderByDesc(HostelActivitySignup::getCreateTime));
        if (signups.isEmpty()) return List.of();
        List<Long> ids = signups.stream().map(HostelActivitySignup::getActivityId).collect(Collectors.toList());
        return hostelActivityMapper.selectList(new LambdaQueryWrapper<HostelActivity>()
                .in(HostelActivity::getId, ids)
                .eq(HostelActivity::getDeleted, 0)
                .orderByAsc(HostelActivity::getActivityTime));
    }

    private HostelActivity getValidActivityForSignup(Long activityId) {
        if (activityId == null) throw new IllegalArgumentException("activityId不能为空");
        HostelActivity activity = hostelActivityMapper.selectOne(new LambdaQueryWrapper<HostelActivity>()
                .eq(HostelActivity::getId, activityId)
                .eq(HostelActivity::getDeleted, 0)
                .last("limit 1"));
        if (activity == null) throw new RuntimeException("活动不存在");
        if (!Integer.valueOf(1).equals(activity.getStatus())) throw new RuntimeException("当前活动不在报名中");
        return activity;
    }

    private void checkCapacity(HostelActivity activity) {
        Integer max = activity.getMaxPeople();
        Integer joined = activity.getJoinedPeople() == null ? 0 : activity.getJoinedPeople();
        if (max != null && max > 0 && joined >= max) {
            throw new RuntimeException("活动报名人数已满");
        }
    }

    private void increaseJoinedPeople(Long activityId) {
        HostelActivity activity = hostelActivityMapper.selectById(activityId);
        Integer joined = activity.getJoinedPeople() == null ? 0 : activity.getJoinedPeople();
        hostelActivityMapper.update(null, new LambdaUpdateWrapper<HostelActivity>()
                .eq(HostelActivity::getId, activityId)
                .set(HostelActivity::getJoinedPeople, joined + 1)
                .set(HostelActivity::getUpdateTime, LocalDateTime.now()));
    }

    private void decreaseJoinedPeople(Long activityId) {
        HostelActivity activity = hostelActivityMapper.selectById(activityId);
        Integer joined = activity.getJoinedPeople() == null ? 0 : activity.getJoinedPeople();
        hostelActivityMapper.update(null, new LambdaUpdateWrapper<HostelActivity>()
                .eq(HostelActivity::getId, activityId)
                .set(HostelActivity::getJoinedPeople, Math.max(0, joined - 1))
                .set(HostelActivity::getUpdateTime, LocalDateTime.now()));
    }
}
