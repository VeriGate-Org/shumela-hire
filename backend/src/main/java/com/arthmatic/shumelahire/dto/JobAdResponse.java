package com.arthmatic.shumelahire.dto;

import com.arthmatic.shumelahire.entity.JobAd;
import com.arthmatic.shumelahire.entity.JobAdStatus;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class JobAdResponse {
    
    private Long id;
    private Long requisitionId;
    private String title;
    private String htmlBody;
    private Boolean channelInternal;
    private Boolean channelExternal;
    private JobAdStatus status;
    private LocalDate closingDate;
    private String slug;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // Constructors
    public JobAdResponse() {}
    
    public JobAdResponse(JobAd jobAd) {
        this.id = jobAd.getId();
        this.requisitionId = jobAd.getRequisitionId();
        this.title = jobAd.getTitle();
        this.htmlBody = jobAd.getHtmlBody();
        this.channelInternal = jobAd.getChannelInternal();
        this.channelExternal = jobAd.getChannelExternal();
        this.status = jobAd.getStatus();
        this.closingDate = jobAd.getClosingDate();
        this.slug = jobAd.getSlug();
        this.createdBy = jobAd.getCreatedBy();
        this.createdAt = jobAd.getCreatedAt();
        this.updatedAt = jobAd.getUpdatedAt();
    }
    
    // Static factory method
    public static JobAdResponse fromEntity(JobAd jobAd) {
        return new JobAdResponse(jobAd);
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getRequisitionId() {
        return requisitionId;
    }
    
    public void setRequisitionId(Long requisitionId) {
        this.requisitionId = requisitionId;
    }
    
    public String getTitle() {
        return title;
    }
    
    public void setTitle(String title) {
        this.title = title;
    }
    
    public String getHtmlBody() {
        return htmlBody;
    }
    
    public void setHtmlBody(String htmlBody) {
        this.htmlBody = htmlBody;
    }
    
    public Boolean getChannelInternal() {
        return channelInternal;
    }
    
    public void setChannelInternal(Boolean channelInternal) {
        this.channelInternal = channelInternal;
    }
    
    public Boolean getChannelExternal() {
        return channelExternal;
    }
    
    public void setChannelExternal(Boolean channelExternal) {
        this.channelExternal = channelExternal;
    }
    
    public JobAdStatus getStatus() {
        return status;
    }
    
    public void setStatus(JobAdStatus status) {
        this.status = status;
    }
    
    public LocalDate getClosingDate() {
        return closingDate;
    }
    
    public void setClosingDate(LocalDate closingDate) {
        this.closingDate = closingDate;
    }
    
    public String getSlug() {
        return slug;
    }
    
    public void setSlug(String slug) {
        this.slug = slug;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}