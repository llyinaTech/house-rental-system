package com.llyinatech.houserental.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.llyinatech.houserental.common.Result;
import com.llyinatech.houserental.entity.LeaseContract;
import com.llyinatech.houserental.service.LeaseContractService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
                                              @RequestParam(defaultValue = "10") Integer size) {
        Page<LeaseContract> page = leaseContractService.page(new Page<>(current, size));
        return Result.success(page);
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
    @PostMapping
    public Result<String> save(@RequestBody LeaseContract leaseContract) {
        leaseContractService.save(leaseContract);
        return Result.success("添加成功");
    }

    /**
     * 修改租赁合同
     */
    @PutMapping
    public Result<String> update(@RequestBody LeaseContract leaseContract) {
        leaseContractService.updateById(leaseContract);
        return Result.success("修改成功");
    }

    /**
     * 删除租赁合同
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        leaseContractService.removeById(id);
        return Result.success("删除成功");
    }
}
