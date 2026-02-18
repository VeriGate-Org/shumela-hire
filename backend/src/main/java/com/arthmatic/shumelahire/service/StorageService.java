package com.arthmatic.shumelahire.service;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;

public interface StorageService {

    String store(MultipartFile file, String folder) throws IOException;

    void delete(String fileKey) throws IOException;

    boolean exists(String fileKey);

    long getFileSize(String fileKey) throws IOException;

    String generateSignedUrl(String fileKey, Duration expiry);

    byte[] download(String fileKey) throws IOException;
}
