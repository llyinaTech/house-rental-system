package com.llyinatech.houserental.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.llyinatech.houserental.annotation.SysLogAnnotation;
import com.llyinatech.houserental.common.Result;
import com.llyinatech.houserental.entity.LeaseContract;
import com.llyinatech.houserental.enums.ActionEnum;
import com.llyinatech.houserental.enums.ModuleEnum;
import com.llyinatech.houserental.service.LeaseContractService;
import com.llyinatech.houserental.security.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.Objects;

/**
 * 租赁合同Controller
 */
@RestController
@RequestMapping("/api/lease-contract")
public class LeaseContractController {

    @Autowired
    private LeaseContractService leaseContractService;

    /**
     * 分页查询租赁合同列表
     */
    @GetMapping("/page")
    public Result<Page<LeaseContract>> page(@RequestParam(defaultValue = "1") Integer current,
                                              @RequestParam(defaultValue = "10") Integer size,
                                              @RequestParam(required = false) String contractNo,
                                              @RequestParam(required = false) Long houseId,
                                              @RequestParam(required = false) Long landlordId,
                                              @RequestParam(required = false) Long tenantId,
                                              @RequestParam(required = false) Integer status,
                                              @RequestParam(required = false) String startDateBegin,
                                              @RequestParam(required = false) String startDateEnd) {
        Page<LeaseContract> page = new Page<>(current, size);
        LambdaQueryWrapper<LeaseContract> wrapper = new LambdaQueryWrapper<>();

        // Get current user and filter by role
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl user = (UserDetailsImpl) auth.getPrincipal();
            String role = user.getRoles().stream().findFirst().orElse("");
            if ("landlord".equals(role)) {
                wrapper.eq(LeaseContract::getLandlordId, user.getId());
            } else if ("tenant".equals(role)) {
                wrapper.eq(LeaseContract::getTenantId, user.getId());
            }
        }

        if (StringUtils.hasText(contractNo)) {
            wrapper.like(LeaseContract::getContractNo, contractNo);
        }
        if (houseId != null) {
            wrapper.eq(LeaseContract::getHouseId, houseId);
        }
        if (landlordId != null) {
            wrapper.eq(LeaseContract::getLandlordId, landlordId);
        }
        if (tenantId != null) {
            wrapper.eq(LeaseContract::getTenantId, tenantId);
        }
        if (status != null) {
            wrapper.eq(LeaseContract::getStatus, status);
        }
        if (StringUtils.hasText(startDateBegin)) {
            wrapper.ge(LeaseContract::getStartDate, LocalDate.parse(startDateBegin));
        }
        if (StringUtils.hasText(startDateEnd)) {
            wrapper.le(LeaseContract::getStartDate, LocalDate.parse(startDateEnd));
        }
        wrapper.orderByDesc(LeaseContract::getCreateTime);

        Page<LeaseContract> result = leaseContractService.page(page, wrapper);
        return Result.success(result);
    }

    /**
     * 根据ID查询租赁合同
     */
    @GetMapping("/{id}")
    public Result<LeaseContract> getById(@PathVariable Long id) {
        LeaseContract leaseContract = leaseContractService.getById(id);
        return Result.success(leaseContract);
    }

    /**
     * 新增租赁合同
     */
    @SysLogAnnotation(module = ModuleEnum.CONTRACT_MANAGEMENT, action = ActionEnum.ADD, detail = "新增租赁合同")
    @PostMapping
    public Result<String> save(@RequestBody LeaseContract leaseContract) {
        leaseContractService.save(leaseContract);
        return Result.success("添加成功");
    }

    /**
     * 修改租赁合同
     */
    @SysLogAnnotation(module = ModuleEnum.CONTRACT_MANAGEMENT, action = ActionEnum.MODIFY, detail = "修改租赁合同")
    @PutMapping
    public Result<String> update(@RequestBody LeaseContract leaseContract) {
        leaseContractService.updateById(leaseContract);
        return Result.success("修改成功");
    }

    /**
     * 删除租赁合同
     */
    @SysLogAnnotation(module = ModuleEnum.CONTRACT_MANAGEMENT, action = ActionEnum.DELETE, detail = "删除租赁合同")
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        leaseContractService.removeById(id);
        return Result.success("删除成功");
    }
}
