package com.llyinatech.houserental.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.llyinatech.houserental.model.entity.Permission;
import com.llyinatech.houserental.mapper.PermissionMapper;
import com.llyinatech.houserental.service.PermissionService;
import com.llyinatech.houserental.model.entity.RolePermission;
import com.llyinatech.houserental.exception.BusinessException;
import com.llyinatech.houserental.mapper.RolePermissionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class PermissionServiceImpl extends ServiceImpl<PermissionMapper, Permission> implements PermissionService {

    @Autowired
    private RolePermissionMapper rolePermissionMapper;

    @Override
    public List<Permission> buildTree() {
        // Get all permissions
        List<Permission> allPerms = this.list(new LambdaQueryWrapper<Permission>()
                .orderByAsc(Permission::getSortOrder));

        // Find roots (parentId = 0)
        List<Permission> roots = allPerms.stream()
                .filter(p -> p.getParentId() == 0)
                .collect(Collectors.toList());

        // Build tree recursively
        for (Permission root : roots) {
            setChildren(root, allPerms);
        }

        return roots;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void removePermission(Long id) {
        // 检查是否有子节点
        Long count = this.lambdaQuery().eq(Permission::getParentId, id).count();
        if (count > 0) {
            throw new BusinessException("存在子权限，无法删除");
        }

        // 检查是否被角色引用
        Long assignedCount = rolePermissionMapper.selectCount(new LambdaQueryWrapper<RolePermission>().eq(RolePermission::getPermId, id));
        if (assignedCount > 0) {
            throw new BusinessException("该权限已被分配给角色，无法删除");
        }

        this.removeById(id);
    }

    private void setChildren(Permission parent, List<Permission> allPerms) {
        List<Permission> children = allPerms.stream()
                .filter(p -> p.getParentId().equals(parent.getId()))
                .collect(Collectors.toList());

        if (!children.isEmpty()) {
            parent.setChildren(children);
            for (Permission child : children) {
                setChildren(child, allPerms);
            }
        }
    }

    @Override
    public List<Permission> buildUserMenuTree(Long userId) {
        List<Permission> userPerms;
        if (userId == 1L) { // Admin user gets all permissions
            userPerms = this.list(new LambdaQueryWrapper<Permission>()
                    .in(Permission::getType, 1, 2) // Menu and Button(actually should be Menu only for display, but here we filter later)
                    .eq(Permission::getStatus, 1)
                    .orderByAsc(Permission::getSortOrder));
        } else {
            userPerms = baseMapper.selectPermissionsByUserId(userId);
        }

        // Filter only menus (type 1 and 2 usually, but for layout we typically need directories and menus)
        // Assuming type 1 = Directory/Menu, 2 = Page/Menu, 3 = Button
        // Let's check Permission entity definition or data. Assuming 1 and 2 are navigable.

        // Find roots (parentId = 0)
        List<Permission> roots = userPerms.stream()
                .filter(p -> p.getParentId() == 0 && (p.getType() == 1 || p.getType() == 2))
                .collect(Collectors.toList());

        // Build tree
        for (Permission root : roots) {
            setChildren(root, userPerms);
        }

        return roots;
    }
}
