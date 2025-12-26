package com.llyinatech.houserental.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 每日运营统计实体类
 */
@Data
@TableName("daily_stats")
public class DailyStats implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 统计日期
     */
    private LocalDate statDate;

    /**
     * 在租合同数
     */
    private Integer activeContracts;

    /**
     * 当日租金收入
     */
    private BigDecimal totalIncome;

    /**
     * 新增报修数
     */
    private Integer newRepairs;
}
