package com.llyinatech.houserental.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 房屋报修实体类
 */
@Data
@TableName("repair_order")
public class RepairOrder extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 房源ID
     */
    private Long houseId;

    /**
     * 租客ID
     */
    private Long tenantId;

    /**
     * 报修标题
     */
    private String title;

    /**
     * 报修详情
     */
    private String content;

    /**
     * 报修图片JSON
     */
    private String images;

    /**
     * 状态: 0-待处理, 1-处理中, 2-已完结, 3-已取消
     */
    private Integer status;

    /**
     * 处理结果反馈
     */
    private String resultFeedback;

    /**
     * 流转日志: [{"time": "...", "action": "房东接单"}]
     */
    private String processLog;

    /**
     * 房源名称
     */
    @TableField(exist = false)
    private String houseName;

    /**
     * 租客姓名
     */
    @TableField(exist = false)
    private String tenantName;
}
