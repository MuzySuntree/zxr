package com.youngman.hostel.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.youngman.hostel.dto.UserLoginDTO;
import com.youngman.hostel.dto.UserRegisterDTO;
import com.youngman.hostel.entity.RegisterVerifyCode;
import com.youngman.hostel.entity.SysUser;
import com.youngman.hostel.mapper.SysUserMapper;
import com.youngman.hostel.service.SysUserService;
import com.youngman.hostel.vo.LoginUserVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 用户业务实现
 */
@Service
@RequiredArgsConstructor
public class SysUserServiceImpl implements SysUserService {

    private static final int ROLE_NORMAL_USER = 2;

    private final SysUserMapper sysUserMapper;
    private final com.youngman.hostel.service.RegisterVerifyCodeService registerVerifyCodeService;

    @Override
    public Long register(UserRegisterDTO dto) {
        validateRegisterDTO(dto);

        if (getUserByUsername(dto.getUsername()) != null) {
            throw new RuntimeException("用户名已存在");
        }
        if (getUserByPhone(dto.getPhone()) != null) {
            throw new RuntimeException("手机号已被注册");
        }

        RegisterVerifyCode verifyCode = registerVerifyCodeService.getLatestUnusedAndUnExpired(dto.getPhone());
        if (verifyCode == null) {
            throw new RuntimeException("验证码不存在或已过期");
        }
        if (!dto.getVerifyCode().trim().equals(verifyCode.getCode())) {
            throw new RuntimeException("验证码错误");
        }

        SysUser user = new SysUser();
        user.setUsername(dto.getUsername().trim());
        user.setPassword(dto.getPassword().trim());
        user.setRealName(dto.getRealName().trim());
        user.setGender(dto.getGender());
        user.setPhone(dto.getPhone().trim());
        user.setRole(ROLE_NORMAL_USER);
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        user.setDeleted(0);

        int rows = sysUserMapper.insert(user);
        if (rows <= 0 || user.getId() == null) {
            throw new RuntimeException("用户注册失败");
        }

        registerVerifyCodeService.markCodeUsed(verifyCode.getId());
        return user.getId();
    }

    @Override
    public LoginUserVO login(UserLoginDTO dto) {
        if (dto == null || isBlank(dto.getUsername()) || isBlank(dto.getPassword())) {
            throw new IllegalArgumentException("登录参数不完整");
        }

        SysUser user = getUserByUsername(dto.getUsername());
        if (user == null) {
            throw new RuntimeException("用户不存在");
        }
        if (!dto.getPassword().equals(user.getPassword())) {
            throw new RuntimeException("用户名或密码错误");
        }

        sysUserMapper.update(null, new LambdaUpdateWrapper<SysUser>()
                .eq(SysUser::getId, user.getId())
                .eq(SysUser::getDeleted, 0)
                .set(SysUser::getLastLoginTime, LocalDateTime.now())
                .set(SysUser::getUpdateTime, LocalDateTime.now()));

        LoginUserVO vo = new LoginUserVO();
        vo.setUserId(user.getId());
        vo.setUsername(user.getUsername());
        vo.setRealName(user.getRealName());
        vo.setGender(user.getGender());
        vo.setRole(user.getRole());
        vo.setToken("mock-token-" + user.getId());
        return vo;
    }

    @Override
    public SysUser getUserById(Long userId) {
        if (userId == null) {
            throw new IllegalArgumentException("用户ID不能为空");
        }
        SysUser user = sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getId, userId)
                .eq(SysUser::getDeleted, 0));
        if (user == null) {
            throw new RuntimeException("用户不存在, id=" + userId);
        }
        return user;
    }

    @Override
    public SysUser getUserByUsername(String username) {
        if (isBlank(username)) {
            return null;
        }
        return sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getUsername, username.trim())
                .eq(SysUser::getDeleted, 0)
                .last("limit 1"));
    }

    @Override
    public List<SysUser> listUsers() {
        return sysUserMapper.selectList(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getDeleted, 0)
                .orderByDesc(SysUser::getId));
    }

    @Override
    public boolean updateUser(SysUser user) {
        if (user == null || user.getId() == null) {
            throw new IllegalArgumentException("更新用户参数非法");
        }
        SysUser dbUser = getUserById(user.getId());

        if (!isBlank(user.getPhone())) {
            validatePhone(user.getPhone());
            SysUser phoneUser = getUserByPhone(user.getPhone());
            if (phoneUser != null && !phoneUser.getId().equals(user.getId())) {
                throw new RuntimeException("手机号已被其他用户占用");
            }
        }
        if (!isBlank(user.getRealName())) {
            validateRealName(user.getRealName());
        }
        if (user.getGender() != null && user.getGender() != 1 && user.getGender() != 2) {
            throw new IllegalArgumentException("性别仅支持男(1)或女(2)");
        }

        user.setUsername(dbUser.getUsername());
        user.setPassword(dbUser.getPassword());
        user.setUpdateTime(LocalDateTime.now());
        return sysUserMapper.updateById(user) > 0;
    }

    @Override
    public boolean deleteUser(Long userId) {
        getUserById(userId);
        return sysUserMapper.update(null, new LambdaUpdateWrapper<SysUser>()
                .eq(SysUser::getId, userId)
                .eq(SysUser::getDeleted, 0)
                .set(SysUser::getDeleted, 1)
                .set(SysUser::getUpdateTime, LocalDateTime.now())) > 0;
    }
    
    public SysUser getUserByPhone(String phone) {
        if (isBlank(phone)) {
            return null;
        }
        return sysUserMapper.selectOne(new LambdaQueryWrapper<SysUser>()
                .eq(SysUser::getPhone, phone.trim())
                .eq(SysUser::getDeleted, 0)
                .last("limit 1"));
    }

    private void validateRegisterDTO(UserRegisterDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("注册参数不能为空");
        }
        if (isBlank(dto.getUsername()) || dto.getUsername().trim().length() < 4 || dto.getUsername().trim().length() > 20) {
            throw new IllegalArgumentException("用户名长度需在4-20位之间");
        }
        if (isBlank(dto.getPassword()) || dto.getPassword().trim().length() < 6 || dto.getPassword().trim().length() > 20) {
            throw new IllegalArgumentException("密码长度需在6-20位之间");
        }
        if (isBlank(dto.getConfirmPassword()) || !dto.getPassword().equals(dto.getConfirmPassword())) {
            throw new IllegalArgumentException("两次输入的密码不一致");
        }
        validateRealName(dto.getRealName());
        if (dto.getGender() == null || (dto.getGender() != 1 && dto.getGender() != 2)) {
            throw new IllegalArgumentException("性别仅支持男(1)或女(2)");
        }
        validatePhone(dto.getPhone());
        if (isBlank(dto.getVerifyCode())) {
            throw new IllegalArgumentException("验证码不能为空");
        }
    }

    private void validateRealName(String realName) {
        if (isBlank(realName)) {
            throw new IllegalArgumentException("真实姓名不能为空");
        }
        String trimName = realName.trim();
        if (!trimName.matches("^[\\u4e00-\\u9fa5]{2,10}$")) {
            throw new IllegalArgumentException("真实姓名需为2-10位中文字符");
        }
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
