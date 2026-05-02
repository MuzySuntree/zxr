package com.youngman.hostel.service;

public interface RegisterVerifyCodeService {

    String sendRegisterCode(String phone);

    boolean checkRegisterCode(String phone, String code);

    void verifyCodeForRegister(String phone, String code);

    void markCodeUsed(Long id);
}
