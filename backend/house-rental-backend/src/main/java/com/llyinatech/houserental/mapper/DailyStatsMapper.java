package com.llyinatech.houserental.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.llyinatech.houserental.model.entity.DailyStats;
import org.apache.ibatis.annotations.Mapper;

/**
 * 每日运营统计Mapper接口
 */
@Mapper
public interface DailyStatsMapper extends BaseMapper<DailyStats> {
}
