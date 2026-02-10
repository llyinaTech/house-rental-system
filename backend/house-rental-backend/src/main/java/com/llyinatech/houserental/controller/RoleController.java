package com.llyinatech.houserental.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.llyinatech.houserental.annotation.SysLogAnnotation;
import com.llyinatech.houserental.common.Result;
import com.llyinatech.houserental.model.entity.Role;
import com.llyinatech.houserental.enums.ActionEnum;
import com.llyinatech.houserental.enums.ModuleEnum;
import com.llyinatech.houserental.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import java.util.List;

/**
 * 角色Controller
 */
@RestController
@RequestMapping("/api/role")
public class RoleController {

    @Autowired
    private RoleService roleService;

    /**
     * 分页查询角色列表
     */
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('system:role:query')")
    public Result<Page<Role>> page(@RequestParam(defaultValue = "1") Integer current,
                                   @RequestParam(defaultValue = "10") Integer size,
                                   @RequestParam(required = false) String roleName,
                                   @RequestParam(required = false) String roleKey) {
        LambdaQueryWrapper<Role> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(roleName)) {
            wrapper.like(Role::getRoleName, roleName);
        }
        if (StringUtils.hasText(roleKey)) {
            wrapper.like(Role::getRoleKey, roleKey);
        }
        Page<Role> page = roleService.page(new Page<>(current, size), wrapper);
        return Result.success(page);
    }
    
    /**
     * 获取所有角色（用于下拉框）
     */
    @GetMapping("/list")
    public Result<List<Role>> list() {
        return Result.success(roleService.list());
    }

    /**
     * 根据ID查询角色
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:query')")
    public Result<Role> getById(@PathVariable Long id) {
        Role role = roleService.getById(id);
        return Result.success(role);
    }

    /**
     * 获取角色权限
     */
    @GetMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('system:role:query')")
    public Result<List<Long>> getRolePermissions(@PathVariable Long id) {
        return Result.success(roleService.getRolePermissions(id));
    }

    /**
     * 分配权限
     */
    @SysLogAnnotation(module = ModuleEnum.ROLE_MANAGEMENT, action = ActionEnum.MODIFY, detail = "分配角色权限")
    @PostMapping("/{id}/permissions")
    @PreAuthorize("hasAuthority('system:role:edit')")
    public Result<String> saveRolePermissions(@PathVariable Long id, @RequestBody List<Long> permIds) {
        roleService.saveRolePermissions(id, permIds);
        return Result.success("分配成功");
    }

    /**
     * 新增角色
     */
    @SysLogAnnotation(module = ModuleEnum.ROLE_MANAGEMENT, action = ActionEnum.ADD, detail = "新增角色")
    @PostMapping
    @PreAuthorize("hasAuthority('system:role:add')")
    public Result<String> save(@RequestBody Role role) {
        roleService.save(role);
        return Result.success("添加成功");
    }

    /**
     * 修改角色
     */
    @SysLogAnnotation(module = ModuleEnum.ROLE_MANAGEMENT, action = ActionEnum.MODIFY, detail = "修改角色信息")
    @PutMapping
    @PreAuthorize("hasAuthority('system:role:edit')")
    public Result<String> update(@RequestBody Role role) {
        roleService.updateById(role);
        return Result.success("修改成功");
    }

    /**
     * 删除角色
     */
    @SysLogAnnotation(module = ModuleEnum.ROLE_MANAGEMENT, action = ActionEnum.DELETE, detail = "删除角色")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:role:remove')")
    public Result<String> delete(@PathVariable Long id) {
        roleService.removeById(id);
        return Result.success("删除成功");
    }
}
