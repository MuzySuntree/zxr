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

    /**
     * 用户注册
     *
     * @param dto 注册参数
     * @return 新增用户ID
     */
    Long register(UserRegisterDTO dto);

    /**
     * 用户登录
     *
     * @param dto 登录参数
     * @return 登录用户信息（可含token）
     */
    LoginUserVO login(UserLoginDTO dto);

    /**
     * 根据用户ID查询
     *
     * @param userId 用户ID
     * @return 用户实体
     */
    SysUser getUserById(Long userId);

    /**
     * 根据用户名查询
     *
     * @param username 用户名
     * @return 用户实体
     */
    SysUser getUserByUsername(String username);

    /**
     * 查询用户列表
     *
     * @return 用户列表
     */
    List<SysUser> listUsers();

    /**
     * 更新用户
     *
     * @param user 用户实体
     * @return 是否成功
     */
    boolean updateUser(SysUser user);

    /**
     * 逻辑删除用户
     *
     * @param userId 用户ID
     * @return 是否成功
     */
    boolean deleteUser(Long userId);
}
