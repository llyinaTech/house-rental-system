package com.llyinatech.houserental.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.llyinatech.houserental.entity.Permission;

import java.util.List;

public interface PermissionService extends IService<Permission> {
    /**
     * 构建权限树
     */
    List<Permission> buildTree();

    /**
     * 删除权限
     */
    void removePermission(Long id);

    /**
     * 构建用户菜单树
     */
    List<Permission> buildUserMenuTree(Long userId);
}
