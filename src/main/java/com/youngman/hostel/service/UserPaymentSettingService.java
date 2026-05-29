package com.youngman.hostel.service;

import com.youngman.hostel.entity.UserPaymentSetting;

import java.util.List;

public interface UserPaymentSettingService {

    List<UserPaymentSetting> listByUserId(Long userId);

    Long save(UserPaymentSetting setting);

    boolean update(UserPaymentSetting setting);

    boolean delete(Long id);

    boolean setDefault(Long id);

    UserPaymentSetting getDefaultByUserId(Long userId);

    UserPaymentSetting getEnabledByUserIdAndPayType(Long userId, Integer payType);

    void deductBalance(Long settingId, java.math.BigDecimal amount);
}
