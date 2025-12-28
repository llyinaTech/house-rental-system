package com.llyinatech.houserental.aspect;

import com.llyinatech.houserental.annotation.SysLogAnnotation;
import com.llyinatech.houserental.common.Result;
import com.llyinatech.houserental.service.SysLogService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;

/**
 * 系统日志切面
 * 用于自动记录标记了 @SysLogAnnotation 注解的方法的操作日志
 */
@Aspect
@Component
public class SysLogAspect {

    private static final Logger log = LoggerFactory.getLogger(SysLogAspect.class);

    @Autowired
    private SysLogService sysLogService;

    /**
     * 定义切点：所有带 @SysLogAnnotation 注解的方法
     */
    @Pointcut("@annotation(com.llyinatech.houserental.annotation.SysLogAnnotation)")
    public void logPointCut() {
    }

    /**
     * 环绕通知：记录操作日志
     */
    @Around("logPointCut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        long beginTime = System.currentTimeMillis();
        Object result = null;
        String status = "成功";
        String errorMsg = null;

        try {
            // 执行方法
            result = point.proceed();

            // 检查返回值，如果业务状态码不为200，标记为失败
            if (result instanceof Result) {
                Result<?> r = (Result<?>) result;
                if (r.getCode() != 200) {
                    status = "失败";
                    errorMsg = r.getMessage();
                }
            }

            return result;
        } catch (Exception e) {
            status = "失败";
            errorMsg = e.getMessage();
            throw e;
        } finally {
            try {
                // 保存日志
                saveSysLog(point, status, errorMsg, System.currentTimeMillis() - beginTime);
            } catch (Exception e) {
                log.error("保存操作日志异常", e);
            }
        }
    }

    /**
     * 保存系统日志
     */
    private void saveSysLog(ProceedingJoinPoint joinPoint, String status, String errorMsg, long time) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();

        SysLogAnnotation sysLog = method.getAnnotation(SysLogAnnotation.class);
        if (sysLog != null) {
            String module = sysLog.module().getValue();
            String action = sysLog.action().getValue();
            String detail = sysLog.detail();

            // 如果失败，添加错误信息
            if ("失败".equals(status) && errorMsg != null) {
                detail = detail + " - 错误: " + errorMsg;
            }

            // 添加执行时间
            detail = detail + " (耗时: " + time + "ms)";

            // 异步保存日志
            sysLogService.saveLog(module, action, detail, status);
        }
    }
}
