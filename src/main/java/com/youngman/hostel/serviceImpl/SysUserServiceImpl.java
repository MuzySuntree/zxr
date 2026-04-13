package com.youngman.hostel.serviceImpl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.youngman.hostel.dto.UserLoginDTO;
import com.youngman.hostel.dto.UserRegisterDTO;
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

    @Override
    public Long register(UserRegisterDTO dto) {
        if (dto == null) {
            throw new IllegalArgumentException("注册参数不能为空");
        }
        if (isBlank(dto.getUsername()) || isBlank(dto.getPassword())) {
            throw new IllegalArgumentException("用户名和密码不能为空");
        }

        SysUser exists = getUserByUsername(dto.getUsername());
        if (exists != null) {
            throw new RuntimeException("用户名已存在: " + dto.getUsername());
        }

        SysUser user = new SysUser();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setRealName(dto.getRealName());
        user.setGender(dto.getGender() == null ? 0 : dto.getGender());
        user.setPhone(dto.getPhone());
        user.setRole(dto.getRole() == null ? ROLE_NORMAL_USER : dto.getRole());
        user.setStatus(1);
        user.setCreateTime(LocalDateTime.now());
        user.setUpdateTime(LocalDateTime.now());
        user.setDeleted(0);

        int rows = sysUserMapper.insert(user);
        if (rows <= 0 || user.getId() == null) {
            throw new RuntimeException("用户注册失败");
        }
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
                .eq(SysUser::getUsername, username)
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
        getUserById(user.getId());
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

    private boolean isBlank(String text) {
        return text == null || text.trim().isEmpty();
    }
}
