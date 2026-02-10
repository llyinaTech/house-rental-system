package com.llyinatech.houserental.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.llyinatech.houserental.model.entity.House;
import com.llyinatech.houserental.model.entity.RentBill;
import com.llyinatech.houserental.model.entity.User;
import com.llyinatech.houserental.mapper.DailyStatsMapper;
import com.llyinatech.houserental.mapper.HouseMapper;
import com.llyinatech.houserental.mapper.RentBillMapper;
import com.llyinatech.houserental.mapper.UserMapper;
import com.llyinatech.houserental.service.StatisticsService;
import com.llyinatech.houserental.model.vo.StatisticsVO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.llyinatech.houserental.security.UserDetailsImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

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

        // Get current user and filter by role
        Long landlordId = null;
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl user = (UserDetailsImpl) auth.getPrincipal();
            String role = user.getRoles().stream().findFirst().orElse("");
            if ("landlord".equals(role)) {
                landlordId = user.getId();
            }
        }

        // 1. 统计房源总数
        LambdaQueryWrapper<House> houseWrapper = new LambdaQueryWrapper<>();
        if (landlordId != null) houseWrapper.eq(House::getLandlordId, landlordId);
        Long totalHouses = houseMapper.selectCount(houseWrapper);
        vo.setTotalHouses(totalHouses.intValue());

        // 2. 统计已租房源 (rentStatus = 1)
        LambdaQueryWrapper<House> rentedWrapper = new LambdaQueryWrapper<>();
        rentedWrapper.eq(House::getRentStatus, 1);
        if (landlordId != null) rentedWrapper.eq(House::getLandlordId, landlordId);
        Long rentedHouses = houseMapper.selectCount(rentedWrapper);
        vo.setRentedHouses(rentedHouses.intValue());

        // 3. 统计租客总数 (userType = 3)
        // For landlord, count unique tenants in their active contracts or bills
        // This is complex, simplified: count tenants who rented this landlord's houses
        // Or simplified further: count rented houses as tenant count approximation for now
        // A better way: join query, but keeping it simple for MybatisPlus
        if (landlordId != null) {
            // Approximation: tenants = rented houses count (assuming 1 tenant per house)
             vo.setTotalTenants(rentedHouses.intValue());
        } else {
            Long totalTenants = userMapper.selectCount(new LambdaQueryWrapper<User>()
                    .eq(User::getUserType, 3));
            vo.setTotalTenants(totalTenants.intValue());
        }

        // 4. 统计本月收入
        // 查询当月已支付的账单 (payStatus = 1, payTime 在当月)
        LocalDate now = LocalDate.now();
        LocalDate firstDay = now.with(TemporalAdjusters.firstDayOfMonth());
        LocalDate lastDay = now.with(TemporalAdjusters.lastDayOfMonth());

        LambdaQueryWrapper<RentBill> billWrapper = new LambdaQueryWrapper<>();
        billWrapper.eq(RentBill::getPayStatus, 1)
                   .ge(RentBill::getPayTime, firstDay.atStartOfDay())
                   .le(RentBill::getPayTime, lastDay.atTime(23, 59, 59));
        if (landlordId != null) billWrapper.eq(RentBill::getLandlordId, landlordId);
        
        List<RentBill> monthlyBills = rentBillMapper.selectList(billWrapper);

        BigDecimal monthlyIncome = monthlyBills.stream()
                .map(RentBill::getBillAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        vo.setMonthlyIncome(monthlyIncome);

        // 5. 构建房源状态分布图表数据
        // rentStatus: 0-未租, 1-已租, 2-下架
        List<Map<String, Object>> houseStatusStats = new ArrayList<>();
        // 未租
        LambdaQueryWrapper<House> unrentedWrapper = new LambdaQueryWrapper<>();
        unrentedWrapper.eq(House::getRentStatus, 0);
        if (landlordId != null) unrentedWrapper.eq(House::getLandlordId, landlordId);
        Long unrented = houseMapper.selectCount(unrentedWrapper);
        houseStatusStats.add(Map.of("name", "未租", "value", unrented));
        // 已租
        houseStatusStats.add(Map.of("name", "已租", "value", rentedHouses));
        // 下架
        LambdaQueryWrapper<House> offShelfWrapper = new LambdaQueryWrapper<>();
        offShelfWrapper.eq(House::getRentStatus, 2);
        if (landlordId != null) offShelfWrapper.eq(House::getLandlordId, landlordId);
        Long offShelf = houseMapper.selectCount(offShelfWrapper);
        houseStatusStats.add(Map.of("name", "下架", "value", offShelf));
        vo.setHouseStatusStats(houseStatusStats);

        // 6. 构建收入趋势图表数据 (最近6个月)
        List<Map<String, Object>> incomeTrend = new ArrayList<>();
        for (int i = 5; i >= 0; i--) {
            LocalDate date = now.minusMonths(i);
            String monthLabel = date.getMonthValue() + "月";
            
            LocalDate start = date.with(TemporalAdjusters.firstDayOfMonth());
            LocalDate end = date.with(TemporalAdjusters.lastDayOfMonth());
            
            BigDecimal income = BigDecimal.ZERO;

            // For landlord, we MUST query RentBill directly as DailyStats might not separate by landlord
            // Or if DailyStats supports landlordId, use it. Assuming it doesn't or using RentBill is safer.
            // Admin can use DailyStats for performance if needed, but RentBill is consistent.
            
            LambdaQueryWrapper<RentBill> trendBillWrapper = new LambdaQueryWrapper<>();
            trendBillWrapper.eq(RentBill::getPayStatus, 1)
                            .ge(RentBill::getPayTime, start.atStartOfDay())
                            .le(RentBill::getPayTime, end.atTime(23, 59, 59));
            if (landlordId != null) trendBillWrapper.eq(RentBill::getLandlordId, landlordId);

            List<RentBill> bills = rentBillMapper.selectList(trendBillWrapper);
            income = bills.stream()
                .map(RentBill::getBillAmount)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

            incomeTrend.add(Map.of("name", monthLabel, "value", income));
        }
        vo.setIncomeTrend(incomeTrend);

        return vo;
    }
}
