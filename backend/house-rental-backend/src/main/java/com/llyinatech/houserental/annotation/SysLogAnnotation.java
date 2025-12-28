package com.llyinatech.houserental.annotation;

import com.llyinatech.houserental.enums.ActionEnum;
import com.llyinatech.houserental.enums.ModuleEnum;

import java.lang.annotation.*;

/**
 * 系统日志注解
 * 用于标记需要记录操作日志的方法
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface SysLogAnnotation {

    /**
     * 操作模块
     */
    ModuleEnum module() default ModuleEnum.SYSTEM_LOGIN;

    /**
     * 操作类型
     */
    ActionEnum action();

    /**
     * 详细信息（支持SpEL表达式）
     */
    String detail() default "";
}