package com.llyinatech.houserental.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.util.List;

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
     * 层级: 1-省/直辖市, 2-市, 3-区/县
     */
    private Integer levelType;

    /**
     * 区域层级常量
     */
    public static final int LEVEL_PROVINCE = 1;
    public static final int LEVEL_CITY = 2;
    public static final int LEVEL_DISTRICT = 3;

    /**
     * 排序
     */
    private Integer sortOrder;

    /**
     * 子区域列表
     */
    @TableField(exist = false)
    private List<Region> children;
}
