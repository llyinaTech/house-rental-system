package com.llyinatech.houserental.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.llyinatech.houserental.entity.House;
import org.apache.ibatis.annotations.Mapper;

/**
 * 房源信息Mapper接口
 */
@Mapper
public interface HouseMapper extends BaseMapper<House> {
}
