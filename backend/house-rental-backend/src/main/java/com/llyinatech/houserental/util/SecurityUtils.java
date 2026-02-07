package com.llyinatech.houserental.util;

import com.llyinatech.houserental.security.UserDetailsImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Security相关工具类
 */
public class SecurityUtils {

    /**
     * 获取当前登录用户
     */
    public static UserDetailsImpl getLoginUser() {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
                return (UserDetailsImpl) authentication.getPrincipal();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取当前登录用户ID
     */
    public static Long getUserId() {
        UserDetailsImpl loginUser = getLoginUser();
        return loginUser != null ? loginUser.getId() : null;
    }

    /**
     * 获取当前登录用户名
     */
    public static String getUsername() {
        UserDetailsImpl loginUser = getLoginUser();
        return loginUser != null ? loginUser.getUsername() : null;
    }
}
