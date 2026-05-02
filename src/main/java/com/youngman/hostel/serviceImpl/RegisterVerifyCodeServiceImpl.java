package com.youngman.hostel.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.youngman.hostel.entity.RegisterVerifyCode;
import com.youngman.hostel.mapper.RegisterVerifyCodeMapper;
import com.youngman.hostel.service.RegisterVerifyCodeService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class RegisterVerifyCodeServiceImpl implements RegisterVerifyCodeService {

    private static final int CODE_EXPIRE_MINUTES = 5;

    private final RegisterVerifyCodeMapper registerVerifyCodeMapper;

    @Override
    public String sendRegisterCode(String phone) {
        validatePhone(phone);

        RegisterVerifyCode latest = getLatestByPhone(phone);
        if (latest != null && latest.getCreateTime() != null
                && latest.getCreateTime().plusSeconds(60).isAfter(LocalDateTime.now())) {
            throw new RuntimeException("发送过于频繁，请60秒后再试");
        }

        String code = generateCode();
        LocalDateTime now = LocalDateTime.now();

        RegisterVerifyCode entity = new RegisterVerifyCode();
        entity.setPhone(phone);
        entity.setCode(code);
        entity.setExpireTime(now.plusMinutes(CODE_EXPIRE_MINUTES));
        entity.setUsed(0);
        entity.setCreateTime(now);
        entity.setUpdateTime(now);

        if (registerVerifyCodeMapper.insert(entity) <= 0) {
            throw new RuntimeException("验证码生成失败");
        }
        return code;
    }

    @Override
    public boolean checkRegisterCode(String phone, String code) {
        if (isBlank(code)) {
            return false;
        }
        validatePhone(phone);

        RegisterVerifyCode latestValid = registerVerifyCodeMapper.selectOne(new LambdaQueryWrapper<RegisterVerifyCode>()
                .eq(RegisterVerifyCode::getPhone, phone)
                .eq(RegisterVerifyCode::getUsed, 0)
                .gt(RegisterVerifyCode::getExpireTime, LocalDateTime.now())
                .orderByDesc(RegisterVerifyCode::getId)
                .last("limit 1"));
        return latestValid != null && code.trim().equals(latestValid.getCode());
    }

    @Override
    public void verifyCodeForRegister(String phone, String code) {
        if (isBlank(code)) {
            throw new IllegalArgumentException("验证码不能为空");
        }
        validatePhone(phone);

        RegisterVerifyCode latestValid = registerVerifyCodeMapper.selectOne(new LambdaQueryWrapper<RegisterVerifyCode>()
                .eq(RegisterVerifyCode::getPhone, phone)
                .eq(RegisterVerifyCode::getUsed, 0)
                .gt(RegisterVerifyCode::getExpireTime, LocalDateTime.now())
                .orderByDesc(RegisterVerifyCode::getId)
                .last("limit 1"));

        if (latestValid == null) {
            throw new RuntimeException("验证码不存在或已过期");
        }
        if (!code.trim().equals(latestValid.getCode())) {
            throw new RuntimeException("验证码错误");
        }
    }

    @Override
    public void markCodeUsed(Long id) {
        if (id == null) {
            throw new IllegalArgumentException("验证码ID不能为空");
        }
        registerVerifyCodeMapper.update(null, new LambdaUpdateWrapper<RegisterVerifyCode>()
                .eq(RegisterVerifyCode::getId, id)
                .set(RegisterVerifyCode::getUsed, 1)
                .set(RegisterVerifyCode::getUpdateTime, LocalDateTime.now()));
    }

    public RegisterVerifyCode getLatestByPhone(String phone) {
        return registerVerifyCodeMapper.selectOne(new LambdaQueryWrapper<RegisterVerifyCode>()
                .eq(RegisterVerifyCode::getPhone, phone)
                .orderByDesc(RegisterVerifyCode::getId)
                .last("limit 1"));
    }

    public RegisterVerifyCode getLatestUnusedAndUnExpired(String phone) {
        return registerVerifyCodeMapper.selectOne(new LambdaQueryWrapper<RegisterVerifyCode>()
                .eq(RegisterVerifyCode::getPhone, phone)
                .eq(RegisterVerifyCode::getUsed, 0)
                .gt(RegisterVerifyCode::getExpireTime, LocalDateTime.now())
                .orderByDesc(RegisterVerifyCode::getId)
                .last("limit 1"));
    }

    private String generateCode() {
        return String.format("%06d", new Random().nextInt(1_000_000));
    }

    private void validatePhone(String phone) {
        if (isBlank(phone)) {
            throw new IllegalArgumentException("手机号不能为空");
        }
        if (!phone.trim().matches("^1[3-9]\\d{9}$")) {
            throw new IllegalArgumentException("手机号格式不正确");
        }
    }

    private boolean isBlank(String text) {
        return text == null || text.trim().isEmpty();
    }
}
