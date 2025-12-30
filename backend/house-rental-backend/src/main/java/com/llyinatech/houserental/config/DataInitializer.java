package com.llyinatech.houserental.config;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.llyinatech.houserental.entity.Role;
import com.llyinatech.houserental.entity.User;
import com.llyinatech.houserental.entity.UserRole;
import com.llyinatech.houserental.mapper.RoleMapper;
import com.llyinatech.houserental.mapper.UserMapper;
import com.llyinatech.houserental.mapper.UserRoleMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements ApplicationRunner {

    private final UserMapper userMapper;
    private final RoleMapper roleMapper;
    private final UserRoleMapper userRoleMapper;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void run(ApplicationArguments args) {
        log.info("Checking default users and roles...");
        
        // Initialize Roles
        initRole("admin", "系统管理员");
        initRole("landlord", "房东");
        initRole("tenant", "租客");

        // Initialize Users
        initUser("admin", "系统管理员", 1, "admin");
        initUser("landlord", "张房东", 2, "landlord");
        initUser("tenant", "李租客", 3, "tenant");
    }

    private void initRole(String key, String name) {
        if (roleMapper.selectCount(new LambdaQueryWrapper<Role>().eq(Role::getRoleKey, key)) == 0) {
            Role role = new Role();
            role.setRoleKey(key);
            role.setRoleName(name);
            roleMapper.insert(role);
            log.info("Initialized role: {}", key);
        }
    }

    private void initUser(String username, String realName, Integer userType, String roleKey) {
        User user = userMapper.selectOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null) {
            user = new User();
            user.setUsername(username);
            // Password: 123456 (using known working hash from admin)
            user.setPassword("$2a$10$9/WdP2KLORs8GwcpniDDaOvKOGgzKEOO.647eROrJv3cQREnLNoma");
            user.setRealName(realName);
            user.setUserType(userType);
            user.setStatus(1);
            user.setDeleted(0);
            userMapper.insert(user);
            log.info("Initialized user: {}", username);

            // Assign role
            Role role = roleMapper.selectOne(new LambdaQueryWrapper<Role>().eq(Role::getRoleKey, roleKey));
            if (role != null) {
                UserRole userRole = new UserRole();
                userRole.setUserId(user.getId());
                userRole.setRoleId(role.getId());
                userRoleMapper.insert(userRole);
            }
        } else {
            // Fix password if it matches the known broken hash
            String brokenHash = "$2a$10$dXJ3SW6G7P50lGmMkkmwe.20cQQubK3.HZWzG3YB1tlRy.fqvM/BG";
            if (brokenHash.equals(user.getPassword())) {
                user.setPassword("$2a$10$9/WdP2KLORs8GwcpniDDaOvKOGgzKEOO.647eROrJv3cQREnLNoma");
                userMapper.updateById(user);
                log.info("Fixed broken password for user: {}", username);
            }

            // Check if user has role
            Role role = roleMapper.selectOne(new LambdaQueryWrapper<Role>().eq(Role::getRoleKey, roleKey));
            if (role != null) {
                 Long count = userRoleMapper.selectCount(new LambdaQueryWrapper<UserRole>()
                         .eq(UserRole::getUserId, user.getId())
                         .eq(UserRole::getRoleId, role.getId()));
                 if (count == 0) {
                     UserRole userRole = new UserRole();
                     userRole.setUserId(user.getId());
                     userRole.setRoleId(role.getId());
                     userRoleMapper.insert(userRole);
                     log.info("Assigned role {} to existing user {}", roleKey, username);
                 }
            }
        }
    }
}
