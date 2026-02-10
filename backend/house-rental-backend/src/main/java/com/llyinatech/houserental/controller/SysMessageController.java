package com.llyinatech.houserental.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.llyinatech.houserental.common.Result;
import com.llyinatech.houserental.model.entity.SysMessage;
import com.llyinatech.houserental.security.UserDetailsImpl;
import com.llyinatech.houserental.service.SysMessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

/**
 * 系统消息Controller
 */
@RestController
@RequestMapping("/api/message")
public class SysMessageController {

    @Autowired
    private SysMessageService sysMessageService;

    /**
     * 分页查询我的消息
     */
    @GetMapping("/my-page")
    public Result<Page<SysMessage>> myPage(@RequestParam(defaultValue = "1") Integer current,
                                           @RequestParam(defaultValue = "10") Integer size,
                                           @RequestParam(required = false) Integer status) {
        // Get current user
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserDetailsImpl)) {
            return Result.error("未登录");
        }
        Long userId = ((UserDetailsImpl) auth.getPrincipal()).getId();

        Page<SysMessage> page = new Page<>(current, size);
        LambdaQueryWrapper<SysMessage> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(SysMessage::getReceiverId, userId);
        if (status != null) {
            wrapper.eq(SysMessage::getStatus, status);
        }
        wrapper.orderByDesc(SysMessage::getCreateTime);
        
        return Result.success(sysMessageService.page(page, wrapper));
    }

    /**
     * 标记消息为已读
     */
    @PutMapping("/{id}/read")
    public Result<String> read(@PathVariable Long id) {
        SysMessage msg = sysMessageService.getById(id);
        if (msg == null) {
            return Result.error("消息不存在");
        }
        
        // Verify ownership
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserDetailsImpl) {
            Long userId = ((UserDetailsImpl) auth.getPrincipal()).getId();
            if (!msg.getReceiverId().equals(userId)) {
                return Result.error("无权操作");
            }
        }
        
        msg.setStatus(1);
        sysMessageService.updateById(msg);
        return Result.success("操作成功");
    }

    /**
     * 一键已读
     */
    @PutMapping("/read-all")
    public Result<String> readAll() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserDetailsImpl)) {
            return Result.error("未登录");
        }
        Long userId = ((UserDetailsImpl) auth.getPrincipal()).getId();

        sysMessageService.update(new LambdaUpdateWrapper<SysMessage>()
                .eq(SysMessage::getReceiverId, userId)
                .eq(SysMessage::getStatus, 0)
                .set(SysMessage::getStatus, 1)); // Set status to 1
        
        return Result.success("全部已读");
    }
    
    /**
     * 获取未读数量
     */
    @GetMapping("/unread-count")
    public Result<Long> getUnreadCount() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !(auth.getPrincipal() instanceof UserDetailsImpl)) {
            return Result.success(0L);
        }
        Long userId = ((UserDetailsImpl) auth.getPrincipal()).getId();
        
        long count = sysMessageService.count(new LambdaQueryWrapper<SysMessage>()
                .eq(SysMessage::getReceiverId, userId)
                .eq(SysMessage::getStatus, 0));
        return Result.success(count);
    }
}
