package com.llyinatech.houserental.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.llyinatech.houserental.common.Result;
import com.llyinatech.houserental.entity.User;
import com.llyinatech.houserental.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 用户信息控制器
 */
@RestController
@RequestMapping("/api/users")
public class UserController {

    @Autowired
    private UserService userService;

    /**
     * 分页查询用户列表
     * GET /api/users?pageNum=1&pageSize=10&keyword=关键词
     */
    @GetMapping
    public Result<Page<User>> getUserList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        Page<User> page = userService.getUserList(pageNum, pageSize, keyword);
        return Result.success(page);
    }

    /**
     * 根据ID查询用户详情
     * GET /api/users/{id}
     */
    @GetMapping("/{id}")
    public Result<User> getUserById(@PathVariable Long id) {
        User user = userService.getUserById(id);
        return Result.success(user);
    }

    /**
     * 添加用户
     * POST /api/users
     */
    @PostMapping
    public Result<Void> addUser(@RequestBody User user) {
        userService.addUser(user);
        return Result.success("添加用户成功", null);
    }

    /**
     * 更新用户信息
     * PUT /api/users/{id}
     */
    @PutMapping("/{id}")
    public Result<Void> updateUser(@PathVariable Long id, @RequestBody User user) {
        user.setId(id);
        userService.updateUser(user);
        return Result.success("更新用户成功", null);
    }

    /**
     * 删除用户
     * DELETE /api/users/{id}
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return Result.success("删除用户成功", null);
    }

    /**
     * 根据用户名查询用户
     * GET /api/users/username/{username}
     */
    @GetMapping("/username/{username}")
    public Result<User> getUserByUsername(@PathVariable String username) {
        User user = userService.getUserByUsername(username);
        return Result.success(user);
    }
}
