package com.arthmatic.shumelahire.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.Duration;
import java.time.Instant;
import java.util.Base64;
import java.util.UUID;

@Service
@ConditionalOnProperty(name = "storage.provider", havingValue = "local", matchIfMissing = true)
public class LocalStorageService implements StorageService {

    private static final Logger logger = LoggerFactory.getLogger(LocalStorageService.class);

    @Value("${app.file.upload-dir:uploads}")
    private String uploadDir;

    @Value("${encryption.key}")
    private String hmacKey;

    @Override
    public String store(MultipartFile file, String folder) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Failed to store empty file");
        }

        Path uploadPath = Paths.get(uploadDir, folder);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
            ? originalFilename.substring(originalFilename.lastIndexOf("."))
            : "";
        String uniqueFilename = UUID.randomUUID().toString() + extension;
        String fileKey = folder + "/" + uniqueFilename;

        Path destinationFile = uploadPath.resolve(uniqueFilename);
        Files.copy(file.getInputStream(), destinationFile, StandardCopyOption.REPLACE_EXISTING);

        logger.info("File stored: {} -> {}", originalFilename, fileKey);
        return fileKey;
    }

    @Override
    public void delete(String fileKey) throws IOException {
        Path filePath = Paths.get(uploadDir, fileKey);
        if (Files.exists(filePath)) {
            Files.delete(filePath);
            logger.info("File deleted: {}", fileKey);
        }
    }

    @Override
    public boolean exists(String fileKey) {
        return Files.exists(Paths.get(uploadDir, fileKey));
    }

    @Override
    public long getFileSize(String fileKey) throws IOException {
        return Files.size(Paths.get(uploadDir, fileKey));
    }

    @Override
    public String generateSignedUrl(String fileKey, Duration expiry) {
        long expiryEpoch = Instant.now().plus(expiry).getEpochSecond();
        String payload = fileKey + ":" + expiryEpoch;

        try {
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(new SecretKeySpec(hmacKey.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            String signature = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(mac.doFinal(payload.getBytes(StandardCharsets.UTF_8)));

            return "/api/files/" + fileKey + "?expires=" + expiryEpoch + "&sig=" + signature;
        } catch (Exception e) {
            logger.error("Failed to generate signed URL: {}", e.getMessage());
            throw new RuntimeException("Failed to generate signed URL", e);
        }
    }

    @Override
    public byte[] download(String fileKey) throws IOException {
        return Files.readAllBytes(Paths.get(uploadDir, fileKey));
    }
}
