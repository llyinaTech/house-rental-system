package com.llyinatech.houserental.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.llyinatech.houserental.annotation.SysLogAnnotation;
import com.llyinatech.houserental.common.Result;
import com.llyinatech.houserental.entity.Role;
import com.llyinatech.houserental.enums.ActionEnum;
import com.llyinatech.houserental.enums.ModuleEnum;
import com.llyinatech.houserental.service.RoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

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
     * 根据ID查询角色
     */
    @GetMapping("/{id}")
    public Result<Role> getById(@PathVariable Long id) {
        Role role = roleService.getById(id);
        return Result.success(role);
    }

    /**
     * 新增角色
     */
    @SysLogAnnotation(module = ModuleEnum.ROLE_MANAGEMENT, action = ActionEnum.ADD, detail = "新增角色")
    @PostMapping
    public Result<String> save(@RequestBody Role role) {
        roleService.save(role);
        return Result.success("添加成功");
    }

    /**
     * 修改角色
     */
    @SysLogAnnotation(module = ModuleEnum.ROLE_MANAGEMENT, action = ActionEnum.MODIFY, detail = "修改角色信息")
    @PutMapping
    public Result<String> update(@RequestBody Role role) {
        roleService.updateById(role);
        return Result.success("修改成功");
    }

    /**
     * 删除角色
     */
    @SysLogAnnotation(module = ModuleEnum.ROLE_MANAGEMENT, action = ActionEnum.DELETE, detail = "删除角色")
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        roleService.removeById(id);
        return Result.success("删除成功");
    }
}
