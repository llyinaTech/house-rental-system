package com.llyinatech.houserental.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 登录响应VO
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "登录响应结果")
public class LoginResponse {

    /**
     * JWT令牌
     */
    @Schema(description = "JWT令牌", example = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...")
    private String token;

    /**
     * 令牌类型
     */
    @Schema(description = "令牌类型", example = "Bearer")
    private String type = "Bearer";

    /**
     * 用户ID
     */
    @Schema(description = "用户ID", example = "1")
    private Long id;

    /**
     * 用户名
     */
    @Schema(description = "用户名", example = "admin")
    private String username;

    /**
     * 真实姓名
     */
    @Schema(description = "真实姓名", example = "系统管理员")
    private String realName;

    /**
     * 角色列表
     */
    @Schema(description = "角色列表", example = "[\"admin\"]")
    private List<String> roles;

    /**
     * 权限列表
     */
    @Schema(description = "权限列表", example = "[\"house:list\",\"rentbill:export\"]")
    private List<String> permissions;

    public LoginResponse(String token, Long id, String username, String realName, List<String> roles, List<String> permissions) {
        this.token = token;
        this.id = id;
        this.username = username;
        this.realName = realName;
        this.roles = roles;
        this.permissions = permissions;
    }
}
