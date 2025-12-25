package com.llyinatech.houserental.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.llyinatech.houserental.entity.User;

/**
 * 用户信息服务接口
 */
public interface UserService {

    /**
     * 分页查询用户列表
     */
    Page<User> getUserList(Integer pageNum, Integer pageSize, String keyword);

    /**
     * 根据ID查询用户详情
     */
    User getUserById(Long id);

    /**
     * 添加用户
     */
    void addUser(User user);

    /**
     * 更新用户信息
     */
    void updateUser(User user);

    /**
     * 删除用户
     */
    void deleteUser(Long id);

    /**
     * 根据用户名查询用户
     */
    User getUserByUsername(String username);
}
