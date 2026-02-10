package com.llyinatech.houserental.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.llyinatech.houserental.common.Result;
import com.llyinatech.houserental.model.entity.House;
import com.llyinatech.houserental.model.entity.Region;
import com.llyinatech.houserental.model.entity.User;
import com.llyinatech.houserental.service.HouseService;
import com.llyinatech.houserental.service.RegionService;
import com.llyinatech.houserental.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 诊断控制器 - 用于排查登录问题
 * 注意：生产环境应删除此控制器
 */
@RestController
@RequestMapping("/api/diag")
@RequiredArgsConstructor
public class DiagnosticController {

    private final UserService userService;
    private final RegionService regionService;
    private final HouseService houseService;
    private final PasswordEncoder passwordEncoder;

    /**
     * 初始化标准区域数据 (省-市-区)
     */
    @GetMapping("/init-regions")
    public Result<String> initRegions() {
        try {
            // 1. 清空现有区域数据
            regionService.remove(new LambdaQueryWrapper<>());
            
            // 2. 准备数据
            List<Region> regions = new ArrayList<>();
            
            // Level 1: 省/直辖市
            addRegion(regions, 110000L, 0L, "北京市", 1, 1);
            addRegion(regions, 310000L, 0L, "上海市", 1, 2);
            addRegion(regions, 440000L, 0L, "广东省", 1, 3);
            addRegion(regions, 330000L, 0L, "浙江省", 1, 4);

            // Level 2: 市/市辖区
            addRegion(regions, 110100L, 110000L, "北京市", 2, 1); // 北京市辖区
            addRegion(regions, 310100L, 310000L, "上海市", 2, 1); // 上海市辖区
            addRegion(regions, 440100L, 440000L, "广州市", 2, 1);
            addRegion(regions, 440300L, 440000L, "深圳市", 2, 2);
            addRegion(regions, 330100L, 330000L, "杭州市", 2, 1);

            // Level 3: 区/县
            // 北京
            addRegion(regions, 110101L, 110100L, "东城区", 3, 1);
            addRegion(regions, 110102L, 110100L, "西城区", 3, 2);
            addRegion(regions, 110105L, 110100L, "朝阳区", 3, 3);
            addRegion(regions, 110108L, 110100L, "海淀区", 3, 4);
            // 上海
            addRegion(regions, 310101L, 310100L, "黄浦区", 3, 1);
            addRegion(regions, 310104L, 310100L, "徐汇区", 3, 2);
            addRegion(regions, 310115L, 310100L, "浦东新区", 3, 3);
            // 广州
            addRegion(regions, 440106L, 440100L, "天河区", 3, 1);
            addRegion(regions, 440104L, 440100L, "越秀区", 3, 2);
            // 深圳
            addRegion(regions, 440304L, 440300L, "福田区", 3, 1);
            addRegion(regions, 440305L, 440300L, "南山区", 3, 2);
            // 杭州
            addRegion(regions, 330106L, 330100L, "西湖区", 3, 1);
            addRegion(regions, 330110L, 330100L, "余杭区", 3, 2);

            // 3. 批量插入
            regionService.saveBatch(regions);

            // 4. 重置房源区域关联 (设为NULL，避免关联到不存在的ID)
            houseService.update(new LambdaUpdateWrapper<House>().set(House::getRegionId, null));

            return Result.success("区域数据初始化成功！房源区域已重置，请重新编辑房源分配区域。");
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("初始化失败: " + e.getMessage());
        }
    }

    private void addRegion(List<Region> list, Long id, Long parentId, String name, Integer level, Integer sort) {
        Region r = new Region();
        r.setId(id);
        r.setParentId(parentId);
        r.setName(name);
        r.setLevelType(level);
        r.setSortOrder(sort);
        list.add(r);
    }


    /**
     * 检查用户是否存在及密码哈希
     */
    @GetMapping("/check-user")
    public Result<Map<String, Object>> checkUser(@RequestParam String username) {
        User user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null) {
            return Result.error("用户不存在");
        }

        Map<String, Object> info = new HashMap<>();
        info.put("id", user.getId());
        info.put("username", user.getUsername());
        info.put("passwordHash", user.getPassword());
        info.put("status", user.getStatus());
        return Result.success(info);
    }

    /**
     * 检查区域搜索逻辑
     */
    @GetMapping("/check-region")
    public Result<Map<String, Object>> checkRegion(@RequestParam String regionName) {
        Map<String, Object> result = new HashMap<>();

        // 1. 查找目标区域
        Region targetRegion = regionService.getOne(new LambdaQueryWrapper<Region>()
                .like(Region::getName, regionName)
                .last("LIMIT 1"));
        
        if (targetRegion == null) {
            return Result.error("未找到区域: " + regionName);
        }
        result.put("targetRegion", targetRegion);

        // 2. 获取所有子区域ID
        List<Long> childIds = regionService.getChildRegionIds(targetRegion.getId());
        result.put("childIds", childIds);

        // 3. 查找该范围内的房源
        List<House> housesInRegion = houseService.list(new LambdaQueryWrapper<House>()
                .in(House::getRegionId, childIds));
        result.put("housesInRegion", housesInRegion);
        result.put("housesInRegionCount", housesInRegion.size());

        // 4. 查找所有房源并附带区域信息，用于对比
        List<House> allHouses = houseService.list();
        List<Map<String, Object>> allHousesInfo = allHouses.stream().map(h -> {
            Map<String, Object> map = new HashMap<>();
            map.put("id", h.getId());
            map.put("title", h.getTitle());
            map.put("regionId", h.getRegionId());
            Region r = regionService.getById(h.getRegionId());
            map.put("regionName", r != null ? r.getName() : "Unknown");
            map.put("regionParentId", r != null ? r.getParentId() : null);
            map.put("isInSearch", childIds.contains(h.getRegionId()));
            return map;
        }).collect(Collectors.toList());
        
        result.put("allHousesSample", allHousesInfo);

        return Result.success(result);
    }

    /**
     * 验证密码是否匹配
     */
    @PostMapping("/verify-password")
    public Result<String> verifyPassword(@RequestParam String username, @RequestParam String rawPassword) {
        User user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        boolean matches = passwordEncoder.matches(rawPassword, user.getPassword());
        if (matches) {
            return Result.success("密码验证通过！");
        } else {
            return Result.error("密码验证失败。输入的密码与数据库哈希不匹配。");
        }
    }

    /**
     * 强制重置密码
     */
    @PostMapping("/reset-password")
    public Result<String> resetPassword(@RequestParam String username, @RequestParam String newPassword) {
        User user = userService.getOne(new LambdaQueryWrapper<User>().eq(User::getUsername, username));
        if (user == null) {
            return Result.error("用户不存在");
        }
        
        String newHash = passwordEncoder.encode(newPassword);
        
        boolean update = userService.update(new LambdaUpdateWrapper<User>()
                .eq(User::getUsername, username)
                .set(User::getPassword, newHash));
                
        if (update) {
            return Result.success("密码重置成功！新密码哈希已写入数据库。");
        } else {
            return Result.error("密码重置失败，数据库更新未生效。");
        }
    }

    /**
     * 修复房源区域数据
     */
    @GetMapping("/fix-data")
    public Result<String> fixData() {
        try {
            StringBuilder sb = new StringBuilder();
            
            // 1. 将地址包含"朝阳区"且区域ID为1(北京市)的房源迁移到5(朝阳区)
            List<House> chaoyangHouses = houseService.list(new LambdaQueryWrapper<House>()
                    .eq(House::getRegionId, 1L)
                    .like(House::getAddress, "朝阳区"));
            
            for (House h : chaoyangHouses) {
                h.setRegionId(5L); // 朝阳区ID
                houseService.updateById(h);
                sb.append(String.format("房源[%s]已从北京市迁移到朝阳区; ", h.getTitle()));
            }
            
            // 2. 检查House 3 (id=3)
            House h3 = houseService.getById(3L);
            if (h3 != null && h3.getAddress() != null && h3.getAddress().contains("北京")) {
                 // 如果是北京的，暂时迁到北京市(1)或者根据地址判断
                 if (h3.getAddress().contains("朝阳区")) {
                     h3.setRegionId(5L);
                     sb.append("房源[3]已迁移到朝阳区; ");
                 } else {
                     h3.setRegionId(1L);
                     sb.append("房源[3]已迁移到北京市; ");
                 }
                 houseService.updateById(h3);
            }

            if (sb.length() == 0) {
                return Result.success("没有需要修复的数据");
            }
            return Result.success(sb.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return Result.error("修复失败: " + e.getMessage());
        }
    }
}
