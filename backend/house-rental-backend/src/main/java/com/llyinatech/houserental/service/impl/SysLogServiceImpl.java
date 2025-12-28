package com.llyinatech.houserental.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.llyinatech.houserental.dto.SysLogQueryDTO;
import com.llyinatech.houserental.entity.SysLog;
import com.llyinatech.houserental.mapper.SysLogMapper;
import com.llyinatech.houserental.security.UserDetailsImpl;
import com.llyinatech.houserental.service.SysLogService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * 系统操作日志Service实现类
 */
@Service
public class SysLogServiceImpl extends ServiceImpl<SysLogMapper, SysLog> implements SysLogService {

    @Autowired
    private HttpServletRequest request;

    @Override
    public Page<SysLog> pageQuery(SysLogQueryDTO queryDTO) {
        Page<SysLog> page = new Page<>(queryDTO.getCurrent(), queryDTO.getSize());
        LambdaQueryWrapper<SysLog> wrapper = new LambdaQueryWrapper<>();
        
        // 按模块搜索
        if (StringUtils.hasText(queryDTO.getModule())) {
            wrapper.like(SysLog::getModule, queryDTO.getModule());
        }
        
        // 按操作人搜索
        if (StringUtils.hasText(queryDTO.getUsername())) {
            wrapper.like(SysLog::getUsername, queryDTO.getUsername());
        }
        
        // 按操作类型搜索
        if (StringUtils.hasText(queryDTO.getAction())) {
            wrapper.like(SysLog::getAction, queryDTO.getAction());
        }
        
        // 按时间范围搜索
        if (StringUtils.hasText(queryDTO.getStartTime())) {
            LocalDateTime startTime;
            if (queryDTO.getStartTime().length() == 10) {
                // 只有日期，格式：yyyy-MM-dd，补充时间为 00:00:00
                startTime = LocalDateTime.parse(queryDTO.getStartTime() + " 00:00:00", 
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            } else {
                // 完整的日期时间，格式：yyyy-MM-dd HH:mm:ss
                startTime = LocalDateTime.parse(queryDTO.getStartTime(), 
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
            wrapper.ge(SysLog::getCreateTime, startTime);
        }
        if (StringUtils.hasText(queryDTO.getEndTime())) {
            LocalDateTime endTime;
            if (queryDTO.getEndTime().length() == 10) {
                // 只有日期，格式：yyyy-MM-dd，补充时间为 23:59:59
                endTime = LocalDateTime.parse(queryDTO.getEndTime() + " 23:59:59", 
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            } else {
                // 完整的日期时间，格式：yyyy-MM-dd HH:mm:ss
                endTime = LocalDateTime.parse(queryDTO.getEndTime(), 
                    DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            }
            wrapper.le(SysLog::getCreateTime, endTime);
        }
        
        // 按创建时间倒序
        wrapper.orderByDesc(SysLog::getCreateTime);
        
        return this.page(page, wrapper);
    }

    @Override
    public void saveLog(String module, String action, String detail, String status) {
        SysLog sysLog = new SysLog();
        
        // 获取当前登录用户信息
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.getPrincipal() instanceof UserDetailsImpl) {
            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
            sysLog.setUserId(userDetails.getId());
            sysLog.setUsername(userDetails.getUsername());
        }
        
        // 获取请求IP地址
        String ip = getIpAddress();
        sysLog.setIp(ip);
        
        sysLog.setModule(module);
        sysLog.setAction(action);
        sysLog.setDetail(detail);
        sysLog.setStatus(status);
        sysLog.setCreateTime(LocalDateTime.now());
        
        this.save(sysLog);
    }

    /**
     * 获取客户端IP地址
     */
    private String getIpAddress() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes == null) {
                return "127.0.0.1";
            }
            HttpServletRequest request = attributes.getRequest();
            String ip = request.getHeader("X-Forwarded-For");
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("Proxy-Client-IP");
            }
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("WL-Proxy-Client-IP");
            }
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_CLIENT_IP");
            }
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getHeader("HTTP_X_FORWARDED_FOR");
            }
            if (ip == null || ip.isEmpty() || "unknown".equalsIgnoreCase(ip)) {
                ip = request.getRemoteAddr();
            }
            // 对于通过多个代理的情况，第一个IP为客户端真实IP
            if (ip != null && ip.length() > 15 && ip.indexOf(",") > 0) {
                ip = ip.substring(0, ip.indexOf(","));
            }
            // 将IPv6的本地地址转换为IPv4格式
            if ("0:0:0:0:0:0:0:1".equals(ip) || "::1".equals(ip)) {
                ip = "127.0.0.1";
            }
            return ip;
        } catch (Exception e) {
            return "127.0.0.1";
        }
    }

    @Override
    public void exportToExcel(SysLogQueryDTO queryDTO, HttpServletResponse response) {
        try {
            // 构建查询条件
            LambdaQueryWrapper<SysLog> wrapper = new LambdaQueryWrapper<>();
            
            // 按模块搜索
            if (StringUtils.hasText(queryDTO.getModule())) {
                wrapper.like(SysLog::getModule, queryDTO.getModule());
            }
            
            // 按操作人搜索
            if (StringUtils.hasText(queryDTO.getUsername())) {
                wrapper.like(SysLog::getUsername, queryDTO.getUsername());
            }
            
            // 按操作类型搜索
            if (StringUtils.hasText(queryDTO.getAction())) {
                wrapper.like(SysLog::getAction, queryDTO.getAction());
            }
            
            // 按时间范围搜索
            if (StringUtils.hasText(queryDTO.getStartTime())) {
                LocalDateTime startTime;
                if (queryDTO.getStartTime().length() == 10) {
                    startTime = LocalDateTime.parse(queryDTO.getStartTime() + " 00:00:00", 
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                } else {
                    startTime = LocalDateTime.parse(queryDTO.getStartTime(), 
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                }
                wrapper.ge(SysLog::getCreateTime, startTime);
            }
            if (StringUtils.hasText(queryDTO.getEndTime())) {
                LocalDateTime endTime;
                if (queryDTO.getEndTime().length() == 10) {
                    endTime = LocalDateTime.parse(queryDTO.getEndTime() + " 23:59:59", 
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                } else {
                    endTime = LocalDateTime.parse(queryDTO.getEndTime(), 
                        DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                }
                wrapper.le(SysLog::getCreateTime, endTime);
            }
            
            // 按创建时间倒序
            wrapper.orderByDesc(SysLog::getCreateTime);
            
            // 查询所有符合条件的数据
            List<SysLog> logList = this.list(wrapper);
            
            // 创建Excel工作簿
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("系统日志");
            
            // 创建表头样式
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            
            // 创建表头
            Row headerRow = sheet.createRow(0);
            String[] headers = {"日志ID", "操作模块", "操作类型", "操作人员", "操作IP", "状态", "操作时间", "详细信息"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // 创建数据行样式
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            
            // 填充数据
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            int rowNum = 1;
            for (SysLog log : logList) {
                Row row = sheet.createRow(rowNum++);
                
                Cell cell0 = row.createCell(0);
                cell0.setCellValue(log.getId());
                cell0.setCellStyle(dataStyle);
                
                Cell cell1 = row.createCell(1);
                cell1.setCellValue(log.getModule());
                cell1.setCellStyle(dataStyle);
                
                Cell cell2 = row.createCell(2);
                cell2.setCellValue(log.getAction());
                cell2.setCellStyle(dataStyle);
                
                Cell cell3 = row.createCell(3);
                cell3.setCellValue(log.getUsername());
                cell3.setCellStyle(dataStyle);
                
                Cell cell4 = row.createCell(4);
                cell4.setCellValue(log.getIp());
                cell4.setCellStyle(dataStyle);
                
                Cell cell5 = row.createCell(5);
                cell5.setCellValue(log.getStatus());
                cell5.setCellStyle(dataStyle);
                
                Cell cell6 = row.createCell(6);
                cell6.setCellValue(log.getCreateTime() != null ? log.getCreateTime().format(formatter) : "");
                cell6.setCellStyle(dataStyle);
                
                Cell cell7 = row.createCell(7);
                cell7.setCellValue(log.getDetail());
                cell7.setCellStyle(dataStyle);
            }
            
            // 自动调整列宽
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                // 解决中文列宽不够的问题
                sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 17 / 10);
            }
            
            // 设置响应头
            String fileName = "系统日志_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx";
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + encodedFileName);
            
            // 写入响应流
            OutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            workbook.close();
            outputStream.flush();
            outputStream.close();
            
        } catch (Exception e) {
            throw new RuntimeException("导出日志失败: " + e.getMessage(), e);
        }
    }

    @Override
    public void exportToExcelByIds(List<Long> ids, HttpServletResponse response) {
        try {
            List<SysLog> logList = this.listByIds(ids);
            Workbook workbook = new XSSFWorkbook();
            Sheet sheet = workbook.createSheet("系统日志");
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            Row headerRow = sheet.createRow(0);
            String[] headers = {"日志ID", "操作模块", "操作类型", "操作人员", "操作IP", "状态", "操作时间", "详细信息"};
            for (int i = 0; i < headers.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(headers[i]);
                cell.setCellStyle(headerStyle);
            }
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            int rowNum = 1;
            for (SysLog log : logList) {
                Row row = sheet.createRow(rowNum++);
                Cell cell0 = row.createCell(0);
                cell0.setCellValue(log.getId());
                cell0.setCellStyle(dataStyle);
                Cell cell1 = row.createCell(1);
                cell1.setCellValue(log.getModule());
                cell1.setCellStyle(dataStyle);
                Cell cell2 = row.createCell(2);
                cell2.setCellValue(log.getAction());
                cell2.setCellStyle(dataStyle);
                Cell cell3 = row.createCell(3);
                cell3.setCellValue(log.getUsername());
                cell3.setCellStyle(dataStyle);
                Cell cell4 = row.createCell(4);
                cell4.setCellValue(log.getIp());
                cell4.setCellStyle(dataStyle);
                Cell cell5 = row.createCell(5);
                cell5.setCellValue(log.getStatus());
                cell5.setCellStyle(dataStyle);
                Cell cell6 = row.createCell(6);
                cell6.setCellValue(log.getCreateTime() != null ? log.getCreateTime().format(formatter) : "");
                cell6.setCellStyle(dataStyle);
                Cell cell7 = row.createCell(7);
                cell7.setCellValue(log.getDetail());
                cell7.setCellStyle(dataStyle);
            }
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
                sheet.setColumnWidth(i, sheet.getColumnWidth(i) * 17 / 10);
            }
            String fileName = "系统日志_勾选项_" + LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss")) + ".xlsx";
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
            response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + encodedFileName);
            OutputStream outputStream = response.getOutputStream();
            workbook.write(outputStream);
            workbook.close();
            outputStream.flush();
            outputStream.close();
        } catch (Exception e) {
            throw new RuntimeException("导出勾选日志失败: " + e.getMessage(), e);
        }
    }
}
