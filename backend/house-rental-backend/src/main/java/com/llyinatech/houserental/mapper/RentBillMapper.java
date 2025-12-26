package com.llyinatech.houserental.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.llyinatech.houserental.entity.RentBill;
import org.apache.ibatis.annotations.Mapper;

/**
 * 租金账单提醒Mapper接口
 */
@Mapper
public interface RentBillMapper extends BaseMapper<RentBill> {
}
