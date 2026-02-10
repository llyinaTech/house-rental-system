package com.llyinatech.houserental.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 系统消息实体类
 */
@Data
@TableName("sys_message")
public class SysMessage extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 消息标题
     */
    private String title;

    /**
     * 消息内容
     */
    private String content;

    /**
     * 接收者ID
     */
    private Long receiverId;

    /**
     * 消息类型: SYSTEM, CONTRACT, BILL
     */
    private String type;

    /**
     * 状态: 0-未读, 1-已读
     */
    private Integer status;
}
