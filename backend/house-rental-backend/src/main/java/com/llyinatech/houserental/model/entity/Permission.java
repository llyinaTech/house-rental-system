package com.llyinatech.houserental.model.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

import java.time.LocalDateTime;

@Data
@TableName("sys_permission")
public class Permission extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long parentId;

    private String permName;

    private String permKey;

    private Integer type;

    private String path;

    private String component;

    private String method;

    private Integer sortOrder;

    private Integer visible;

    private Integer status;

    @TableField(exist = false)
    private List<Permission> children;
}

