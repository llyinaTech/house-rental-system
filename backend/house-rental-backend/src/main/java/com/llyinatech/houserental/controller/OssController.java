package com.llyinatech.houserental.controller;

import com.llyinatech.houserental.service.OssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/oss")
public class OssController {

    @Autowired
    private OssService ossService;

    /**
     * 上传文件
     *
     * @param file 上传的文件
     * @return 上传结果
     */
    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(@RequestParam("file") MultipartFile file) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 验证文件
            if (file.isEmpty()) {
                result.put("success", false);
                result.put("message", "文件不能为空");
                return ResponseEntity.badRequest().body(result);
            }

            // 上传文件
            String fileUrl = ossService.uploadFile(file);
            
            result.put("success", true);
            result.put("message", "文件上传成功");
            result.put("url", fileUrl);
            result.put("originalName", file.getOriginalFilename());
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "文件上传失败: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(result);
        }
    }

    /**
     * 上传多文件
     *
     * @param files 上传的文件数组
     * @return 上传结果
     */
    @PostMapping("/uploadMultiple")
    public ResponseEntity<Map<String, Object>> uploadMultipleFiles(@RequestParam("files") MultipartFile[] files) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            if (files.length == 0) {
                result.put("success", false);
                result.put("message", "至少需要上传一个文件");
                return ResponseEntity.badRequest().body(result);
            }

            String[] fileUrls = new String[files.length];
            for (int i = 0; i < files.length; i++) {
                fileUrls[i] = ossService.uploadFile(files[i]);
            }
            
            result.put("success", true);
            result.put("message", "文件上传成功");
            result.put("urls", fileUrls);
            
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "文件上传失败: " + e.getMessage());
            
            return ResponseEntity.internalServerError().body(result);
        }
    }
}