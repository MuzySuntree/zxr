package com.youngman.hostel.service;

import com.youngman.hostel.entity.RegisterVerifyCode;

public interface RegisterVerifyCodeService {

    String sendRegisterCode(String phone);

    boolean checkRegisterCode(String phone, String code);

    void verifyCodeForRegister(String phone, String code);

    void markCodeUsed(Long id);

    RegisterVerifyCode getLatestUnusedAndUnExpired(String phone);
}
