package com.example.recruitment.dto;

import jakarta.validation.constraints.NotBlank;

public class ApplicationWithdrawRequest {
    
    @NotBlank(message = "Withdrawal reason is required")
    private String reason;
    
    // Constructors
    public ApplicationWithdrawRequest() {}
    
    public ApplicationWithdrawRequest(String reason) {
        this.reason = reason;
    }
    
    // Getters and Setters
    public String getReason() {
        return reason;
    }
    
    public void setReason(String reason) {
        this.reason = reason;
    }
}