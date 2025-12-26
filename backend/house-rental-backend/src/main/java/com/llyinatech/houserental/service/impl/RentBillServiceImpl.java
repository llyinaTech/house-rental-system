package com.llyinatech.houserental.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.llyinatech.houserental.entity.RentBill;
import com.llyinatech.houserental.mapper.RentBillMapper;
import com.llyinatech.houserental.service.RentBillService;
import org.springframework.stereotype.Service;

/**
 * 租金账单提醒Service实现类
 */
@Service
public class RentBillServiceImpl extends ServiceImpl<RentBillMapper, RentBill> implements RentBillService {
}
