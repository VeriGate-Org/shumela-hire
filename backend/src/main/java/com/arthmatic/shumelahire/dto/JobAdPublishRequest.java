package com.arthmatic.shumelahire.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class JobAdPublishRequest {
    
    @NotNull(message = "Internal channel flag is required")
    private Boolean channelInternal;
    
    @NotNull(message = "External channel flag is required")
    private Boolean channelExternal;
    
    private LocalDate closingDate;
    
    private String slug;
    
    @NotBlank(message = "Actor user ID is required")
    private String actorUserId;
    
    // Constructors
    public JobAdPublishRequest() {}
    
    public JobAdPublishRequest(Boolean channelInternal, Boolean channelExternal, String actorUserId) {
        this.channelInternal = channelInternal;
        this.channelExternal = channelExternal;
        this.actorUserId = actorUserId;
    }
    
    // Getters and Setters
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
    
    public String getActorUserId() {
        return actorUserId;
    }
    
    public void setActorUserId(String actorUserId) {
        this.actorUserId = actorUserId;
    }
}