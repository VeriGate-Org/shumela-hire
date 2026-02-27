package com.arthmatic.shumelahire.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class LeaveRequestCreateRequest {

    @NotNull(message = "Leave type ID is required")
    private Long leaveTypeId;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    @NotNull(message = "Number of days is required")
    private BigDecimal numberOfDays;

    private boolean halfDay;
    private String halfDayPeriod;
    private String reason;
    private Long delegateId;

    // Getters and Setters
    public Long getLeaveTypeId() { return leaveTypeId; }
    public void setLeaveTypeId(Long leaveTypeId) { this.leaveTypeId = leaveTypeId; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public BigDecimal getNumberOfDays() { return numberOfDays; }
    public void setNumberOfDays(BigDecimal numberOfDays) { this.numberOfDays = numberOfDays; }

    public boolean isHalfDay() { return halfDay; }
    public void setHalfDay(boolean halfDay) { this.halfDay = halfDay; }

    public String getHalfDayPeriod() { return halfDayPeriod; }
    public void setHalfDayPeriod(String halfDayPeriod) { this.halfDayPeriod = halfDayPeriod; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public Long getDelegateId() { return delegateId; }
    public void setDelegateId(Long delegateId) { this.delegateId = delegateId; }
}
