package com.llyinatech.houserental.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.llyinatech.houserental.entity.Role;
import com.llyinatech.houserental.mapper.RoleMapper;
import com.llyinatech.houserental.service.RoleService;
import org.springframework.stereotype.Service;

/**
 * 角色Service实现类
 */
@Service
public class RoleServiceImpl extends ServiceImpl<RoleMapper, Role> implements RoleService {
}
