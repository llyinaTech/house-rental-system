package com.llyinatech.houserental.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.llyinatech.houserental.entity.DictType;
import com.llyinatech.houserental.mapper.DictTypeMapper;
import com.llyinatech.houserental.service.DictTypeService;
import org.springframework.stereotype.Service;

/**
 * 字典类型Service业务层处理
 */
@Service
public class DictTypeServiceImpl extends ServiceImpl<DictTypeMapper, DictType> implements DictTypeService {
}
