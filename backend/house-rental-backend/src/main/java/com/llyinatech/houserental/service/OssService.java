package com.llyinatech.houserental.service;

import org.springframework.web.multipart.MultipartFile;

public interface OssService {
    String uploadFile(MultipartFile file);

    void deleteFile(String fileUrl);

    String uploadContract(MultipartFile file);

    String generateSignedUrl(String fileUrlOrKey, int expireSeconds);
}
