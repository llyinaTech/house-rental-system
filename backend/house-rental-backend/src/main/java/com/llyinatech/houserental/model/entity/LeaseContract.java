package com.llyinatech.houserental.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 租赁合同实体类
 */
@Data
@TableName("lease_contract")
public class LeaseContract extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 合同编号
     */
    private String contractNo;

    /**
     * 房源ID
     */
    private Long houseId;

    /**
     * 房东ID
     */
    private Long landlordId;

    /**
     * 租客ID
     */
    private Long tenantId;

    /**
     * 生效日期
     */
    private LocalDate startDate;

    /**
     * 到期日期
     */
    private LocalDate endDate;

    /**
     * 签署日期
     */
    private LocalDate signDate;

    /**
     * 月租金
     */
    private BigDecimal rentAmount;

    /**
     * 押金
     */
    private BigDecimal depositAmount;

    /**
     * 支付周期(月): 1-月付, 3-季付
     */
    private Integer payPeriod;

    /**
     * PDF文件地址
     */
    private String contractFileUrl;

    /**
     * 状态: 0-待签署, 1-生效中, 2-已到期, 3-已解约, 4-已退租
     */
    private Integer status;

    /**
     * 备注
     */
    private String remark;
}
