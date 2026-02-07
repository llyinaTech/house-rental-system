package com.llyinatech.houserental.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.llyinatech.houserental.entity.Permission;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface PermissionMapper extends BaseMapper<Permission> {

    List<String> selectPermKeysByUserId(Long userId);

    List<Permission> selectPermissionsByUserId(Long userId);
}

