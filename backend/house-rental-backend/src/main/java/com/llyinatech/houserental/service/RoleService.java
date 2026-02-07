package com.llyinatech.houserental.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.llyinatech.houserental.entity.Role;

import java.util.List;

/**
 * 角色Service接口
 */
public interface RoleService extends IService<Role> {
    /**
     * 保存角色权限
     */
    void saveRolePermissions(Long roleId, List<Long> permIds);

    /**
     * 获取角色权限ID列表
     */
    List<Long> getRolePermissions(Long roleId);
}
