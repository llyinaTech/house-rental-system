package com.llyinatech.houserental.entity;

import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import java.io.Serializable;

/**
 * 角色实体类
 */
@Data
@TableName("sys_role")
public class Role implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 角色名称 (管理员/房东/租客)
     */
    private String roleName;

    /**
     * 角色权限字符 (admin/landlord/tenant)
     */
    private String roleKey;

    /**
     * 备注
     */
    private String remark;
}
