package com.llyinatech.houserental.runner;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.llyinatech.houserental.entity.DictData;
import com.llyinatech.houserental.entity.DictType;
import com.llyinatech.houserental.service.DictDataService;
import com.llyinatech.houserental.service.DictTypeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * 初始化数据字典数据
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final DictTypeService dictTypeService;
    private final DictDataService dictDataService;

    @Override
    public void run(String... args) throws Exception {
        initDictData();
    }

    private void initDictData() {
        log.info("Checking dictionary data...");

        // Ensure Types exist
        ensureDictType("用户性别", "sys_user_sex", "用户性别列表");
        ensureDictType("菜单状态", "sys_show_hide", "菜单状态列表");
        ensureDictType("系统开关", "sys_normal_disable", "系统开关列表");
        
        // Ensure Data exists
        if (dictDataService.count() == 0) {
            log.info("Initializing dictionary data...");
            saveDictData("sys_user_sex", "男", "0", 1, "Y", "primary");
            saveDictData("sys_user_sex", "女", "1", 2, "N", "danger");
            saveDictData("sys_user_sex", "未知", "2", 3, "N", "info");

            saveDictData("sys_show_hide", "显示", "1", 1, "Y", "primary");
            saveDictData("sys_show_hide", "隐藏", "0", 2, "N", "danger");

            saveDictData("sys_normal_disable", "正常", "1", 1, "Y", "primary");
            saveDictData("sys_normal_disable", "停用", "0", 2, "N", "danger");
            log.info("Dictionary data initialized.");
        }
    }

    private void ensureDictType(String name, String type, String remark) {
        long count = dictTypeService.count(new LambdaQueryWrapper<DictType>().eq(DictType::getDictType, type));
        if (count == 0) {
            DictType dt = new DictType();
            dt.setDictName(name);
            dt.setDictType(type);
            dt.setStatus(1);
            dt.setRemark(remark);
            dt.setCreateTime(LocalDateTime.now());
            dt.setUpdateTime(LocalDateTime.now());
            dictTypeService.save(dt);
            log.info("Initialized dict type: {}", name);
        }
    }

    private void saveDictData(String dictType, String dictLabel, String dictValue, Integer sort, String isDefault, String listClass) {
        DictData data = new DictData();
        data.setDictType(dictType);
        data.setDictLabel(dictLabel);
        data.setDictValue(dictValue);
        data.setDictSort(sort);
        data.setIsDefault(isDefault);
        data.setListClass(listClass);
        data.setStatus(1);
        data.setCreateTime(LocalDateTime.now());
        data.setUpdateTime(LocalDateTime.now());
        dictDataService.save(data);
    }
}
