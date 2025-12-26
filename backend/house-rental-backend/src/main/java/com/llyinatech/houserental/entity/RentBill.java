package com.llyinatech.houserental.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 租金账单提醒实体类
 */
@Data
@TableName("rent_bill")
public class RentBill implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 合同ID
     */
    private Long contractId;

    /**
     * 租客ID
     */
    private Long tenantId;

    /**
     * 房东ID
     */
    private Long landlordId;

    /**
     * 期数描述 (如: 2025年6月租金)
     */
    private String periodDesc;

    /**
     * 应缴金额
     */
    private BigDecimal billAmount;

    /**
     * 最晚应缴日期 (用于定时任务扫描)
     */
    private LocalDate dueDate;

    /**
     * 状态: 0-未支付, 1-已支付, 2-逾期
     */
    private Integer payStatus;

    /**
     * 实际支付时间
     */
    private LocalDateTime payTime;

    /**
     * 已发送提醒次数
     */
    private Integer reminderCount;

    /**
     * 上次提醒时间
     */
    private LocalDateTime lastRemindTime;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
