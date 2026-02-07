package com.llyinatech.houserental.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.llyinatech.houserental.annotation.SysLogAnnotation;
import com.llyinatech.houserental.common.Result;
import com.llyinatech.houserental.dto.UserQueryDto;
import com.llyinatech.houserental.entity.Permission;
import com.llyinatech.houserental.entity.User;
import com.llyinatech.houserental.enums.ActionEnum;
import com.llyinatech.houserental.enums.ModuleEnum;
import com.llyinatech.houserental.service.PermissionService;
import com.llyinatech.houserental.service.UserService;
import com.llyinatech.houserental.util.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 用户Controller
 */
@RestController
@RequestMapping("/api/user")
public class UserController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private PermissionService permissionService;

    /**
     * 获取当前用户菜单
     */
    @GetMapping("/menus")
    public Result<List<Permission>> getMenus() {
        Long userId = SecurityUtils.getUserId();
        return Result.success(permissionService.buildUserMenuTree(userId));
    }

    /**
     * 分页查询用户列表
     */
    @GetMapping("/page")
    @PreAuthorize("hasAuthority('system:user:query')")
    public Result<Page<User>> page(UserQueryDto queryDto) {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        if (queryDto.getUsername() != null && !queryDto.getUsername().isEmpty()) {
            wrapper.like(User::getUsername, queryDto.getUsername());
        }
        if (queryDto.getPhone() != null && !queryDto.getPhone().isEmpty()) {
            wrapper.like(User::getPhone, queryDto.getPhone());
        }
        if (queryDto.getStatus() != null) {
            wrapper.eq(User::getStatus, queryDto.getStatus());
        }
        wrapper.orderByDesc(User::getCreateTime);
        Page<User> page = userService.page(new Page<>(queryDto.getCurrent(), queryDto.getSize()), wrapper);
        return Result.success(page);
    }

    /**
     * 根据ID查询用户
     */
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user:query')")
    public Result<User> getById(@PathVariable Long id) {
        User user = userService.getById(id);
        return Result.success(user);
    }

    /**
     * 新增用户
     */
    @SysLogAnnotation(module = ModuleEnum.USER_MANAGEMENT, action = ActionEnum.ADD, detail = "新增用户")
    @PostMapping
    @PreAuthorize("hasAuthority('system:user:add')")
    public Result<String> save(@RequestBody User user) {
        userService.save(user);
        return Result.success("添加成功");
    }

    /**
     * 修改用户
     */
    @SysLogAnnotation(module = ModuleEnum.USER_MANAGEMENT, action = ActionEnum.MODIFY, detail = "修改用户信息")
    @PutMapping
    @PreAuthorize("hasAuthority('system:user:edit')")
    public Result<String> update(@RequestBody User user) {
        userService.updateById(user);
        return Result.success("修改成功");
    }

    /**
     * 删除用户
     */
    @SysLogAnnotation(module = ModuleEnum.USER_MANAGEMENT, action = ActionEnum.DELETE, detail = "删除用户")
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('system:user:remove')")
    public Result<String> delete(@PathVariable Long id) {
        userService.removeById(id);
        return Result.success("删除成功");
    }

    /**
     * 批量删除用户
     */
    @SysLogAnnotation(module = ModuleEnum.USER_MANAGEMENT, action = ActionEnum.DELETE, detail = "批量删除用户")
    @DeleteMapping("/batch")
    @PreAuthorize("hasAuthority('system:user:remove')")
    public Result<String> deleteBatch(@RequestBody List<Long> ids) {
        userService.removeByIds(ids);
        return Result.success("批量删除成功");
    }

    /**
     * 获取用户角色
     */
    @GetMapping("/{id}/roles")
    @PreAuthorize("hasAuthority('system:user:query')")
    public Result<List<Long>> getUserRoles(@PathVariable Long id) {
        return Result.success(userService.getUserRoleIds(id));
    }

    /**
     * 分配用户角色
     */
    @SysLogAnnotation(module = ModuleEnum.USER_MANAGEMENT, action = ActionEnum.MODIFY, detail = "分配用户角色")
    @PostMapping("/{id}/roles")
    @PreAuthorize("hasAuthority('system:user:edit')")
    public Result<String> saveUserRoles(@PathVariable Long id, @RequestBody List<Long> roleIds) {
        userService.saveUserRoles(id, roleIds);
        return Result.success("分配成功");
    }
}
