package com.arthmatic.shumelahire.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class LeaveEncashmentRequest {

    @NotNull(message = "Leave type ID is required")
    private Long leaveTypeId;

    @NotNull(message = "Days to encash is required")
    private BigDecimal daysToEncash;

    @NotNull(message = "Daily rate is required")
    private BigDecimal dailyRate;

    // Getters and Setters
    public Long getLeaveTypeId() { return leaveTypeId; }
    public void setLeaveTypeId(Long leaveTypeId) { this.leaveTypeId = leaveTypeId; }

    public BigDecimal getDaysToEncash() { return daysToEncash; }
    public void setDaysToEncash(BigDecimal daysToEncash) { this.daysToEncash = daysToEncash; }

    public BigDecimal getDailyRate() { return dailyRate; }
    public void setDailyRate(BigDecimal dailyRate) { this.dailyRate = dailyRate; }
}
