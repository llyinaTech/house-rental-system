package com.llyinatech.houserental.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.llyinatech.houserental.entity.SysLog;
import com.llyinatech.houserental.mapper.SysLogMapper;
import com.llyinatech.houserental.service.SysLogService;
import org.springframework.stereotype.Service;

/**
 * 系统操作日志Service实现类
 */
@Service
public class SysLogServiceImpl extends ServiceImpl<SysLogMapper, SysLog> implements SysLogService {
}
