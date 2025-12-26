package com.llyinatech.houserental.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.llyinatech.houserental.entity.House;
import com.llyinatech.houserental.mapper.HouseMapper;
import com.llyinatech.houserental.service.HouseService;
import org.springframework.stereotype.Service;

/**
 * 房源信息Service实现类
 */
@Service
public class HouseServiceImpl extends ServiceImpl<HouseMapper, House> implements HouseService {
}
