package com.llyinatech.houserental.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.llyinatech.houserental.entity.DailyStats;
import com.llyinatech.houserental.mapper.DailyStatsMapper;
import com.llyinatech.houserental.service.DailyStatsService;
import org.springframework.stereotype.Service;

/**
 * 每日运营统计Service实现类
 */
@Service
public class DailyStatsServiceImpl extends ServiceImpl<DailyStatsMapper, DailyStats> implements DailyStatsService {
}
