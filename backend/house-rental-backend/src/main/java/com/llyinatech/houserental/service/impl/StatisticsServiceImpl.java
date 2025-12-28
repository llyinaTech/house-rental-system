package com.llyinatech.houserental.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.llyinatech.houserental.entity.DailyStats;
import com.llyinatech.houserental.entity.House;
import com.llyinatech.houserental.entity.RentBill;
import com.llyinatech.houserental.entity.User;
import com.llyinatech.houserental.mapper.DailyStatsMapper;
import com.llyinatech.houserental.mapper.HouseMapper;
import com.llyinatech.houserental.mapper.RentBillMapper;
import com.llyinatech.houserental.mapper.UserMapper;
import com.llyinatech.houserental.service.StatisticsService;
import com.llyinatech.houserental.vo.StatisticsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final HouseMapper houseMapper;
    private final UserMapper userMapper;
    private final RentBillMapper rentBillMapper;
    private final DailyStatsMapper dailyStatsMapper;

    @Override
    public StatisticsVO getDashboardStats() {
        StatisticsVO vo = new StatisticsVO();

        // 1. 统计房源总数
        Long totalHouses = houseMapper.selectCount(null);
        vo.setTotalHouses(totalHouses.intValue());

        // 2. 统计已租房源 (rentStatus = 1)
        Long rentedHouses = houseMapper.selectCount(new LambdaQueryWrapper<House>()
                .eq(House::getRentStatus, 1));
        vo.setRentedHouses(rentedHouses.intValue());

        // 3. 统计租客总数 (userType = 3)
        Long totalTenants = userMapper.selectCount(new LambdaQueryWrapper<User>()
                .eq(User::getUserType, 3));
        vo.setTotalTenants(totalTenants.intValue());

        // 4. 统计本月收入
        // 查询当月已支付的账单 (payStatus = 1, payTime 在当月)
        LocalDate now = LocalDate.now();
        LocalDate firstDay = now.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDay = now.with(TemporalAdjusters.lastDayOfMonth());

        // 由于payTime是LocalDateTime，这里需要注意边界
        // 或者简单起见，我们假设dueDate在当月且已支付的都算本月收入（具体业务逻辑可调整）
        // 这里采用：状态为已支付，且支付时间在当月
        List<RentBill> monthlyBills = rentBillMapper.selectList(new LambdaQueryWrapper<RentBill>()
                .eq(RentBill::getPayStatus, 1)
                .ge(RentBill::getPayTime, firstDay.atStartOfDay())
                .le(RentBill::getPayTime, lastDay.atTime(23, 59, 59)));

        BigDecimal monthlyIncome = monthlyBills.stream()
                .map(RentBill::getBillAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        vo.setMonthlyIncome(monthlyIncome);

        // 5. 构建房源状态分布图表数据
        // rentStatus: 0-未租, 1-已租, 2-下架
        List<Map<String, Object>> houseStatusStats = new ArrayList<>();
        // 未租
        Long unrented = houseMapper.selectCount(new LambdaQueryWrapper<House>().eq(House::getRentStatus, 0));
        houseStatusStats.add(Map.of("name", "未租", "value", unrented));
        // 已租
        houseStatusStats.add(Map.of("name", "已租", "value", rentedHouses));
        // 下架
        Long offShelf = houseMapper.selectCount(new LambdaQueryWrapper<House>().eq(House::getRentStatus, 2));
        houseStatusStats.add(Map.of("name", "下架", "value", offShelf));
        vo.setHouseStatusStats(houseStatusStats);

        // 6. 构建收入趋势图表数据 (最近6个月)
        // 这里可以使用 daily_stats 表的数据，如果没有历史数据，暂时用当月数据模拟或返回空
        // 假设 daily_stats 已经有数据了，查询最近6个月的数据聚合
        List<Map<String, Object>> incomeTrend = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            LocalDate date = now.minusMonths(i);
            String monthLabel = date.getMonthValue() + "月";
            
            // 查询该月的总收入
            LocalDate start = date.with(TemporalAdjusters.firstDayOfMonth());
            LocalDate end = date.with(TemporalAdjusters.lastDayOfMonth());
            
            // 从 daily_stats 聚合
            List<DailyStats> stats = dailyStatsMapper.selectList(new LambdaQueryWrapper<DailyStats>()
                    .ge(DailyStats::getStatDate, start)
                    .le(DailyStats::getStatDate, end));
            
            BigDecimal income = stats.stream()
                    .map(DailyStats::getTotalIncome)
                    .filter(val -> val != null)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            
            // 如果 daily_stats 没数据，尝试查 rent_bill (作为兜底，虽然性能差一点但保证有数据)
            if (income.compareTo(BigDecimal.ZERO) == 0) {
                 List<RentBill> bills = rentBillMapper.selectList(new LambdaQueryWrapper<RentBill>()
                    .eq(RentBill::getPayStatus, 1)
                    .ge(RentBill::getPayTime, start.atStartOfDay())
                    .le(RentBill::getPayTime, end.atTime(23, 59, 59)));
                 income = bills.stream()
                    .map(RentBill::getBillAmount)
                    .reduce(BigDecimal.ZERO, BigDecimal::add);
            }

            incomeTrend.add(Map.of("name", monthLabel, "value", income));
        }
        vo.setIncomeTrend(incomeTrend);

        return vo;
    }
}
