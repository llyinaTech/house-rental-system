package com.llyinatech.houserental.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.llyinatech.houserental.entity.House;
import com.llyinatech.houserental.exception.BusinessException;
import com.llyinatech.houserental.mapper.HouseMapper;
import com.llyinatech.houserental.service.HouseService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;

/**
 * 房屋信息服务实现类
 */
@Service
public class HouseServiceImpl implements HouseService {

    @Autowired
    private HouseMapper houseMapper;

    @Override
    public Page<House> getHouseList(Integer pageNum, Integer pageSize, String keyword) {
        Page<House> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<House> wrapper = new LambdaQueryWrapper<>();
        
        if (StringUtils.hasText(keyword)) {
            wrapper.and(w -> w.like(House::getTitle, keyword)
                    .or().like(House::getAddress, keyword));
        }
        
        wrapper.orderByDesc(House::getCreateTime);
        return houseMapper.selectPage(page, wrapper);
    }

    @Override
    public House getHouseById(Long id) {
        House house = houseMapper.selectById(id);
        if (house == null) {
            throw new BusinessException("房屋不存在");
        }
        return house;
    }

    @Override
    public void addHouse(House house) {
        house.setCreateTime(LocalDateTime.now());
        house.setUpdateTime(LocalDateTime.now());
        int result = houseMapper.insert(house);
        if (result <= 0) {
            throw new BusinessException("添加房屋失败");
        }
    }

    @Override
    public void updateHouse(House house) {
        House existHouse = houseMapper.selectById(house.getId());
        if (existHouse == null) {
            throw new BusinessException("房屋不存在");
        }
        house.setUpdateTime(LocalDateTime.now());
        int result = houseMapper.updateById(house);
        if (result <= 0) {
            throw new BusinessException("更新房屋失败");
        }
    }

    @Override
    public void deleteHouse(Long id) {
        House house = houseMapper.selectById(id);
        if (house == null) {
            throw new BusinessException("房屋不存在");
        }
        int result = houseMapper.deleteById(id);
        if (result <= 0) {
            throw new BusinessException("删除房屋失败");
        }
    }

    @Override
    public Page<House> getHouseListByLandlordId(Integer pageNum, Integer pageSize, Long landlordId) {
        Page<House> page = new Page<>(pageNum, pageSize);
        LambdaQueryWrapper<House> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(House::getLandlordId, landlordId);
        wrapper.orderByDesc(House::getCreateTime);
        return houseMapper.selectPage(page, wrapper);
    }
}
