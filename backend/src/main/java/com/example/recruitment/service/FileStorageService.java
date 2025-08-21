package com.example.recruitment.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {
    
    private static final Logger logger = LoggerFactory.getLogger(FileStorageService.class);
    
    @Value("${app.file.upload-dir:uploads}")
    private String uploadDir;
    
    /**
     * Store uploaded file and return the file URL
     */
    public String store(MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Failed to store empty file");
        }
        
        // Create upload directory if it doesn't exist
        Path uploadPath = Paths.get(uploadDir);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }
        
        // Generate unique filename
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".") 
            ? originalFilename.substring(originalFilename.lastIndexOf("."))
            : "";
        String uniqueFilename = UUID.randomUUID().toString() + extension;
        
        // Store file
        Path destinationFile = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);
        
        logger.info("File stored: {} -> {}", originalFilename, uniqueFilename);
        
        // Return relative path as URL
        return uploadDir + "/" + uniqueFilename;
    }
    
    /**
     * Delete file from storage
     */
    public void delete(String fileUrl) throws IOException {
        if (fileUrl == null || fileUrl.isEmpty()) {
            logger.warn("Attempted to delete null or empty file URL");
            return;
        }
        
        Path filePath = Paths.get(fileUrl);
        if (Files.exists(filePath)) {
            Files.delete(filePath);
            logger.info("File deleted: {}", fileUrl);
        } else {
            logger.warn("File not found for deletion: {}", fileUrl);
        }
    }
    
    /**
     * Check if file exists
     */
    public boolean exists(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return false;
        }
        Path filePath = Paths.get(fileUrl);
        return Files.exists(filePath);
    }
    
    /**
     * Get file size
     */
    public long getFileSize(String fileUrl) throws IOException {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return 0;
        }
        Path filePath = Paths.get(fileUrl);
        return Files.size(filePath);
    }
}