package com.arthmatic.shumelahire.dto;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class LeaveBalanceAdjustmentRequest {

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    @NotNull(message = "Leave type ID is required")
    private Long leaveTypeId;

    @NotNull(message = "Leave year is required")
    private Integer leaveYear;

    @NotNull(message = "Adjustment amount is required")
    private BigDecimal adjustment;

    private String reason;

    // Getters and Setters
    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public Long getLeaveTypeId() { return leaveTypeId; }
    public void setLeaveTypeId(Long leaveTypeId) { this.leaveTypeId = leaveTypeId; }

    public Integer getLeaveYear() { return leaveYear; }
    public void setLeaveYear(Integer leaveYear) { this.leaveYear = leaveYear; }

    public BigDecimal getAdjustment() { return adjustment; }
    public void setAdjustment(BigDecimal adjustment) { this.adjustment = adjustment; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
