package com.example.recruitment.dto;

import jakarta.validation.constraints.NotBlank;

import java.time.LocalDate;

public class JobAdUpdateRequest {
    
    @NotBlank(message = "Title is required")
    private String title;
    
    @NotBlank(message = "HTML body is required")
    private String htmlBody;
    
    private Boolean channelInternal;
    
    private Boolean channelExternal;
    
    private LocalDate closingDate;
    
    private String slug;
    
    // Constructors
    public JobAdUpdateRequest() {}
    
    // Getters and Setters
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
}