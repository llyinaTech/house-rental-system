package com.llyinatech.houserental.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.llyinatech.houserental.model.entity.User;

import java.util.List;

/**
 * 用户Service接口
 */
public interface UserService extends IService<User> {

    /**
     * 保存用户角色
     */
    void saveUserRoles(Long userId, List<Long> roleIds);

    /**
     * 获取用户角色ID列表
     */
    List<Long> getUserRoleIds(Long userId);
}
