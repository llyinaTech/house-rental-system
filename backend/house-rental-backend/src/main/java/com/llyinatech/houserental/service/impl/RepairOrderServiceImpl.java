package com.llyinatech.houserental.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.llyinatech.houserental.model.entity.RepairOrder;
import com.llyinatech.houserental.mapper.RepairOrderMapper;
import com.llyinatech.houserental.service.RepairOrderService;
import org.springframework.stereotype.Service;

/**
 * 房屋报修Service实现类
 */
@Service
public class RepairOrderServiceImpl extends ServiceImpl<RepairOrderMapper, RepairOrder> implements RepairOrderService {
}
