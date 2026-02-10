package com.llyinatech.houserental.controller;

import com.llyinatech.houserental.annotation.SysLogAnnotation;
import com.llyinatech.houserental.common.Result;
import com.llyinatech.houserental.model.entity.Permission;
import com.llyinatech.houserental.enums.ActionEnum;
import com.llyinatech.houserental.enums.ModuleEnum;
import com.llyinatech.houserental.service.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 权限管理Controller
 */
@RestController
@RequestMapping("/api/permission")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;

    /**
     * 获取权限树
     */
    @GetMapping("/tree")
    @PreAuthorize("hasAuthority('system:permission:query')")
    public Result<List<Permission>> getTree() {
        List<Permission> tree = permissionService.buildTree();
        return Result.success(tree);
    }

    /**
     * 获取所有权限列表
     */
    @GetMapping("/list")
    @PreAuthorize("hasAuthority('system:permission:query')")
    public Result<List<Permission>> list() {
        return Result.success(permissionService.list());
    }

    /**
     * 新增权限
     */
    @SysLogAnnotation(module = ModuleEnum.PERMISSION_MANAGEMENT, action = ActionEnum.ADD, detail = "新增权限")
    @PostMapping
    @PreAuthorize("hasAuthority('system:permission:add')")
    public Result<String> save(@RequestBody Permission permission) {
        permissionService.save(permission);
        return Result.success("添加成功");
    }

    /**
     * 修改权限
     */
    @SysLogAnnotation(module = ModuleEnum.PERMISSION_MANAGEMENT, action = ActionEnum.MODIFY, detail = "修改权限")
    @PutMapping
    @PreAuthorize("hasAuthority('system:permission:edit')")
    public Result<String> update(@RequestBody Permission permission) {
        permissionService.updateById(permission);
        return Result.success("修改成功");
    }

    /**
     * 删除权限
     */
    @SysLogAnnotation(module = ModuleEnum.PERMISSION_MANAGEMENT, action = ActionEnum.DELETE, detail = "删除权限")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:permission:remove')")
    public Result<String> delete(@PathVariable Long id) {
        permissionService.removePermission(id);
        return Result.success("删除成功");
    }
}
