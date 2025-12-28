package com.llyinatech.houserental;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码加密工具类 - 用于生成BCrypt加密密码
 */
public class PasswordEncoderTest {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        
        // 生成密码：123456
        String password = "123456";
        String encodedPassword = encoder.encode(password);

        System.out.println("原始密码: " + password);
        System.out.println("加密后密码: " + encodedPassword);
        System.out.println("验证结果: " + encoder.matches(password, encodedPassword));
    }
}
