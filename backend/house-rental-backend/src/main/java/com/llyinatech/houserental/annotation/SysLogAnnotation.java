package com.llyinatech.houserental.annotation;

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
    String module() default "";

    /**
     * 操作类型
     */
    String action() default "";

    /**
     * 详细信息（支持SpEL表达式）
     */
    String detail() default "";
}
