package com.youngman.hostel.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.youngman.hostel.entity.UserPaymentSetting;
import com.youngman.hostel.mapper.UserPaymentSettingMapper;
import com.youngman.hostel.service.UserPaymentSettingService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserPaymentSettingServiceImpl implements UserPaymentSettingService {

    private final UserPaymentSettingMapper userPaymentSettingMapper;

    @Override
    public List<UserPaymentSetting> listByUserId(Long userId) {
        if (userId == null) throw new IllegalArgumentException("userId不能为空");
        return userPaymentSettingMapper.selectList(new LambdaQueryWrapper<UserPaymentSetting>()
                .eq(UserPaymentSetting::getUserId, userId).orderByDesc(UserPaymentSetting::getIsDefault).orderByAsc(UserPaymentSetting::getPayType));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long save(UserPaymentSetting setting) {
        validateSetting(setting, false);
        if (setting.getIsDefault() != null && setting.getIsDefault() == 1) {
            clearDefault(setting.getUserId());
        }
        setting.setCreateTime(LocalDateTime.now());
        setting.setUpdateTime(LocalDateTime.now());
        if (setting.getStatus() == null) setting.setStatus(1);
        if (setting.getIsDefault() == null) setting.setIsDefault(0);
        if (setting.getBalance() == null) setting.setBalance(BigDecimal.ZERO);
        if (userPaymentSettingMapper.insert(setting) <= 0) throw new RuntimeException("新增支付方式失败");
        return setting.getId();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean update(UserPaymentSetting setting) {
        validateSetting(setting, true);
        UserPaymentSetting db = userPaymentSettingMapper.selectById(setting.getId());
        if (db == null) throw new RuntimeException("支付方式不存在");
        setting.setUserId(db.getUserId());
        if (setting.getIsDefault() != null && setting.getIsDefault() == 1) clearDefault(db.getUserId());
        setting.setUpdateTime(LocalDateTime.now());
        return userPaymentSettingMapper.updateById(setting) > 0;
    }

    @Override
    public boolean delete(Long id) {
        if (id == null) throw new IllegalArgumentException("id不能为空");
        return userPaymentSettingMapper.deleteById(id) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean setDefault(Long id) {
        if (id == null) throw new IllegalArgumentException("id不能为空");
        UserPaymentSetting db = userPaymentSettingMapper.selectById(id);
        if (db == null) throw new RuntimeException("支付方式不存在");
        clearDefault(db.getUserId());
        return userPaymentSettingMapper.update(null, new LambdaUpdateWrapper<UserPaymentSetting>()
                .eq(UserPaymentSetting::getId, id)
                .set(UserPaymentSetting::getIsDefault, 1)
                .set(UserPaymentSetting::getUpdateTime, LocalDateTime.now())) > 0;
    }

    @Override
    public UserPaymentSetting getDefaultByUserId(Long userId) {
        return userPaymentSettingMapper.selectOne(new LambdaQueryWrapper<UserPaymentSetting>()
                .eq(UserPaymentSetting::getUserId, userId)
                .eq(UserPaymentSetting::getIsDefault, 1)
                .eq(UserPaymentSetting::getStatus, 1)
                .last("limit 1"));
    }

    @Override
    public UserPaymentSetting getEnabledByUserIdAndPayType(Long userId, Integer payType) {
        return userPaymentSettingMapper.selectOne(new LambdaQueryWrapper<UserPaymentSetting>()
                .eq(UserPaymentSetting::getUserId, userId)
                .eq(UserPaymentSetting::getPayType, payType)
                .eq(UserPaymentSetting::getStatus, 1)
                .last("limit 1"));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deductBalance(Long settingId, BigDecimal amount) {
        if (settingId == null || amount == null || amount.compareTo(BigDecimal.ZERO) < 0) throw new IllegalArgumentException("扣减参数非法");
        UserPaymentSetting db = userPaymentSettingMapper.selectById(settingId);
        if (db == null) throw new RuntimeException("支付方式不存在");
        BigDecimal balance = db.getBalance() == null ? BigDecimal.ZERO : db.getBalance();
        if (balance.compareTo(amount) < 0) throw new RuntimeException("余额不足，支付失败");
        userPaymentSettingMapper.update(null, new LambdaUpdateWrapper<UserPaymentSetting>()
                .eq(UserPaymentSetting::getId, settingId)
                .set(UserPaymentSetting::getBalance, balance.subtract(amount))
                .set(UserPaymentSetting::getUpdateTime, LocalDateTime.now()));
    }

    private void clearDefault(Long userId) {
        userPaymentSettingMapper.update(null, new LambdaUpdateWrapper<UserPaymentSetting>()
                .eq(UserPaymentSetting::getUserId, userId)
                .set(UserPaymentSetting::getIsDefault, 0)
                .set(UserPaymentSetting::getUpdateTime, LocalDateTime.now()));
    }

    private void validateSetting(UserPaymentSetting setting, boolean isUpdate) {
        if (setting == null) throw new IllegalArgumentException("支付方式参数不能为空");
        if (isUpdate && setting.getId() == null) throw new IllegalArgumentException("id不能为空");
        if (!isUpdate && setting.getUserId() == null) throw new IllegalArgumentException("userId不能为空");
        if (setting.getPayType() == null || (setting.getPayType() != 1 && setting.getPayType() != 2 && setting.getPayType() != 3)) {
            throw new IllegalArgumentException("payType仅支持1-余额、2-微信、3-支付宝");
        }
    }
}
