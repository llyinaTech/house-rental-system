package com.llyinatech.houserental.controller;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.llyinatech.houserental.annotation.SysLogAnnotation;
import com.llyinatech.houserental.common.Result;
import com.llyinatech.houserental.entity.Announcement;
import com.llyinatech.houserental.security.UserDetailsImpl;
import com.llyinatech.houserental.service.AnnouncementService;
import com.llyinatech.houserental.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 公告控制器
 */
@Tag(name = "公告管理", description = "公告管理相关接口")
@RestController
@RequestMapping("/announcements")
@RequiredArgsConstructor
public class AnnouncementController {

    private final AnnouncementService announcementService;
    private final UserService userService;

    /**
     * 分页查询公告
     */
    @Operation(summary = "分页查询公告")
    @GetMapping
    public Result<IPage<Announcement>> findAnnouncements(
            @RequestParam(defaultValue = "1") Integer pageNum,
            @RequestParam(defaultValue = "10") Integer pageSize,
            @RequestParam(required = false) String title,
            @RequestParam(required = false) Integer status) {
        Page<Announcement> page = new Page<>(pageNum, pageSize);
        IPage<Announcement> result = announcementService.findAnnouncements(page, title, status);
        return Result.success(result);
    }

    /**
     * 获取已发布的公告列表
     */
    @Operation(summary = "获取已发布的公告列表")
    @GetMapping("/published")
    public Result<List<Announcement>> getPublishedAnnouncements() {
        List<Announcement> announcements = announcementService.getPublishedAnnouncements();
        return Result.success(announcements);
    }

    /**
     * 根据ID获取公告详情
     */
    @Operation(summary = "根据ID获取公告详情")
    @GetMapping("/{id}")
    public Result<Announcement> getAnnouncement(@PathVariable Long id) {
        Announcement announcement = announcementService.getById(id);
        return Result.success(announcement);
    }

    /**
     * 新增公告
     */
    @SysLogAnnotation(module = "公告管理", action = "新增", detail = "新增公告")
    @Operation(summary = "新增公告")
    @PostMapping
    public Result<String> addAnnouncement(@RequestBody Announcement announcement) {
        // 获取当前用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof UserDetailsImpl)) {
            return Result.error("用户未登录");
        }
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        // 获取用户真实姓名
        String realName = getUserRealName(userDetails.getId(), userDetails.getUsername());
        announcementService.publishAnnouncement(announcement, userDetails.getId(), realName);
        return Result.success("新增公告成功");
    }

    /**
     * 修改公告
     */
    @SysLogAnnotation(module = "公告管理", action = "修改", detail = "修改公告")
    @Operation(summary = "修改公告")
    @PutMapping("/{id}")
    public Result<String> updateAnnouncement(@PathVariable Long id, @RequestBody Announcement announcement) {
        announcement.setId(id);
        // 获取当前用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof UserDetailsImpl)) {
            return Result.error("用户未登录");
        }
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        // 获取用户真实姓名
        String realName = getUserRealName(userDetails.getId(), userDetails.getUsername());
        announcementService.publishAnnouncement(announcement, userDetails.getId(), realName);
        return Result.success("修改公告成功");
    }

    /**
     * 删除公告
     */
    @SysLogAnnotation(module = "公告管理", action = "删除", detail = "删除公告")
    @Operation(summary = "删除公告")
    @DeleteMapping("/{id}")
    public Result<String> deleteAnnouncement(@PathVariable Long id) {
        announcementService.removeById(id);
        return Result.success("删除成功");
    }

    /**
     * 批量删除公告
     */
    @SysLogAnnotation(module = "公告管理", action = "删除", detail = "批量删除公告")
    @Operation(summary = "批量删除公告")
    @DeleteMapping("/batch")
    public Result<String> deleteBatch(@RequestBody List<Long> ids) {
        announcementService.removeByIds(ids);
        return Result.success("批量删除成功");
    }

    /**
     * 发布公告
     */
    @SysLogAnnotation(module = "公告管理", action = "发布", detail = "发布公告")
    @Operation(summary = "发布公告")
    @PutMapping("/publish/{id}")
    public Result<String> publishAnnouncement(@PathVariable Long id) {
        // 获取当前用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (!(authentication.getPrincipal() instanceof UserDetailsImpl)) {
            return Result.error("用户未登录");
        }
        
        Announcement announcement = announcementService.getById(id);
        if (announcement == null) {
            return Result.error("公告不存在");
        }
        
        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        // 获取用户真实姓名
        String realName = getUserRealName(userDetails.getId(), userDetails.getUsername());
        announcementService.publishAnnouncement(announcement, userDetails.getId(), realName);
        return Result.success("发布公告成功");
    }

    /**
     * 撤销公告
     */
    @SysLogAnnotation(module = "公告管理", action = "撤销", detail = "撤销公告")
    @Operation(summary = "撤销公告")
    @PutMapping("/revoke/{id}")
    public Result<String> revokeAnnouncement(@PathVariable Long id) {
        announcementService.revokeAnnouncement(id);
        return Result.success("撤销公告成功");
    }
    
    /**
     * 获取用户真实姓名，如果不存在则返回用户名
     */
    private String getUserRealName(Long userId, String username) {
        User user = userService.getById(userId);
        if (user != null && user.getRealName() != null && !user.getRealName().trim().isEmpty()) {
            return user.getRealName();
        }
        return username;
    }
}