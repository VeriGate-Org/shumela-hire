package com.example.recruitment.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@Service
@ConditionalOnProperty(name = "storage.provider", havingValue = "s3")
public class S3StorageService implements StorageService {

    private static final Logger logger = LoggerFactory.getLogger(S3StorageService.class);

    @Autowired
    private S3Client s3Client;

    @Autowired
    private S3Presigner s3Presigner;

    @Value("${s3.bucket}")
    private String bucket;

    @Override
    public String store(MultipartFile file, String folder) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Failed to store empty file");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
            ? originalFilename.substring(originalFilename.lastIndexOf("."))
            : "";
        String fileKey = folder + "/" + UUID.randomUUID() + extension;

        PutObjectRequest putRequest = PutObjectRequest.builder()
            .bucket(bucket)
            .key(fileKey)
            .contentType(file.getContentType())
            .serverSideEncryption(ServerSideEncryption.AES256)
            .build();

        s3Client.putObject(putRequest, RequestBody.fromInputStream(file.getInputStream(), file.getSize()));

        logger.info("File stored in S3: {} -> {}", originalFilename, fileKey);
        return fileKey;
    }

    @Override
    public void delete(String fileKey) {
        DeleteObjectRequest deleteRequest = DeleteObjectRequest.builder()
            .bucket(bucket)
            .key(fileKey)
            .build();

        s3Client.deleteObject(deleteRequest);
        logger.info("File deleted from S3: {}", fileKey);
    }

    @Override
    public boolean exists(String fileKey) {
        try {
            s3Client.headObject(HeadObjectRequest.builder()
                .bucket(bucket)
                .key(fileKey)
                .build());
            return true;
        } catch (NoSuchKeyException e) {
            return false;
        }
    }

    @Override
    public long getFileSize(String fileKey) {
        HeadObjectResponse response = s3Client.headObject(HeadObjectRequest.builder()
            .bucket(bucket)
            .key(fileKey)
            .build());
        return response.contentLength();
    }

    @Override
    public String generateSignedUrl(String fileKey, Duration expiry) {
        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
            .signatureDuration(expiry.isZero() ? Duration.ofMinutes(15) : expiry)
            .getObjectRequest(GetObjectRequest.builder()
                .bucket(bucket)
                .key(fileKey)
                .build())
            .build();

        return s3Presigner.presignGetObject(presignRequest).url().toString();
    }

    @Override
    public byte[] download(String fileKey) throws IOException {
        return s3Client.getObjectAsBytes(GetObjectRequest.builder()
            .bucket(bucket)
            .key(fileKey)
            .build()).asByteArray();
    }
}
