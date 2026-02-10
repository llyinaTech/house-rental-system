package com.llyinatech.houserental.controller;

import com.llyinatech.houserental.common.Result;
import com.llyinatech.houserental.service.StatisticsService;
import com.llyinatech.houserental.model.vo.StatisticsVO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "数据统计", description = "仪表盘及报表统计接口")
@RestController
@RequestMapping("/api/statistics")
@RequiredArgsConstructor
public class StatisticsController {

    private final StatisticsService statisticsService;

    @Operation(summary = "获取仪表盘统计数据")
    @GetMapping("/dashboard")
    public Result<StatisticsVO> getDashboardStats() {
        StatisticsVO stats = statisticsService.getDashboardStats();
        return Result.success(stats);
    }
}
