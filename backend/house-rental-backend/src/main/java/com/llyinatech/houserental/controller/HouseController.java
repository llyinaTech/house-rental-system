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
import com.llyinatech.houserental.service.OssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.json.JSONArray;
import java.util.ArrayList;

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

    @Autowired
    private OssService ossService;

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
        
        // 处理图片签名
        if (page.getRecords() != null) {
            page.getRecords().forEach(this::processHouseImages);
        }
        
        return Result.success(page);
    }

    /**
     * 根据ID查询房源
     */
    @GetMapping("/{id}")
    public Result<House> getById(@PathVariable Long id) {
        House house = houseService.getById(id);
        processHouseImages(house);
        return Result.success(house);
    }

    /**
     * 处理房源图片，生成签名URL
     */
    private void processHouseImages(House house) {
        if (house == null || !StringUtils.hasText(house.getImages())) {
            return;
        }
        try {
            String imagesJson = house.getImages();
            // 处理可能的双重序列化
            if (imagesJson.startsWith("\"") && imagesJson.endsWith("\"")) {
                imagesJson = imagesJson.substring(1, imagesJson.length() - 1).replace("\\\"", "\"");
            }
            
            JSONArray jsonArray = null;
            try {
                jsonArray = new JSONArray(imagesJson);
            } catch (Exception e) {
                // ignore parsing error
            }

            if (jsonArray != null) {
                List<String> signedUrls = new ArrayList<>();
                for (int i = 0; i < jsonArray.length(); i++) {
                    String url = jsonArray.optString(i);
                    if (StringUtils.hasText(url)) {
                        // 签名有效期 1小时
                        try {
                            String signedUrl = ossService.generateSignedUrl(url, 3600);
                            signedUrls.add(signedUrl);
                        } catch (Exception e) {
                            signedUrls.add(url); // 签名失败则保留原URL
                        }
                    }
                }
                // 使用 Gson 重新序列化
                house.setImages(new com.google.gson.Gson().toJson(signedUrls));
            }
        } catch (Exception e) {
            // 仅打印日志，不影响主流程
            e.printStackTrace();
        }
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
