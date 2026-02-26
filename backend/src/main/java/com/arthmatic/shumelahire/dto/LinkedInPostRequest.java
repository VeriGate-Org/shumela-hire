package com.arthmatic.shumelahire.dto;

import jakarta.validation.constraints.NotNull;

public class LinkedInPostRequest {

    @NotNull
    private Long jobPostingId;

    private String customText;

    public Long getJobPostingId() {
        return jobPostingId;
    }

    public void setJobPostingId(Long jobPostingId) {
        this.jobPostingId = jobPostingId;
    }

    public String getCustomText() {
        return customText;
    }

    public void setCustomText(String customText) {
        this.customText = customText;
    }
}
