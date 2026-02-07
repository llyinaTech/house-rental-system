package com.llyinatech.houserental.enums;

import lombok.Getter;

/**
 * 操作类型枚举
 */
@Getter
public enum ActionEnum {
    ADD("新增"),
    MODIFY("修改"),
    DELETE("删除"),
    QUERY("查询"),
    LOGIN("登录"),
    LOGOUT("登出"),
    EXPORT("导出"),
    PUBLISH("发布"),
    REVOKE("撤销"),
    PAY("支付");

    private final String value;

    ActionEnum(String value) {
        this.value = value;
    }

}