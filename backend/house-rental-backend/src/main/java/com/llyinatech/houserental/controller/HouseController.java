package com.llyinatech.houserental.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.llyinatech.houserental.common.Result;
import com.llyinatech.houserental.entity.House;
import com.llyinatech.houserental.service.HouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 房源Controller
 */
@RestController
@RequestMapping("/api/house")
public class HouseController {

    @Autowired
    private HouseService houseService;

    /**
     * 分页查询房源列表
     */
    @GetMapping("/page")
    public Result<Page<House>> page(@RequestParam(defaultValue = "1") Integer current,
                                      @RequestParam(defaultValue = "10") Integer size) {
        Page<House> page = houseService.page(new Page<>(current, size));
        return Result.success(page);
    }

    /**
     * 根据ID查询房源
     */
    @GetMapping("/{id}")
    public Result<House> getById(@PathVariable Long id) {
        House house = houseService.getById(id);
        return Result.success(house);
    }

    /**
     * 新增房源
     */
    @PostMapping
    public Result<String> save(@RequestBody House house) {
        houseService.save(house);
        return Result.success("添加成功");
    }

    /**
     * 修改房源
     */
    @PutMapping
    public Result<String> update(@RequestBody House house) {
        houseService.updateById(house);
        return Result.success("修改成功");
    }

    /**
     * 删除房源
     */
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        houseService.removeById(id);
        return Result.success("删除成功");
    }

    /**
     * 批量删除房源
     */
    @DeleteMapping("/batch")
    public Result<String> deleteBatch(@RequestBody List<Long> ids) {
        houseService.removeByIds(ids);
        return Result.success("批量删除成功");
    }
}
