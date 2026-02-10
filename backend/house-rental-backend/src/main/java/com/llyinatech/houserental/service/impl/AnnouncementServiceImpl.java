package com.llyinatech.houserental.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.llyinatech.houserental.model.entity.Announcement;
import com.llyinatech.houserental.mapper.AnnouncementMapper;
import com.llyinatech.houserental.service.AnnouncementService;
import com.llyinatech.houserental.service.UserService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 公告Service实现类
 */
@Service
public class AnnouncementServiceImpl extends ServiceImpl<AnnouncementMapper, Announcement> implements AnnouncementService {

    private final UserService userService;

    public AnnouncementServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public IPage<Announcement> findAnnouncements(Page<Announcement> page, String title, Integer status) {
        QueryWrapper<Announcement> wrapper = new QueryWrapper<>();
        if (title != null && !title.isEmpty()) {
            wrapper.like("title", title);
        }
        if (status != null) {
            wrapper.eq("status", status);
        }
        wrapper.orderByDesc("publish_time");
        return this.page(page, wrapper);
    }

    @Override
    public void publishAnnouncement(Announcement announcement, Long userId, String realName) {
        // 设置发布信息
        announcement.setUserId(userId);
        announcement.setUserName(realName);
        announcement.setPublishTime(LocalDateTime.now());
        announcement.setStatus(1); // 已发布状态
        announcement.setUpdateTime(LocalDateTime.now());
        
        if (announcement.getId() == null) {
            // 新增公告
            announcement.setCreateTime(LocalDateTime.now());
            this.save(announcement);
        } else {
            // 更新公告并发布
            this.updateById(announcement);
        }
    }

    @Override
    public void createAnnouncementDraft(Announcement announcement, Long userId, String realName) {
        // 新增为草稿
        announcement.setUserId(userId);
        announcement.setUserName(realName);
        announcement.setStatus(0); // 草稿
        announcement.setPublishTime(null);
        announcement.setCreateTime(LocalDateTime.now());
        announcement.setUpdateTime(LocalDateTime.now());
        this.save(announcement);
    }

    @Override
    public void updateAnnouncement(Announcement announcement, Long userId, String realName) {
        announcement.setUserId(userId);
        announcement.setUserName(realName);
        announcement.setUpdateTime(LocalDateTime.now());
        announcement.setStatus(0);
        announcement.setPublishTime(null);
        this.updateById(announcement);
    }

    @Override
    public void revokeAnnouncement(Long id) {
        Announcement announcement = this.getById(id);
        if (announcement != null) {
            announcement.setStatus(2); // 已撤销状态
            announcement.setUpdateTime(LocalDateTime.now());
            this.updateById(announcement);
        }
    }

    @Override
    public List<Announcement> getPublishedAnnouncements() {
        QueryWrapper<Announcement> wrapper = new QueryWrapper<>();
        wrapper.eq("status", 1); // 只查询已发布的公告
        wrapper.orderByDesc("publish_time");
        return this.list(wrapper);
    }
}
