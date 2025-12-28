package com.llyinatech.houserental.vo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
public class StatisticsVO {
    // 概览数据
    private Integer totalHouses;      // 房源总数
    private Integer rentedHouses;     // 已租房源
    private Integer totalTenants;     // 租客总数
    private BigDecimal monthlyIncome; // 本月收入
    
    // 图表数据
    private List<Map<String, Object>> houseStatusStats; // 房源状态分布
    private List<Map<String, Object>> incomeTrend;      // 收入趋势
}
