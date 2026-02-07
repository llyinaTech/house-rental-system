package com.llyinatech.houserental.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.llyinatech.houserental.entity.DictData;
import java.util.List;

/**
 * 字典数据Service接口
 */
public interface DictDataService extends IService<DictData> {
    /**
     * 根据字典类型查询字典数据
     * @param dictType 字典类型
     * @return 字典数据列表
     */
    List<DictData> selectDictDataByType(String dictType);
}
