package com.llyinatech.houserental.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.llyinatech.houserental.model.dto.SysLogQueryDTO;
import com.llyinatech.houserental.model.entity.SysLog;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * 系统操作日志Service接口
 */
public interface SysLogService extends IService<SysLog> {

    /**
     * 分页查询日志列表
     * @param queryDTO 查询条件
     * @return 日志分页数据
     */
    Page<SysLog> pageQuery(SysLogQueryDTO queryDTO);

    /**
     * 保存操作日志
     * @param module 操作模块
     * @param action 操作类型
     * @param detail 详细信息
     * @param status 操作状态
     */
    void saveLog(String module, String action, String detail, String status);

    /**
     * 导出日志到Excel
     * @param queryDTO 查询条件
     * @param response HTTP响应
     */
    void exportToExcel(SysLogQueryDTO queryDTO, HttpServletResponse response);

    /**
     * 按ID列表导出日志到Excel
     * @param ids 日志ID列表
     * @param response HTTP响应
     */
    void exportToExcelByIds(List<Long> ids, HttpServletResponse response);
}
