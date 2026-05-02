package com.youngman.hostel.service;

import com.youngman.hostel.dto.UserLoginDTO;
import com.youngman.hostel.dto.UserRegisterDTO;
import com.youngman.hostel.entity.SysUser;
import com.youngman.hostel.vo.LoginUserVO;

import java.util.List;

/**
 * 用户业务接口
 */
public interface SysUserService {

    Long register(UserRegisterDTO dto);

    LoginUserVO login(UserLoginDTO dto);

    SysUser getUserById(Long userId);

    SysUser getUserByUsername(String username);

    SysUser getUserByPhone(String phone);

    List<SysUser> listUsers();

    boolean updateUser(SysUser user);

    boolean deleteUser(Long userId);
}
