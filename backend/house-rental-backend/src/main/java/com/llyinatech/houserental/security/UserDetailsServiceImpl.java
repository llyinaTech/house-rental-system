package com.llyinatech.houserental.security;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.llyinatech.houserental.entity.User;
import com.llyinatech.houserental.mapper.UserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 自定义UserDetailsService实现类
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserMapper userMapper;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // 查询用户
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUsername, username);
        User user = userMapper.selectOne(queryWrapper);

        if (user == null) {
            throw new UsernameNotFoundException("用户不存在: " + username);
        }

        // 查询用户角色
        List<String> roles = userMapper.selectRoleKeysByUserId(user.getId());

        // 构建UserDetails对象
        return UserDetailsImpl.build(user, roles);
    }
}
