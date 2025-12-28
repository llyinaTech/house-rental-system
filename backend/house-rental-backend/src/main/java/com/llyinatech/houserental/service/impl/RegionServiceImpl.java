package com.llyinatech.houserental.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.llyinatech.houserental.entity.Region;
import com.llyinatech.houserental.mapper.RegionMapper;
import com.llyinatech.houserental.service.RegionService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 区域商圈Service实现类
 */
@Service
public class RegionServiceImpl extends ServiceImpl<RegionMapper, Region> implements RegionService {

    @Override
    public List<Long> getChildRegionIds(Long parentId) {
        List<Long> result = new ArrayList<>();
        if (parentId == null) {
            return result;
        }

        // 1. 添加自身
        result.add(parentId);

        // 2. 递归查找子节点
        // 为了性能，一次性查出所有节点在内存中处理
        List<Region> allRegions = this.list();

        findChildrenRecursively(parentId, allRegions, result);

        return result;
    }

    private void findChildrenRecursively(Long parentId, List<Region> allRegions, List<Long> result) {
        List<Region> children = allRegions.stream()
                .filter(r -> parentId.equals(r.getParentId()))
                .collect(Collectors.toList());

        for (Region child : children) {
            result.add(child.getId());
            findChildrenRecursively(child.getId(), allRegions, result);
        }
    }
}
