package com.llyinatech.houserental.entity;

import com.baomidou.mybatisplus.annotation.*;
import com.fasterxml.jackson.annotation.JsonFormat;
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
     * 操作人用户名
     */
    private String username;

    /**
     * 操作模块 (用户管理/房源管理/合同管理/财务管理/系统登录)
     */
    private String module;

    /**
     * 操作类型 (新增/修改/删除/查询/登录/导出)
     */
    private String action;

    /**
     * 操作IP地址
     */
    private String ip;

    /**
     * 操作状态: 成功/失败
     */
    private String status;

    /**
     * 详细信息
     */
    private String detail;

    /**
     * 操作时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss", timezone = "GMT+8")
    private LocalDateTime createTime;
}
