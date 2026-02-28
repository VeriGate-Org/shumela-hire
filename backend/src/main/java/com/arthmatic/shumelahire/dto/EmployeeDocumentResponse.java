package com.arthmatic.shumelahire.dto;

import com.arthmatic.shumelahire.entity.EmployeeDocument;
import com.arthmatic.shumelahire.entity.EmployeeDocumentType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class EmployeeDocumentResponse {

    private Long id;
    private Long employeeId;
    private EmployeeDocumentType documentType;
    private String name;
    private String filename;
    private String fileUrl;
    private Long fileSize;
    private String fileSizeFormatted;
    private String contentType;
    private Integer version;
    private Boolean isCurrent;
    private LocalDate expiryDate;
    private LocalDate issuedDate;
    private String issuingAuthority;
    private String notes;
    private Long uploadedBy;
    private boolean expired;
    private boolean expiringSoon;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public EmployeeDocumentResponse() {}

    public EmployeeDocumentResponse(EmployeeDocument document) {
        this.id = document.getId();
        this.employeeId = document.getEmployeeId();
        this.documentType = document.getDocumentType();
        this.name = document.getName();
        this.filename = document.getFilename();
        this.fileUrl = document.getFileUrl();
        this.fileSize = document.getFileSize();
        this.fileSizeFormatted = document.getFileSizeFormatted();
        this.contentType = document.getContentType();
        this.version = document.getVersion();
        this.isCurrent = document.getIsCurrent();
        this.expiryDate = document.getExpiryDate();
        this.issuedDate = document.getIssuedDate();
        this.issuingAuthority = document.getIssuingAuthority();
        this.notes = document.getNotes();
        this.uploadedBy = document.getUploadedBy();
        this.expired = document.isExpired();
        this.expiringSoon = document.isExpiringSoon(30);
        this.createdAt = document.getCreatedAt();
        this.updatedAt = document.getUpdatedAt();
    }

    public static EmployeeDocumentResponse fromEntity(EmployeeDocument document) {
        return new EmployeeDocumentResponse(document);
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public EmployeeDocumentType getDocumentType() { return documentType; }
    public void setDocumentType(EmployeeDocumentType documentType) { this.documentType = documentType; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getFilename() { return filename; }
    public void setFilename(String filename) { this.filename = filename; }

    public String getFileUrl() { return fileUrl; }
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl; }

    public Long getFileSize() { return fileSize; }
    public void setFileSize(Long fileSize) { this.fileSize = fileSize; }

    public String getFileSizeFormatted() { return fileSizeFormatted; }
    public void setFileSizeFormatted(String fileSizeFormatted) { this.fileSizeFormatted = fileSizeFormatted; }

    public String getContentType() { return contentType; }
    public void setContentType(String contentType) { this.contentType = contentType; }

    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }

    public Boolean getIsCurrent() { return isCurrent; }
    public void setIsCurrent(Boolean isCurrent) { this.isCurrent = isCurrent; }

    public LocalDate getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDate expiryDate) { this.expiryDate = expiryDate; }

    public LocalDate getIssuedDate() { return issuedDate; }
    public void setIssuedDate(LocalDate issuedDate) { this.issuedDate = issuedDate; }

    public String getIssuingAuthority() { return issuingAuthority; }
    public void setIssuingAuthority(String issuingAuthority) { this.issuingAuthority = issuingAuthority; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Long getUploadedBy() { return uploadedBy; }
    public void setUploadedBy(Long uploadedBy) { this.uploadedBy = uploadedBy; }

    public boolean isExpired() { return expired; }
    public void setExpired(boolean expired) { this.expired = expired; }

    public boolean isExpiringSoon() { return expiringSoon; }
    public void setExpiringSoon(boolean expiringSoon) { this.expiringSoon = expiringSoon; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
