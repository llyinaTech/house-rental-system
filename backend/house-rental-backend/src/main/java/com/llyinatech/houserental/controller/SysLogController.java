package com.llyinatech.houserental.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.llyinatech.houserental.annotation.SysLogAnnotation;
import com.llyinatech.houserental.common.Result;
import com.llyinatech.houserental.model.dto.SysLogQueryDTO;
import com.llyinatech.houserental.model.entity.SysLog;
import com.llyinatech.houserental.enums.ActionEnum;
import com.llyinatech.houserental.enums.ModuleEnum;
import com.llyinatech.houserental.service.SysLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 系统日志Controller
 */
@Tag(name = "系统日志管理", description = "系统日志管理接口")
@RestController
@RequestMapping("/api/log")
public class SysLogController {

    @Autowired
    private SysLogService sysLogService;

    /**
     * 分页查询日志列表
     */
    @Operation(summary = "分页查询日志列表")
    @GetMapping("/page")
    public Result<Page<SysLog>> page(SysLogQueryDTO queryDTO) {
        Page<SysLog> page = sysLogService.pageQuery(queryDTO);
        return Result.success(page);
    }

    /**
     * 根据ID查询日志详情
     */
    @Operation(summary = "查询日志详情")
    @GetMapping("/{id}")
    public Result<SysLog> getById(@PathVariable Long id) {
        SysLog sysLog = sysLogService.getById(id);
        return Result.success(sysLog);
    }

    /**
     * 删除日志
     */
    @SysLogAnnotation(module = ModuleEnum.SYSTEM_LOG, action = ActionEnum.DELETE, detail = "删除日志")
    @Operation(summary = "删除日志")
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        sysLogService.removeById(id);
        return Result.success("删除成功");
    }

    /**
     * 批量删除日志
     */
    @SysLogAnnotation(module = ModuleEnum.SYSTEM_LOG, action = ActionEnum.DELETE, detail = "批量删除日志")
    @Operation(summary = "批量删除日志")
    @DeleteMapping("/batch")
    public Result<String> deleteBatch(@RequestBody List<Long> ids) {
        sysLogService.removeByIds(ids);
        return Result.success("批量删除成功");
    }

    /**
     * 导出日志
     */
    @SysLogAnnotation(module = ModuleEnum.SYSTEM_LOG, action = ActionEnum.EXPORT, detail = "导出日志")
    @Operation(summary = "导出日志")
    @GetMapping("/export")
    public void export(SysLogQueryDTO queryDTO,
                       @RequestParam(required = false) List<Long> ids,
                       HttpServletResponse response) {
        if (ids != null && !ids.isEmpty()) {
            sysLogService.exportToExcelByIds(ids, response);
        } else {
            sysLogService.exportToExcel(queryDTO, response);
        }
    }
}
