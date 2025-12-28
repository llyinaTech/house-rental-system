package com.llyinatech.houserental.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.llyinatech.houserental.entity.Announcement;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.List;

/**
 * 公告Service接口
 */
public interface AnnouncementService extends IService<Announcement> {
    /**
     * 分页查询公告
     */
    IPage<Announcement> findAnnouncements(Page<Announcement> page, String title, Integer status);

    /**
     * 发布公告
     */
    void publishAnnouncement(Announcement announcement, Long userId, String realName);

    /**
     * 新增公告为草稿
     */
    void createAnnouncementDraft(Announcement announcement, Long userId, String realName);

    /**
     * 更新公告（不改变状态）
     */
    void updateAnnouncement(Announcement announcement, Long userId, String realName);

    /**
     * 撤销公告
     */
    void revokeAnnouncement(Long id);

    /**
     * 获取已发布的公告列表
     */
    List<Announcement> getPublishedAnnouncements();
}
