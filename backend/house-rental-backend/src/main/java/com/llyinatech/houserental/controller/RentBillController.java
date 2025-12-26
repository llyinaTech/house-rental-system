package com.llyinatech.houserental.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.llyinatech.houserental.common.Result;
import com.llyinatech.houserental.entity.RentBill;
import com.llyinatech.houserental.service.RentBillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 租金账单提醒Controller
 */
@RestController
@RequestMapping("/api/rent-bill")
public class RentBillController {

    @Autowired
    private RentBillService rentBillService;

    /**
     * 分页查询账单列表
     */
    @GetMapping("/page")
    public Result<Page<RentBill>> page(@RequestParam(defaultValue = "1") Integer current,
                                         @RequestParam(defaultValue = "10") Integer size) {
        Page<RentBill> page = rentBillService.page(new Page<>(current, size));
        return Result.success(page);
    }

    /**
     * 根据ID查询账单
     */
    @GetMapping("/{id}")
    public Result<RentBill> getById(@PathVariable Long id) {
        RentBill rentBill = rentBillService.getById(id);
        return Result.success(rentBill);
    }

    /**
     * 新增账单
     */
    @PostMapping
    public Result<String> save(@RequestBody RentBill rentBill) {
        rentBillService.save(rentBill);
        return Result.success("添加成功");
    }

    /**
     * 支付账单
     */
    @PutMapping("/pay/{id}")
    public Result<String> pay(@PathVariable Long id) {
        RentBill rentBill = rentBillService.getById(id);
        rentBill.setPayStatus(1); // 已支付
        rentBillService.updateById(rentBill);
        return Result.success("支付成功");
    }
}
