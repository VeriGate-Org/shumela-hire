package com.example.recruitment.dto;

import com.example.recruitment.entity.Document;
import com.example.recruitment.entity.DocumentType;

import java.time.LocalDateTime;

public class DocumentResponse {
    
    private Long id;
    private Long applicantId;
    private Long applicationId;
    private DocumentType type;
    private String filename;
    private String url;
    private Long fileSize;
    private String contentType;
    private LocalDateTime uploadedAt;
    private String fileSizeFormatted;
    
    // Constructors
    public DocumentResponse() {}
    
    public DocumentResponse(Document document) {
        this.id = document.getId();
        this.applicantId = document.getApplicant().getId();
        this.applicationId = document.getApplicationId();
        this.type = document.getType();
        this.filename = document.getFilename();
        this.url = document.getUrl();
        this.fileSize = document.getFileSize();
        this.contentType = document.getContentType();
        this.uploadedAt = document.getUploadedAt();
        this.fileSizeFormatted = document.getFileSizeFormatted();
    }
    
    // Static factory method
    public static DocumentResponse fromEntity(Document document) {
        return new DocumentResponse(document);
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getApplicantId() {
        return applicantId;
    }
    
    public void setApplicantId(Long applicantId) {
        this.applicantId = applicantId;
    }
    
    public Long getApplicationId() {
        return applicationId;
    }
    
    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }
    
    public DocumentType getType() {
        return type;
    }
    
    public void setType(DocumentType type) {
        this.type = type;
    }
    
    public String getFilename() {
        return filename;
    }
    
    public void setFilename(String filename) {
        this.filename = filename;
    }
    
    public String getUrl() {
        return url;
    }
    
    public void setUrl(String url) {
        this.url = url;
    }
    
    public Long getFileSize() {
        return fileSize;
    }
    
    public void setFileSize(Long fileSize) {
        this.fileSize = fileSize;
    }
    
    public String getContentType() {
        return contentType;
    }
    
    public void setContentType(String contentType) {
        this.contentType = contentType;
    }
    
    public LocalDateTime getUploadedAt() {
        return uploadedAt;
    }
    
    public void setUploadedAt(LocalDateTime uploadedAt) {
        this.uploadedAt = uploadedAt;
    }
    
    public String getFileSizeFormatted() {
        return fileSizeFormatted;
    }
    
    public void setFileSizeFormatted(String fileSizeFormatted) {
        this.fileSizeFormatted = fileSizeFormatted;
    }
}