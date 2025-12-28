package com.llyinatech.houserental.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.llyinatech.houserental.annotation.SysLogAnnotation;
import com.llyinatech.houserental.common.Result;
import com.llyinatech.houserental.entity.RepairOrder;
import com.llyinatech.houserental.enums.ActionEnum;
import com.llyinatech.houserental.enums.ModuleEnum;
import com.llyinatech.houserental.service.RepairOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * 房屋报修Controller
 */
@RestController
@RequestMapping("/api/repair-order")
public class RepairOrderController {

    @Autowired
    private RepairOrderService repairOrderService;

    /**
     * 分页查询报修列表
     */
    @GetMapping("/page")
    public Result<Page<RepairOrder>> page(@RequestParam(defaultValue = "1") Integer current,
                                            @RequestParam(defaultValue = "10") Integer size,
                                            @RequestParam(required = false) String title,
                                            @RequestParam(required = false) Integer status,
                                            @RequestParam(required = false) String startDateBegin,
                                            @RequestParam(required = false) String startDateEnd) {
        LambdaQueryWrapper<RepairOrder> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(title)) {
            wrapper.like(RepairOrder::getTitle, title);
        }
        if (Objects.nonNull(status)) {
            wrapper.eq(RepairOrder::getStatus, status);
        }
        DateTimeFormatter df = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        if (StringUtils.hasText(startDateBegin)) {
            LocalDateTime begin = startDateBegin.length() == 10
                    ? LocalDateTime.parse(startDateBegin + " 00:00:00", df)
                    : LocalDateTime.parse(startDateBegin, df);
            wrapper.ge(RepairOrder::getCreateTime, begin);
        }
        if (StringUtils.hasText(startDateEnd)) {
            LocalDateTime end = startDateEnd.length() == 10
                    ? LocalDateTime.parse(startDateEnd + " 23:59:59", df)
                    : LocalDateTime.parse(startDateEnd, df);
            wrapper.le(RepairOrder::getCreateTime, end);
        }
        wrapper.orderByDesc(RepairOrder::getCreateTime);
        Page<RepairOrder> page = repairOrderService.page(new Page<>(current, size), wrapper);
        return Result.success(page);
    }

    /**
     * 根据ID查询报修
     */
    @GetMapping("/{id}")
    public Result<RepairOrder> getById(@PathVariable Long id) {
        RepairOrder repairOrder = repairOrderService.getById(id);
        return Result.success(repairOrder);
    }

    /**
     * 新增报修
     */
    @SysLogAnnotation(module = ModuleEnum.REPAIR_ORDER, action = ActionEnum.ADD, detail = "新增报修")
    @PostMapping
    public Result<String> save(@RequestBody RepairOrder repairOrder) {
        repairOrderService.save(repairOrder);
        return Result.success("提交成功");
    }

    /**
     * 修改报修
     */
    @SysLogAnnotation(module = ModuleEnum.REPAIR_ORDER, action = ActionEnum.MODIFY, detail = "修改报修")
    @PutMapping
    public Result<String> update(@RequestBody RepairOrder repairOrder) {
        repairOrderService.updateById(repairOrder);
        return Result.success("修改成功");
    }

    /**
     * 删除报修
     */
    @SysLogAnnotation(module = ModuleEnum.REPAIR_ORDER, action = ActionEnum.DELETE, detail = "删除报修")
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        repairOrderService.removeById(id);
        return Result.success("删除成功");
    }

    /**
     * 开始处理报修（状态置为处理中）
     */
    @SysLogAnnotation(module = ModuleEnum.REPAIR_ORDER, action = ActionEnum.MODIFY, detail = "开始处理报修")
    @PutMapping("/start/{id}")
    public Result<String> start(@PathVariable Long id) {
        RepairOrder ro = repairOrderService.getById(id);
        if (ro == null) {
            return Result.error("记录不存在");
        }
        ro.setStatus(1);
        ro.setUpdateTime(LocalDateTime.now());
        repairOrderService.updateById(ro);
        return Result.success("已开始处理");
    }
}
