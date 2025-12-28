package com.llyinatech.houserental.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.llyinatech.houserental.annotation.SysLogAnnotation;
import com.llyinatech.houserental.common.Result;
import com.llyinatech.houserental.entity.Region;
import com.llyinatech.houserental.enums.ActionEnum;
import com.llyinatech.houserental.enums.ModuleEnum;
import com.llyinatech.houserental.service.RegionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 区域商圈Controller
 */
@RestController
@RequestMapping("/api/region")
public class RegionController {

    @Autowired
    private RegionService regionService;

    /**
     * 分页查询区域列表
     */
    @GetMapping("/page")
    public Result<Page<Region>> page(@RequestParam(defaultValue = "1") Integer current,
                                     @RequestParam(defaultValue = "10") Integer size,
                                     @RequestParam(required = false) String name,
                                     @RequestParam(required = false) Integer levelType,
                                     @RequestParam(required = false) Long parentId) {
        LambdaQueryWrapper<Region> wrapper = new LambdaQueryWrapper<>();
        if (StringUtils.hasText(name)) {
            wrapper.like(Region::getName, name);
        }
        if (levelType != null) {
            wrapper.eq(Region::getLevelType, levelType);
        }
        if (parentId != null) {
            wrapper.eq(Region::getParentId, parentId);
        }
        wrapper.orderByAsc(Region::getSortOrder);
        Page<Region> page = regionService.page(new Page<>(current, size), wrapper);
        return Result.success(page);
    }

    /**
     * 查询所有区域（可按层级过滤）
     */
    @GetMapping("/list")
    public Result<List<Region>> list(@RequestParam(required = false) Integer levelType) {
        LambdaQueryWrapper<Region> wrapper = new LambdaQueryWrapper<>();
        if (levelType != null) {
            wrapper.eq(Region::getLevelType, levelType);
        }
        wrapper.orderByAsc(Region::getSortOrder);
        List<Region> list = regionService.list(wrapper);
        return Result.success(list);
    }

    /**
     * 查询区域树形结构
     */
    @GetMapping("/tree")
    public Result<List<Region>> tree() {
        // 1. 查询所有区域
        LambdaQueryWrapper<Region> wrapper = new LambdaQueryWrapper<>();
        wrapper.orderByAsc(Region::getSortOrder);
        List<Region> allList = regionService.list(wrapper);

        // 2. 构建树形结构
        // 2.1 找到所有根节点 (parentId = 0 或 null)
        List<Region> rootList = allList.stream()
                .filter(r -> r.getParentId() == null || r.getParentId() == 0)
                .collect(Collectors.toList());

        // 2.2 递归查找子节点
        for (Region root : rootList) {
            buildChildren(root, allList);
        }

        return Result.success(rootList);
    }

    private void buildChildren(Region parent, List<Region> allList) {
        List<Region> children = allList.stream()
                .filter(r -> parent.getId().equals(r.getParentId()))
                .collect(Collectors.toList());
        
        if (!children.isEmpty()) {
            parent.setChildren(children);
            for (Region child : children) {
                buildChildren(child, allList);
            }
        }
    }

    /**
     * 根据ID查询区域
     */
    @GetMapping("/{id}")
    public Result<Region> getById(@PathVariable Long id) {
        Region region = regionService.getById(id);
        return Result.success(region);
    }

    /**
     * 新增区域
     */
    @SysLogAnnotation(module = ModuleEnum.HOUSE_MANAGEMENT, action = ActionEnum.ADD, detail = "新增区域")
    @PostMapping
    public Result<String> save(@RequestBody Region region) {
        regionService.save(region);
        return Result.success("添加成功");
    }

    /**
     * 修改区域
     */
    @SysLogAnnotation(module = ModuleEnum.HOUSE_MANAGEMENT, action = ActionEnum.MODIFY, detail = "修改区域")
    @PutMapping
    public Result<String> update(@RequestBody Region region) {
        regionService.updateById(region);
        return Result.success("修改成功");
    }

    /**
     * 删除区域
     */
    @SysLogAnnotation(module = ModuleEnum.HOUSE_MANAGEMENT, action = ActionEnum.DELETE, detail = "删除区域")
    @DeleteMapping("/{id}")
    public Result<String> delete(@PathVariable Long id) {
        regionService.removeById(id);
        return Result.success("删除成功");
    }
}
