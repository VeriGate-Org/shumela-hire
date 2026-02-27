package com.arthmatic.shumelahire.dto;

import jakarta.validation.constraints.NotBlank;

public class LeaveRejectionRequest {

    @NotBlank(message = "Rejection reason is required")
    private String reason;

    // Getters and Setters
    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
