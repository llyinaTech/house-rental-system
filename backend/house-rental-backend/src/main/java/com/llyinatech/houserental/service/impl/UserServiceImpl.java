package com.llyinatech.houserental.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.llyinatech.houserental.entity.User;
import com.llyinatech.houserental.mapper.UserMapper;
import com.llyinatech.houserental.service.UserService;
import org.springframework.stereotype.Service;

/**
 * 用户Service实现类
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
