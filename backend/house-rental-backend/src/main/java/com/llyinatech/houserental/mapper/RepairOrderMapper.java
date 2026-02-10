package com.llyinatech.houserental.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.llyinatech.houserental.model.entity.RepairOrder;
import org.apache.ibatis.annotations.Mapper;

/**
 * 房屋报修Mapper接口
 */
@Mapper
public interface RepairOrderMapper extends BaseMapper<RepairOrder> {
}
