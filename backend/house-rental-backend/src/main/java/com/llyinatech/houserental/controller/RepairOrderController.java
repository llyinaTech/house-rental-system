package com.llyinatech.houserental.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.llyinatech.houserental.common.Result;
import com.llyinatech.houserental.entity.RepairOrder;
import com.llyinatech.houserental.service.RepairOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
                                            @RequestParam(defaultValue = "10") Integer size) {
        Page<RepairOrder> page = repairOrderService.page(new Page<>(current, size));
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
    @PostMapping
    public Result<String> save(@RequestBody RepairOrder repairOrder) {
        repairOrderService.save(repairOrder);
        return Result.success("提交成功");
    }

    /**
     * 修改报修
     */
    @PutMapping
    public Result<String> update(@RequestBody RepairOrder repairOrder) {
        repairOrderService.updateById(repairOrder);
        return Result.success("修改成功");
    }
}
