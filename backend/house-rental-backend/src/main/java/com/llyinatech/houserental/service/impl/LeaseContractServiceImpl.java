package com.llyinatech.houserental.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.llyinatech.houserental.model.entity.LeaseContract;
import com.llyinatech.houserental.mapper.LeaseContractMapper;
import com.llyinatech.houserental.service.LeaseContractService;
import org.springframework.stereotype.Service;

/**
 * 租赁合同Service实现类
 */
@Service
public class LeaseContractServiceImpl extends ServiceImpl<LeaseContractMapper, LeaseContract> implements LeaseContractService {
}
