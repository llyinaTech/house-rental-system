package com.llyinatech.houserental.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.llyinatech.houserental.annotation.SysLogAnnotation;
import com.llyinatech.houserental.common.Result;
import com.llyinatech.houserental.entity.User;
import com.llyinatech.houserental.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
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

    /**
     * 分页查询用户列表
     */
    @GetMapping("/page")
    public Result<Page<User>> page(@RequestParam(defaultValue = "1") Integer current,
                                     @RequestParam(defaultValue = "10") Integer size) {
        Page<User> page = userService.page(new Page<>(current, size));
        return Result.success(page);
    }

    /**
     * 根据ID查询用户
     */
    @GetMapping("/{id}")
    public Result<User> getById(@PathVariable Long id) {
        User user = userService.getById(id);
        return Result.success(user);
    }

    /**
     * 新增用户
     */
    @SysLogAnnotation(module = "用户管理", action = "新增", detail = "新增用户")
    @PostMapping
    public Result<String> save(@RequestBody User user) {
        userService.save(user);
        return Result.success("添加成功");
    }

    /**
     * 修改用户
     */
    @SysLogAnnotation(module = "用户管理", action = "修改", detail = "修改用户信息")
    @PutMapping
    public Result<String> update(@RequestBody User user) {
        userService.updateById(user);
        return Result.success("修改成功");
    }

    /**
     * 删除用户
     */
    @SysLogAnnotation(module = "用户管理", action = "删除", detail = "删除用户")
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        userService.removeById(id);
        return Result.success("删除成功");
    }

    /**
     * 批量删除用户
     */
    @SysLogAnnotation(module = "用户管理", action = "删除", detail = "批量删除用户")
    @DeleteMapping("/batch")
    public Result<String> deleteBatch(@RequestBody List<Long> ids) {
        userService.removeByIds(ids);
        return Result.success("批量删除成功");
    }
}
