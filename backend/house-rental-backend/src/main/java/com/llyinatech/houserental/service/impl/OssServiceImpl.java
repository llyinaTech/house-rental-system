package com.llyinatech.houserental.service.impl;

import com.aliyun.oss.OSS;
import com.aliyun.oss.HttpMethod;
import com.aliyun.oss.model.GeneratePresignedUrlRequest;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.llyinatech.houserental.service.OssService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
public class OssServiceImpl implements OssService {

    @Autowired
    private OSS ossClient;

    @Value("${aliyun.oss.bucketName}")
    private String bucketName;

    @Value("${aliyun.oss.endpoint}")
    private String endpoint;

    @Override
    public String uploadFile(MultipartFile file) {
        try {
            // 获取文件的原始名称
            String originalFilename = file.getOriginalFilename();

            // 获取文件扩展名
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }

            // 生成唯一文件名
            String fileName = UUID.randomUUID().toString().replace("-", "") + fileExtension;

            // 获取文件输入流
            InputStream inputStream = file.getInputStream();

            // 创建PutObjectRequest对象
            PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, fileName, inputStream);

            // 执行上传
            PutObjectResult result = ossClient.putObject(putObjectRequest);

            // 返回文件访问URL
            String fileUrl = "https://" + bucketName + "." + getEndpointHost() + "/" + fileName;

            return fileUrl;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("文件上传失败", e);
        }
    }

    @Override
    public void deleteFile(String fileUrl) {
        try {
            // 从URL中提取文件名
            String fileName = extractFileNameFromUrl(fileUrl);
            // 删除OSS上的文件
            ossClient.deleteObject(bucketName, fileName);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("文件删除失败", e);
        }
    }

    @Override
    public String uploadContract(MultipartFile file) {
        // 验证文件类型，只允许PDF文件
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null && !originalFilename.toLowerCase().endsWith(".pdf")) {
            throw new RuntimeException("仅支持上传PDF文件");
        }

        // 使用通用的uploadFile方法上传
        return this.uploadFile(file);
    }

    @Override
    public String generateSignedUrl(String fileUrlOrKey, int expireSeconds) {
        try {
            if (fileUrlOrKey == null || fileUrlOrKey.isBlank()) {
                throw new IllegalArgumentException("fileUrlOrKey不能为空");
            }
            if (fileUrlOrKey.startsWith("http")) {
                boolean alreadySigned = fileUrlOrKey.contains("Signature=") && fileUrlOrKey.contains("Expires=");
                if (alreadySigned) {
                    return fileUrlOrKey.replace("http://", "https://");
                }
            }
            String objectKey = fileUrlOrKey.startsWith("http") ? extractFileNameFromUrl(fileUrlOrKey) : fileUrlOrKey;
            int seconds = Math.max(expireSeconds, 60);
            Date expiration = new Date(System.currentTimeMillis() + seconds * 1000L);
            GeneratePresignedUrlRequest request = new GeneratePresignedUrlRequest(bucketName, objectKey);
            request.setMethod(HttpMethod.GET);
            request.setExpiration(expiration);
            URL signed = ossClient.generatePresignedUrl(request);
            if (signed == null) {
                throw new RuntimeException("生成签名URL失败");
            }
            // 强制使用 HTTPS
            return signed.toString().replace("http://", "https://");
        } catch (Exception e) {
            throw new RuntimeException("生成签名URL失败: " + e.getMessage(), e);
        }
    }

    /**
     * 从URL中提取文件名
     * @param fileUrl 文件URL
     * @return 文件名
     */
    private String extractFileNameFromUrl(String fileUrl) {
        if (fileUrl != null) {
            int lastSlashIndex = fileUrl.lastIndexOf("/");
            if (lastSlashIndex != -1) {
                return fileUrl.substring(lastSlashIndex + 1);
            }
        }
        return fileUrl;
    }

    /**
     * 从endpoint中提取主机名
     * @return 主机名
     */
    private String getEndpointHost() {
        if (endpoint != null) {
            // 移除协议前缀 (http:// 或 https://)
            String host = endpoint.replace("http://", "").replace("https://", "");
            // 移除末尾的斜杠
            if (host.endsWith("/")) {
                host = host.substring(0, host.length() - 1);
            }
            return host;
        }
        return "oss-cn-hangzhou.aliyuncs.com"; // 默认值
    }
}
