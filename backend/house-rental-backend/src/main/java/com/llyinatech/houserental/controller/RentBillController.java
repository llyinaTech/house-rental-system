package com.llyinatech.houserental.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.llyinatech.houserental.annotation.SysLogAnnotation;
import com.llyinatech.houserental.common.Result;
import com.llyinatech.houserental.entity.RentBill;
import com.llyinatech.houserental.enums.ActionEnum;
import com.llyinatech.houserental.enums.ModuleEnum;
import com.llyinatech.houserental.service.RentBillService;
import com.llyinatech.houserental.service.UserService;
import com.llyinatech.houserental.service.LeaseContractService;
import com.llyinatech.houserental.entity.User;
import com.llyinatech.houserental.entity.LeaseContract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;


import com.llyinatech.houserental.security.UserDetailsImpl;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * 租金账单提醒Controller
 */
@RestController
@RequestMapping("/api/rent-bill")
public class RentBillController {

    @Autowired
    private RentBillService rentBillService;

    @Autowired
    private UserService userService;

    @Autowired
    private LeaseContractService leaseContractService;

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

        // Get current user and filter by role
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl user = (UserDetailsImpl) auth.getPrincipal();
            String role = user.getRoles().stream().findFirst().orElse("");
            if ("landlord".equals(role)) {
                queryWrapper.eq(RentBill::getLandlordId, user.getId());
            } else if ("tenant".equals(role)) {
                queryWrapper.eq(RentBill::getTenantId, user.getId());
            }
        }

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

        // Populate names
        if (result.getRecords() != null && !result.getRecords().isEmpty()) {
            List<Long> userIds = result.getRecords().stream().map(RentBill::getTenantId).filter(Objects::nonNull).collect(Collectors.toList());
            userIds.addAll(result.getRecords().stream().map(RentBill::getLandlordId).filter(Objects::nonNull).collect(Collectors.toList()));
            List<Long> contractIds = result.getRecords().stream().map(RentBill::getContractId).filter(Objects::nonNull).collect(Collectors.toList());
            
            Map<Long, String> userMap = new java.util.HashMap<>();
            if (!userIds.isEmpty()) {
                List<User> users = userService.listByIds(userIds);
                if (users != null) {
                    userMap = users.stream().collect(Collectors.toMap(User::getId, u -> StringUtils.hasText(u.getRealName()) ? u.getRealName() : u.getUsername(), (k1, k2) -> k1));
                }
            }
            
            Map<Long, String> contractMap = new java.util.HashMap<>();
            if (!contractIds.isEmpty()) {
                List<LeaseContract> contracts = leaseContractService.listByIds(contractIds);
                if (contracts != null) {
                    contractMap = contracts.stream().collect(Collectors.toMap(LeaseContract::getId, LeaseContract::getContractNo, (k1, k2) -> k1));
                }
            }

            for (RentBill bill : result.getRecords()) {
                if (bill.getTenantId() != null) bill.setTenantName(userMap.get(bill.getTenantId()));
                if (bill.getLandlordId() != null) bill.setLandlordName(userMap.get(bill.getLandlordId()));
                if (bill.getContractId() != null) bill.setContractNo(contractMap.get(bill.getContractId()));
            }
        }

        return Result.success(result);
    }

    /**
     * 根据ID查询账单
     */
    @GetMapping("/{id}")
    public Result<RentBill> getById(@PathVariable Long id) {
        RentBill rentBill = rentBillService.getById(id);
        if (rentBill != null) {
            if (rentBill.getTenantId() != null) {
                User u = userService.getById(rentBill.getTenantId());
                if (u != null) rentBill.setTenantName(StringUtils.hasText(u.getRealName()) ? u.getRealName() : u.getUsername());
            }
            if (rentBill.getLandlordId() != null) {
                User u = userService.getById(rentBill.getLandlordId());
                if (u != null) rentBill.setLandlordName(StringUtils.hasText(u.getRealName()) ? u.getRealName() : u.getUsername());
            }
            if (rentBill.getContractId() != null) {
                LeaseContract c = leaseContractService.getById(rentBill.getContractId());
                if (c != null) rentBill.setContractNo(c.getContractNo());
            }
        }
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