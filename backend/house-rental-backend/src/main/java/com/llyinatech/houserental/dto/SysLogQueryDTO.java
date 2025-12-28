package com.llyinatech.houserental.dto;

import lombok.Data;

/**
 * 系统日志查询DTO
 */
@Data
public class SysLogQueryDTO {

    /**
     * 操作模块
     */
    private String module;

    /**
     * 操作人员
     */
    private String username;

    /**
     * 操作类型
     */
    private String action;

    /**
     * 开始时间
     */
    private String startTime;

    /**
     * 结束时间
     */
    private String endTime;

    /**
     * 当前页
     */
    private Integer current = 1;

    /**
     * 每页大小
     */
    private Integer size = 10;
}
