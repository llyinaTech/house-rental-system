package com.llyinatech.houserental.enums;

/**
 * 系统模块枚举
 */
public enum ModuleEnum {
    USER_MANAGEMENT("用户管理"),
    ROLE_MANAGEMENT("角色管理"),
    HOUSE_MANAGEMENT("房源管理"),
    CONTRACT_MANAGEMENT("合同管理"),
    FINANCE_MANAGEMENT("财务管理"),
    SYSTEM_LOGIN("系统登录"),
    SYSTEM_LOG("系统日志"),
    REPAIR_ORDER("报修管理"),
    ANNOUNCEMENT_MANAGEMENT("公告管理"),
    PERMISSION_MANAGEMENT("权限管理");

    private final String value;

    ModuleEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    /**
     * 根据值获取枚举
     */
    public static ModuleEnum fromValue(String value) {
        for (ModuleEnum module : ModuleEnum.values()) {
            if (module.value.equals(value)) {
                return module;
            }
        }
        return null;
    }
}
