package com.llyinatech.houserental.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.llyinatech.houserental.model.entity.Region;

import java.util.List;

/**
 * 区域商圈Service接口
 */
public interface RegionService extends IService<Region> {
    /**
     * 获取指定区域及其所有子区域的ID列表
     * @param parentId 父区域ID
     * @return ID列表
     */
    List<Long> getChildRegionIds(Long parentId);
}
