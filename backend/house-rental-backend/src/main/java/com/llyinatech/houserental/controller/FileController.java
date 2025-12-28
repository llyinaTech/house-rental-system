package com.llyinatech.houserental.controller;

import com.llyinatech.houserental.annotation.SysLogAnnotation;
import com.llyinatech.houserental.common.Result;
import com.llyinatech.houserental.enums.ActionEnum;
import com.llyinatech.houserental.enums.ModuleEnum;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

/**
 * 文件上传/下载Controller
 */
@RestController
@RequestMapping("/api/file")
public class FileController {

    private final com.llyinatech.houserental.service.OssService ossService;

    public FileController(com.llyinatech.houserental.service.OssService ossService) {
        this.ossService = ossService;
    }

    /**
     * 上传合同PDF文件
     */
    @SysLogAnnotation(module = ModuleEnum.CONTRACT_MANAGEMENT, action = ActionEnum.ADD, detail = "上传合同文件")
    @PostMapping("/upload")
    public Result<String> uploadContract(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return Result.error("文件为空");
        }
        String url = ossService.uploadContract(file);
        return Result.success(url);
    }

    @GetMapping("/signed-url")
    public Result<String> signedUrl(@RequestParam(required = false) String path,
                                    @RequestParam(required = false) String key,
                                    @RequestParam(defaultValue = "300") Integer expires) {
        String input = StringUtils.hasText(key) ? key : path;
        if (!StringUtils.hasText(input)) {
            return Result.error("参数缺失");
        }
        String url = ossService.generateSignedUrl(input, expires != null ? expires : 300);
        return Result.success(url);
    }
}
