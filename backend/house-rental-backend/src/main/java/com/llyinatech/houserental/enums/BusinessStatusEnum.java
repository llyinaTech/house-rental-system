package com.llyinatech.houserental.enums;

/**
 * 业务状态枚举
 */
public enum BusinessStatusEnum {
    // 通用状态
    SUCCESS(200, "操作成功"),
    ERROR(500, "操作失败"),
    
    // 用户状态
    USER_NORMAL(1, "正常"),
    USER_DISABLED(0, "禁用"),
    
    // 房源租赁状态
    HOUSE_UNRENTED(0, "未租"),
    HOUSE_RENTED(1, "已租"),
    HOUSE_OFF_SHELF(2, "下架"),
    
    // 房源审核状态
    HOUSE_PENDING_REVIEW(0, "待审核"),
    HOUSE_APPROVED(1, "通过"),
    HOUSE_REJECTED(2, "拒绝"),
    
    // 合同状态
    CONTRACT_PENDING_SIGN(0, "待签署"),
    CONTRACT_IN_EFFECT(1, "生效中"),
    CONTRACT_EXPIRED(2, "已到期"),
    CONTRACT_CANCELLED(3, "已解约"),
    CONTRACT_RENTED_OUT(4, "已退租"),
    
    // 租金账单支付状态
    BILL_UNPAID(0, "未支付"),
    BILL_PAID(1, "已支付"),
    BILL_OVERDUE(2, "逾期"),
    
    // 报修工单状态
    REPAIR_PENDING(0, "待处理"),
    REPAIR_IN_PROGRESS(1, "处理中"),
    REPAIR_COMPLETED(2, "已完结"),
    REPAIR_CANCELLED(3, "已取消"),
    
    // 系统日志操作状态
    LOG_SUCCESS("成功"),
    LOG_FAILED("失败");

    private final Integer code;
    private final String message;

    BusinessStatusEnum(Integer code, String message) {
        this.code = code;
        this.message = message;
    }

    // 用于只有消息没有代码的枚举值
    BusinessStatusEnum(String message) {
        this.code = null;
        this.message = message;
    }

    public Integer getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}