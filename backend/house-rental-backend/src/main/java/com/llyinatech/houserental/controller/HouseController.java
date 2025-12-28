package com.llyinatech.houserental.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.llyinatech.houserental.annotation.SysLogAnnotation;
import com.llyinatech.houserental.common.Result;
import com.llyinatech.houserental.entity.House;
import com.llyinatech.houserental.enums.ActionEnum;
import com.llyinatech.houserental.enums.ModuleEnum;
import com.llyinatech.houserental.service.HouseService;
import com.llyinatech.houserental.service.RegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import com.llyinatech.houserental.entity.Region;

import java.util.List;

/**
 * 房源Controller
 */
@RestController
@RequestMapping("/api/house")
public class HouseController {

    @Autowired
    private HouseService houseService;

    @Autowired
    private RegionService regionService;

    /**
     * 分页查询房源列表
     */
    @GetMapping("/page")
    public Result<Page<House>> page(@RequestParam(defaultValue = "1") Integer current,
                                    @RequestParam(defaultValue = "10") Integer size,
                                    @RequestParam(required = false) String title,
                                    @RequestParam(required = false) Long regionId,
                                    @RequestParam(required = false) Integer rentStatus,
                                    @RequestParam(required = false) Integer auditStatus) {
        LambdaQueryWrapper<House> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(title)) {
            wrapper.like(House::getTitle, title);
        }
        if (regionId != null) {
            // 健壮的区域搜索逻辑：
            // 1. 获取该区域下的所有子区域ID（保证向下兼容）
            List<Long> regionIds = regionService.getChildRegionIds(regionId);
            
            // 2. 获取该区域的名称（用于文本兜底匹配）
            Region region = regionService.getById(regionId);
            String regionName = (region != null) ? region.getName() : null;

            // 3. 构建组合查询条件: (region_id IN (ids) OR address LIKE '%name%')
            wrapper.and(w -> {
                if (!regionIds.isEmpty()) {
                    w.in(House::getRegionId, regionIds);
                } else {
                    w.eq(House::getRegionId, regionId);
                }
                
                if (StringUtils.hasText(regionName)) {
                    w.or().like(House::getAddress, regionName);
                }
            });
        }
        if (rentStatus != null) {
            wrapper.eq(House::getRentStatus, rentStatus);
        }
        if (auditStatus != null) {
            wrapper.eq(House::getAuditStatus, auditStatus);
        }
        wrapper.orderByDesc(House::getCreateTime);
        
        Page<House> page = houseService.page(new Page<>(current, size), wrapper);
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
    @SysLogAnnotation(module = ModuleEnum.HOUSE_MANAGEMENT, action = ActionEnum.ADD, detail = "新增房源")
    @PostMapping
    public Result<String> save(@RequestBody House house) {
        houseService.save(house);
        return Result.success("添加成功");
    }

    /**
     * 修改房源
     */
    @SysLogAnnotation(module = ModuleEnum.HOUSE_MANAGEMENT, action = ActionEnum.MODIFY, detail = "修改房源")
    @PutMapping
    public Result<String> update(@RequestBody House house) {
        houseService.updateById(house);
        return Result.success("修改成功");
    }

    /**
     * 删除房源
     */
    @SysLogAnnotation(module = ModuleEnum.HOUSE_MANAGEMENT, action = ActionEnum.DELETE, detail = "删除房源")
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        houseService.removeById(id);
        return Result.success("删除成功");
    }

    /**
     * 批量删除房源
     */
    @SysLogAnnotation(module = ModuleEnum.HOUSE_MANAGEMENT, action = ActionEnum.DELETE, detail = "批量删除房源")
    @DeleteMapping("/batch")
    public Result<String> deleteBatch(@RequestBody List<Long> ids) {
        houseService.removeByIds(ids);
        return Result.success("批量删除成功");
    }
}
