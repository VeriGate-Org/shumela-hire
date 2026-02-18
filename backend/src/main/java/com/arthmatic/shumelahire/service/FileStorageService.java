package com.arthmatic.shumelahire.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;

@Service
public class FileStorageService {

    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);

    @Autowired
    private StorageService storageService;

    public String store(MultipartFile file) throws IOException {
        return storageService.store(file, "uploads");
    }

    public void delete(String fileUrl) throws IOException {
        storageService.delete(fileUrl);
    }

    public boolean exists(String fileUrl) {
        return storageService.exists(fileUrl);
    }

    public long getFileSize(String fileUrl) throws IOException {
        return storageService.getFileSize(fileUrl);
    }

    public String generateSignedUrl(String fileKey, Duration expiry) {
        return storageService.generateSignedUrl(fileKey, expiry);
    }

    public byte[] download(String fileKey) throws IOException {
        return storageService.download(fileKey);
    }
}
