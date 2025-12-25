package com.llyinatech.houserental.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.llyinatech.houserental.entity.House;

/**
 * 房屋信息服务接口
 */
public interface HouseService {

    /**
     * 分页查询房屋列表
     */
    Page<House> getHouseList(Integer pageNum, Integer pageSize, String keyword);

    /**
     * 根据ID查询房屋详情
     */
    House getHouseById(Long id);

    /**
     * 添加房屋
     */
    void addHouse(House house);

    /**
     * 更新房屋信息
     */
    void updateHouse(House house);

    /**
     * 删除房屋
     */
    void deleteHouse(Long id);

    /**
     * 根据房东ID查询房屋列表
     */
    Page<House> getHouseListByLandlordId(Integer pageNum, Integer pageSize, Long landlordId);
}
