package com.llyinatech.houserental.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;

/**
 * 区域商圈实体类
 */
@Data
@TableName("sys_region")
public class Region implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 父ID
     */
    private Long parentId;

    /**
     * 区域名称
     */
    private String name;

    /**
     * 层级: 1-省/市, 2-区, 3-街道/商圈
     */
    private Integer levelType;

    /**
     * 排序
     */
    private Integer sortOrder;
}
