package com.llyinatech.houserental.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.llyinatech.houserental.entity.LeaseContract;
import org.apache.ibatis.annotations.Mapper;

/**
 * 租赁合同Mapper接口
 */
@Mapper
public interface LeaseContractMapper extends BaseMapper<LeaseContract> {
}
