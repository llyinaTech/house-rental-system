package com.llyinatech.houserental.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.llyinatech.houserental.entity.Role;
import com.llyinatech.houserental.entity.RolePermission;
import com.llyinatech.houserental.mapper.RoleMapper;
import com.llyinatech.houserental.mapper.RolePermissionMapper;
import com.llyinatech.houserental.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 角色Service实现类
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void saveRolePermissions(Long roleId, List<Long> permIds) {
        // Delete old permissions
        LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermission::getRoleId, roleId);
        rolePermissionMapper.delete(wrapper);

        // Insert new permissions
        if (permIds != null && !permIds.isEmpty()) {
            for (Long permId : permIds) {
                RolePermission rp = new RolePermission();
                rp.setRoleId(roleId);
                rp.setPermId(permId);
                rolePermissionMapper.insert(rp);
            }
        }
    }

    @Override
    public List<Long> getRolePermissions(Long roleId) {
        LambdaQueryWrapper<RolePermission> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(RolePermission::getRoleId, roleId);
        wrapper.select(RolePermission::getPermId);
        return rolePermissionMapper.selectList(wrapper).stream()
                .map(RolePermission::getPermId)
                .collect(Collectors.toList());
    }
}

