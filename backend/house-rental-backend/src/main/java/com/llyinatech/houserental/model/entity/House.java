package com.llyinatech.houserental.model.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 房源信息实体类
 */
@Data
@TableName("house_info")
public class House extends BaseEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 房东ID
     */
    private Long landlordId;

    /**
     * 房源标题
     */
    private String title;

    /**
     * 所属区域ID
     */
    private Long regionId;

    /**
     * 详细地址
     */
    private String address;

    /**
     * 月租金
     */
    private BigDecimal price;

    /**
     * 面积
     */
    private BigDecimal area;

    /**
     * 室
     */
    private Integer roomNum;

    /**
     * 厅
     */
    private Integer hallNum;

    /**
     * 所在楼层
     */
    private Integer floorNo;

    /**
     * 总楼层
     */
    private Integer totalFloor;

    /**
     * 朝向
     */
    private String direction;

    /**
     * 配套设施JSON: ["WiFi","空调"]
     */
    private String featureTags;

    /**
     * 图片列表JSON
     */
    private String images;

    /**
     * 详细描述
     */
    private String description;

    /**
     * 租赁状态: 0-未租, 1-已租, 2-下架
     */
    private Integer rentStatus;

    /**
     * 审核状态: 0-待审核, 1-通过, 2-拒绝
     */
    private Integer auditStatus;

    /**
     * 乐观锁版本号(并发控制)
     */
    @Version
    private Integer version;

    /**
     * 浏览量
     */
    private Integer viewCount;
}
