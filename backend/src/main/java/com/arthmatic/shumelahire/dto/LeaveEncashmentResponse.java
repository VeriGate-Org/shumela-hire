package com.arthmatic.shumelahire.dto;

import com.arthmatic.shumelahire.entity.LeaveEncashment;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class LeaveEncashmentResponse {

    private Long id;
    private Long employeeId;
    private String employeeName;
    private Long leaveTypeId;
    private String leaveTypeName;
    private BigDecimal daysEncashed;
    private BigDecimal dailyRate;
    private BigDecimal totalAmount;
    private String currency;
    private String status;
    private LocalDateTime requestedAt;
    private Long approvedById;
    private String approvedByName;
    private LocalDateTime approvedAt;
    private String rejectionReason;
    private LocalDateTime paidAt;
    private String payrollReference;

    public LeaveEncashmentResponse(LeaveEncashment encashment) {
        this.id = encashment.getId();
        this.employeeId = encashment.getEmployee().getId();
        this.employeeName = encashment.getEmployee().getFullName();
        this.leaveTypeId = encashment.getLeaveType().getId();
        this.leaveTypeName = encashment.getLeaveType().getName();
        this.daysEncashed = encashment.getDaysEncashed();
        this.dailyRate = encashment.getDailyRate();
        this.totalAmount = encashment.getTotalAmount();
        this.currency = encashment.getCurrency();
        this.status = encashment.getStatus().name();
        this.requestedAt = encashment.getRequestedAt();
        if (encashment.getApprovedBy() != null) {
            this.approvedById = encashment.getApprovedBy().getId();
            this.approvedByName = encashment.getApprovedBy().getFullName();
        }
        this.approvedAt = encashment.getApprovedAt();
        this.rejectionReason = encashment.getRejectionReason();
        this.paidAt = encashment.getPaidAt();
        this.payrollReference = encashment.getPayrollReference();
    }

    public static LeaveEncashmentResponse fromEntity(LeaveEncashment encashment) {
        return new LeaveEncashmentResponse(encashment);
    }

    // Getters
    public Long getId() { return id; }
    public Long getEmployeeId() { return employeeId; }
    public String getEmployeeName() { return employeeName; }
    public Long getLeaveTypeId() { return leaveTypeId; }
    public String getLeaveTypeName() { return leaveTypeName; }
    public BigDecimal getDaysEncashed() { return daysEncashed; }
    public BigDecimal getDailyRate() { return dailyRate; }
    public BigDecimal getTotalAmount() { return totalAmount; }
    public String getCurrency() { return currency; }
    public String getStatus() { return status; }
    public LocalDateTime getRequestedAt() { return requestedAt; }
    public Long getApprovedById() { return approvedById; }
    public String getApprovedByName() { return approvedByName; }
    public LocalDateTime getApprovedAt() { return approvedAt; }
    public String getRejectionReason() { return rejectionReason; }
    public LocalDateTime getPaidAt() { return paidAt; }
    public String getPayrollReference() { return payrollReference; }
}
