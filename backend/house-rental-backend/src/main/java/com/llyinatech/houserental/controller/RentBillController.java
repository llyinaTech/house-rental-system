package com.llyinatech.houserental.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.llyinatech.houserental.annotation.SysLogAnnotation;
import com.llyinatech.houserental.common.Result;
import com.llyinatech.houserental.entity.RentBill;
import com.llyinatech.houserental.enums.ActionEnum;
import com.llyinatech.houserental.enums.ModuleEnum;
import com.llyinatech.houserental.service.RentBillService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Objects;


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
                                       @RequestParam(defaultValue = "10") Integer size,
                                       RentBill rentBill,
                                       @RequestParam(required = false) String dueDateBegin,
                                       @RequestParam(required = false) String dueDateEnd) {
        Page<RentBill> page = new Page<>(current, size);
        LambdaQueryWrapper<RentBill> queryWrapper = new LambdaQueryWrapper<>();

        queryWrapper.eq(Objects.nonNull(rentBill.getTenantId()), RentBill::getTenantId, rentBill.getTenantId());
        queryWrapper.eq(Objects.nonNull(rentBill.getContractId()), RentBill::getContractId, rentBill.getContractId());
        queryWrapper.eq(Objects.nonNull(rentBill.getPayStatus()), RentBill::getPayStatus, rentBill.getPayStatus());

        if (StringUtils.hasText(dueDateBegin)) {
            queryWrapper.ge(RentBill::getDueDate, LocalDate.parse(dueDateBegin));
        }
        if (StringUtils.hasText(dueDateEnd)) {
            queryWrapper.le(RentBill::getDueDate, LocalDate.parse(dueDateEnd));
        }

        queryWrapper.orderByDesc(RentBill::getCreateTime);

        Page<RentBill> result = rentBillService.page(page, queryWrapper);
        return Result.success(result);
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
    @SysLogAnnotation(module = ModuleEnum.FINANCE_MANAGEMENT, action = ActionEnum.ADD, detail = "新增租金账单")
    @PostMapping
    public Result<String> save(@RequestBody RentBill rentBill) {
        rentBillService.save(rentBill);
        return Result.success("添加成功");
    }

    /**
     * 支付账单
     */
    @SysLogAnnotation(module = ModuleEnum.FINANCE_MANAGEMENT, action = ActionEnum.PAY, detail = "支付租金账单")
    @PutMapping("/pay/{id}")
    public Result<String> pay(@PathVariable Long id) {
        RentBill rentBill = rentBillService.getById(id);
        rentBill.setPayStatus(1); // 已支付
        rentBill.setPayTime(LocalDateTime.now());
        rentBillService.updateById(rentBill);
        return Result.success("支付成功");
    }

    /**
     * 删除账单
     */
    @SysLogAnnotation(module = ModuleEnum.FINANCE_MANAGEMENT, action = ActionEnum.DELETE, detail = "删除租金账单")
    @DeleteMapping("/{id}")
    public Result<String> remove(@PathVariable Long id) {
        rentBillService.removeById(id);
        return Result.success("删除成功");
    }
}