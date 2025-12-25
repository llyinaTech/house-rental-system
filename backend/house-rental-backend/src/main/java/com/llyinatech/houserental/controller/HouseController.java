package com.llyinatech.houserental.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.llyinatech.houserental.common.Result;
import com.llyinatech.houserental.entity.House;
import com.llyinatech.houserental.service.HouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 房屋信息控制器
 */
@RestController
@RequestMapping("/api/houses")
public class HouseController {

    @Autowired
    private HouseService houseService;

    /**
     * 分页查询房屋列表
     * GET /api/houses?pageNum=1&pageSize=10&keyword=关键词
     */
    @GetMapping
    public Result<Page<House>> getHouseList(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String keyword) {
        Page<House> page = houseService.getHouseList(pageNum, pageSize, keyword);
        return Result.success(page);
    }

    /**
     * 根据ID查询房屋详情
     * GET /api/houses/{id}
     */
    @GetMapping("/{id}")
    public Result<House> getHouseById(@PathVariable Long id) {
        House house = houseService.getHouseById(id);
        return Result.success(house);
    }

    /**
     * 添加房屋
     * POST /api/houses
     */
    @PostMapping
    public Result<Void> addHouse(@RequestBody House house) {
        houseService.addHouse(house);
        return Result.success("添加房屋成功", null);
    }

    /**
     * 更新房屋信息
     * PUT /api/houses/{id}
     */
    @PutMapping("/{id}")
    public Result<Void> updateHouse(@PathVariable Long id, @RequestBody House house) {
        house.setId(id);
        houseService.updateHouse(house);
        return Result.success("更新房屋成功", null);
    }

    /**
     * 删除房屋
     * DELETE /api/houses/{id}
     */
    @DeleteMapping("/{id}")
    public Result<Void> deleteHouse(@PathVariable Long id) {
        houseService.deleteHouse(id);
        return Result.success("删除房屋成功", null);
    }

    /**
     * 根据房东ID查询房屋列表
     * GET /api/houses/landlord/{landlordId}?pageNum=1&pageSize=10
     */
    @GetMapping("/landlord/{landlordId}")
    public Result<Page<House>> getHouseListByLandlordId(
            @PathVariable Long landlordId,
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize) {
        Page<House> page = houseService.getHouseListByLandlordId(pageNum, pageSize, landlordId);
        return Result.success(page);
    }
}
