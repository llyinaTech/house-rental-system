package com.llyinatech.houserental.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.llyinatech.houserental.entity.DictData;
import com.llyinatech.houserental.mapper.DictDataMapper;
import com.llyinatech.houserental.service.DictDataService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 字典数据Service业务层处理
 */
@Service
public class DictDataServiceImpl extends ServiceImpl<DictDataMapper, DictData> implements DictDataService {

    @Override
    public List<DictData> selectDictDataByType(String dictType) {
        return this.list(new LambdaQueryWrapper<DictData>()
                .eq(DictData::getDictType, dictType)
                .eq(DictData::getStatus, 1) // 1为正常
                .orderByAsc(DictData::getDictSort));
    }
}
