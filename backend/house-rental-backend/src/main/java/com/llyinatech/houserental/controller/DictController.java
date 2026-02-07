package com.llyinatech.houserental.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.llyinatech.houserental.common.Result;
import com.llyinatech.houserental.entity.DictData;
import com.llyinatech.houserental.entity.DictType;
import com.llyinatech.houserental.service.DictDataService;
import com.llyinatech.houserental.service.DictTypeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 数据字典信息
 */
@RestController
@RequestMapping("/api/dict")
public class DictController {

    @Autowired
    private DictTypeService dictTypeService;

    @Autowired
    private DictDataService dictDataService;

    /**
     * 查询字典类型列表
     */
    @GetMapping("/type/list")
    @PreAuthorize("hasAuthority('system:dict:query')")
    public Result<Page<DictType>> listType(@RequestParam(defaultValue = "1") Integer current,
                                           @RequestParam(defaultValue = "10") Integer size,
                                           @RequestParam(required = false) String dictName,
                                           @RequestParam(required = false) String dictType,
                                           @RequestParam(required = false) Integer status) {
        Page<DictType> page = new Page<>(current, size);
        LambdaQueryWrapper<DictType> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.hasText(dictName), DictType::getDictName, dictName);
        queryWrapper.like(StringUtils.hasText(dictType), DictType::getDictType, dictType);
        queryWrapper.eq(status != null, DictType::getStatus, status);
        queryWrapper.orderByDesc(DictType::getCreateTime);
        return Result.success(dictTypeService.page(page, queryWrapper));
    }

    /**
     * 查询字典类型详细
     */
    @GetMapping("/type/{dictId}")
    @PreAuthorize("hasAuthority('system:dict:query')")
    public Result<DictType> getTypeInfo(@PathVariable Long dictId) {
        return Result.success(dictTypeService.getById(dictId));
    }

    /**
     * 新增字典类型
     */
    @PostMapping("/type")
    @PreAuthorize("hasAuthority('system:dict:add')")
    public Result<Boolean> addType(@RequestBody DictType dict) {
        // 校验dictType唯一性
        long count = dictTypeService.count(new LambdaQueryWrapper<DictType>().eq(DictType::getDictType, dict.getDictType()));
        if (count > 0) {
            return Result.error("新增字典'" + dict.getDictName() + "'失败，字典类型已存在");
        }
        return Result.success(dictTypeService.save(dict));
    }

    /**
     * 修改字典类型
     */
    @PutMapping("/type")
    @PreAuthorize("hasAuthority('system:dict:edit')")
    public Result<Boolean> editType(@RequestBody DictType dict) {
        // 校验dictType唯一性
        long count = dictTypeService.count(new LambdaQueryWrapper<DictType>()
                .eq(DictType::getDictType, dict.getDictType())
                .ne(DictType::getDictId, dict.getDictId()));
        if (count > 0) {
            return Result.error("修改字典'" + dict.getDictName() + "'失败，字典类型已存在");
        }
        return Result.success(dictTypeService.updateById(dict));
    }

    /**
     * 删除字典类型
     */
    @DeleteMapping("/type/{dictIds}")
    @PreAuthorize("hasAuthority('system:dict:remove')")
    public Result<Boolean> removeType(@PathVariable Long[] dictIds) {
        return Result.success(dictTypeService.removeByIds(Arrays.asList(dictIds)));
    }

    /**
     * 查询字典数据列表
     */
    @GetMapping("/data/list")
    @PreAuthorize("hasAuthority('system:dict:query')")
    public Result<Page<DictData>> listData(@RequestParam(defaultValue = "1") Integer current,
                                           @RequestParam(defaultValue = "10") Integer size,
                                           @RequestParam(required = false) String dictType,
                                           @RequestParam(required = false) String dictLabel,
                                           @RequestParam(required = false) Integer status) {
        Page<DictData> page = new Page<>(current, size);
        LambdaQueryWrapper<DictData> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.hasText(dictType), DictData::getDictType, dictType);
        queryWrapper.like(StringUtils.hasText(dictLabel), DictData::getDictLabel, dictLabel);
        queryWrapper.eq(status != null, DictData::getStatus, status);
        queryWrapper.orderByAsc(DictData::getDictSort);
        return Result.success(dictDataService.page(page, queryWrapper));
    }

    /**
     * 根据字典类型查询字典数据信息 (For Select Options)
     */
    @GetMapping("/data/type/{dictType}")
    public Result<List<DictData>> getDicts(@PathVariable String dictType) {
        return Result.success(dictDataService.selectDictDataByType(dictType));
    }

    /**
     * 查询字典数据详细
     */
    @GetMapping("/data/{dictCode}")
    @PreAuthorize("hasAuthority('system:dict:query')")
    public Result<DictData> getDataInfo(@PathVariable Long dictCode) {
        return Result.success(dictDataService.getById(dictCode));
    }

    /**
     * 新增字典数据
     */
    @PostMapping("/data")
    @PreAuthorize("hasAuthority('system:dict:add')")
    public Result<Boolean> addData(@RequestBody DictData dict) {
        return Result.success(dictDataService.save(dict));
    }

    /**
     * 修改字典数据
     */
    @PutMapping("/data")
    @PreAuthorize("hasAuthority('system:dict:edit')")
    public Result<Boolean> editData(@RequestBody DictData dict) {
        return Result.success(dictDataService.updateById(dict));
    }

    /**
     * 删除字典数据
     */
    @DeleteMapping("/data/{dictCodes}")
    @PreAuthorize("hasAuthority('system:dict:remove')")
    public Result<Boolean> removeData(@PathVariable Long[] dictCodes) {
        return Result.success(dictDataService.removeByIds(Arrays.asList(dictCodes)));
    }
}
