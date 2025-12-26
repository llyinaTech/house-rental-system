package com.llyinatech.houserental.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.llyinatech.houserental.entity.Region;
import com.llyinatech.houserental.mapper.RegionMapper;
import com.llyinatech.houserental.service.RegionService;
import org.springframework.stereotype.Service;

/**
 * 区域商圈Service实现类
 */
@Service
public class RegionServiceImpl extends ServiceImpl<RegionMapper, Region> implements RegionService {
}
