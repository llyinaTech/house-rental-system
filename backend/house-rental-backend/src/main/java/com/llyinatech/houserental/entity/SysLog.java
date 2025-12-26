package com.llyinatech.houserental.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统操作日志实体类
 */
@Data
@TableName("sys_log")
public class SysLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 操作人ID
     */
    private Long userId;

    /**
     * 模块 (合同/房源/财务)
     */
    private String module;

    /**
     * 动作 (新增/删除/支付)
     */
    private String action;

    /**
     * IP地址
     */
    private String ip;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;
}
