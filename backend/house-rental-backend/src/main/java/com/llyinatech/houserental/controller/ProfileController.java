package com.llyinatech.houserental.controller;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.llyinatech.houserental.annotation.SysLogAnnotation;
import com.llyinatech.houserental.common.Result;
import com.llyinatech.houserental.enums.ActionEnum;
import com.llyinatech.houserental.enums.ModuleEnum;
import com.llyinatech.houserental.model.entity.User;
import com.llyinatech.houserental.security.UserDetailsImpl;
import com.llyinatech.houserental.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 个人信息Controller
 */
@Tag(name = "个人中心", description = "用户个人信息管理")
@RestController
@RequestMapping("/api/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final UserService userService;
    private final PasswordEncoder passwordEncoder;

    /**
     * 获取个人信息
     */
    @Operation(summary = "获取个人信息")
    @GetMapping
    public Result<User> getProfile() {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return Result.error("未登录");
        }
        User user = userService.getById(userId);
        user.setPassword(null); // Hide password
        return Result.success(user);
    }

    /**
     * 修改个人信息
     */
    @SysLogAnnotation(module = ModuleEnum.USER_MANAGEMENT, action = ActionEnum.MODIFY, detail = "修改个人信息")
    @Operation(summary = "修改个人信息")
    @PutMapping("/info")
    public Result<String> updateProfile(@RequestBody User user) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return Result.error("未登录");
        }
        
        // Prevent modifying critical fields or other users
        user.setId(userId);
        user.setUsername(null); // Cannot change username
        user.setPassword(null); // Cannot change password here
        user.setStatus(null); // Cannot change status
        user.setUserType(null); // Cannot change type
        
        boolean success = userService.updateById(user);
        return success ? Result.success("修改成功") : Result.error("修改失败");
    }

    /**
     * 修改密码
     */
    @SysLogAnnotation(module = ModuleEnum.USER_MANAGEMENT, action = ActionEnum.MODIFY, detail = "修改个人密码")
    @Operation(summary = "修改密码")
    @PutMapping("/password")
    public Result<String> updatePassword(@RequestBody Map<String, String> params) {
        Long userId = getCurrentUserId();
        if (userId == null) {
            return Result.error("未登录");
        }

        String oldPassword = params.get("oldPassword");
        String newPassword = params.get("newPassword");

        if (!StringUtils.hasText(oldPassword) || !StringUtils.hasText(newPassword)) {
            return Result.error("参数不完整");
        }

        User user = userService.getById(userId);
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            return Result.error("旧密码错误");
        }

        String newHash = passwordEncoder.encode(newPassword);
        boolean success = userService.update(new LambdaUpdateWrapper<User>()
                .eq(User::getId, userId)
                .set(User::getPassword, newHash));

        return success ? Result.success("修改成功") : Result.error("修改失败");
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserDetailsImpl) {
            return ((UserDetailsImpl) auth.getPrincipal()).getId();
        }
        return null;
    }
}
