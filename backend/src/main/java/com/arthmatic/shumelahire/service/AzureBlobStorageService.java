package com.arthmatic.shumelahire.service;

import com.azure.storage.blob.BlobClient;
import com.azure.storage.blob.BlobContainerClient;
import com.azure.storage.blob.BlobServiceClient;
import com.azure.storage.blob.BlobServiceClientBuilder;
import com.azure.storage.blob.models.BlobHttpHeaders;
import com.azure.storage.blob.sas.BlobSasPermission;
import com.azure.storage.blob.sas.BlobServiceSasSignatureValues;
import jakarta.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Duration;
import java.time.OffsetDateTime;
import java.util.UUID;

@Service
@ConditionalOnProperty(name = "storage.provider", havingValue = "azure-blob")
public class AzureBlobStorageService implements StorageService {

    private static final Logger logger = LoggerFactory.getLogger(AzureBlobStorageService.class);

    @Value("${azure.storage.connection-string}")
    private String connectionString;

    @Value("${azure.storage.container-name:shumelahire-documents}")
    private String containerName;

    private BlobServiceClient blobServiceClient;
    private BlobContainerClient containerClient;

    @PostConstruct
    public void init() {
        blobServiceClient = new BlobServiceClientBuilder()
                .connectionString(connectionString)
                .buildClient();

        containerClient = blobServiceClient.getBlobContainerClient(containerName);
        if (!containerClient.exists()) {
            containerClient.create();
            logger.info("Created Azure Blob container: {}", containerName);
        }
    }

    @Override
    public String store(MultipartFile file, String folder) throws IOException {
        if (file.isEmpty()) {
            throw new IOException("Failed to store empty file");
        }

        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename != null && originalFilename.contains(".")
                ? originalFilename.substring(originalFilename.lastIndexOf("."))
                : "";
        String blobKey = folder + "/" + UUID.randomUUID() + extension;

        BlobClient blobClient = containerClient.getBlobClient(blobKey);

        BlobHttpHeaders headers = new BlobHttpHeaders()
                .setContentType(file.getContentType());

        blobClient.upload(file.getInputStream(), file.getSize(), true);
        blobClient.setHttpHeaders(headers);

        logger.info("File stored in Azure Blob: {} -> {}", originalFilename, blobKey);
        return blobKey;
    }

    @Override
    public void delete(String fileKey) {
        BlobClient blobClient = containerClient.getBlobClient(fileKey);
        if (blobClient.exists()) {
            blobClient.delete();
            logger.info("File deleted from Azure Blob: {}", fileKey);
        }
    }

    @Override
    public boolean exists(String fileKey) {
        return containerClient.getBlobClient(fileKey).exists();
    }

    @Override
    public long getFileSize(String fileKey) {
        return containerClient.getBlobClient(fileKey).getProperties().getBlobSize();
    }

    @Override
    public String generateSignedUrl(String fileKey, Duration expiry) {
        BlobClient blobClient = containerClient.getBlobClient(fileKey);

        BlobSasPermission permission = new BlobSasPermission().setReadPermission(true);
        OffsetDateTime expiryTime = OffsetDateTime.now().plus(expiry.isZero() ? Duration.ofMinutes(15) : expiry);

        BlobServiceSasSignatureValues sasValues = new BlobServiceSasSignatureValues(expiryTime, permission);

        String sasToken = blobClient.generateSas(sasValues);
        return blobClient.getBlobUrl() + "?" + sasToken;
    }

    @Override
    public byte[] download(String fileKey) throws IOException {
        BlobClient blobClient = containerClient.getBlobClient(fileKey);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        blobClient.downloadStream(outputStream);
        return outputStream.toByteArray();
    }
}
