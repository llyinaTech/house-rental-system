package com.llyinatech.houserental.controller;

import com.llyinatech.houserental.annotation.SysLogAnnotation;
import com.llyinatech.houserental.common.Result;
import com.llyinatech.houserental.dto.LoginRequest;
import com.llyinatech.houserental.enums.ActionEnum;
import com.llyinatech.houserental.enums.ModuleEnum;
import com.llyinatech.houserental.security.UserDetailsImpl;
import com.llyinatech.houserental.util.JwtUtil;
import com.llyinatech.houserental.vo.LoginResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 认证控制器
 */
@Tag(name = "认证管理", description = "用户登录、登出、获取当前用户信息等接口")
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    /**
     * 用户登录
     */
    @SysLogAnnotation(module = ModuleEnum.SYSTEM_LOGIN, action = ActionEnum.LOGIN, detail = "用户登录")
    @Operation(summary = "用户登录", description = "通过用户名和密码登录，返回JWT令牌")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "登录成功",
                    content = @Content(schema = @Schema(implementation = LoginResponse.class))),
            @ApiResponse(responseCode = "400", description = "参数校验失败"),
            @ApiResponse(responseCode = "401", description = "用户名或密码错误")
    })
    @PostMapping("/login")
    public Result<LoginResponse> login(
            @Parameter(description = "登录请求信息", required = true)
            @Valid @RequestBody LoginRequest loginRequest) {
        try {
            // 执行认证
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getUsername(),
                            loginRequest.getPassword()
                    )
            );

            // 设置认证信息
            SecurityContextHolder.getContext().setAuthentication(authentication);

            // 获取用户详情
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

            // 生成JWT令牌
            String jwt = jwtUtil.generateToken(userDetails);

            // 获取角色列表
            List<String> roles = userDetails.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .map(role -> role.replace("ROLE_", ""))
                    .collect(Collectors.toList());

            // 构建响应
            LoginResponse response = new LoginResponse(
                    jwt,
                    userDetails.getId(),
                    userDetails.getUsername(),
                    null, // realName可以从User实体中获取
                    roles
            );

            return Result.success(response);
        } catch (Exception e) {
            return Result.error("用户名或密码错误: " + e.getMessage());
        }
    }

    /**
     * 用户登出
     */
    @SysLogAnnotation(module = ModuleEnum.SYSTEM_LOGIN, action = ActionEnum.LOGOUT, detail = "用户登出")
    @Operation(summary = "用户登出", description = "清除当前用户的认证信息")
    @ApiResponse(responseCode = "200", description = "登出成功")
    @PostMapping("/logout")
    public Result<Void> logout() {
        // 清除Security上下文
        SecurityContextHolder.clearContext();
        return Result.success("登出成功", null);
    }

    /**
     * 获取当前用户信息
     */
    @Operation(summary = "获取当前用户信息", description = "获取已登录用户的详细信息")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "获取成功"),
            @ApiResponse(responseCode = "401", description = "未登录或令牌失效")
    })
    @GetMapping("/current")
    public Result<UserDetailsImpl> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        return Result.success(userDetails);
    }
}
