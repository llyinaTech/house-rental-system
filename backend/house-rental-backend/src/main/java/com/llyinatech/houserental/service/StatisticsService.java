package com.llyinatech.houserental.service;

import com.llyinatech.houserental.vo.StatisticsVO;

public interface StatisticsService {
    /**
     * 获取仪表盘统计数据
     */
    StatisticsVO getDashboardStats();
}
