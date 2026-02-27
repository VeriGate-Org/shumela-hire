package com.arthmatic.shumelahire.dto;

import com.arthmatic.shumelahire.entity.LeaveRequest;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class LeaveRequestResponse {

    private Long id;
    private Long employeeId;
    private String employeeName;
    private Long leaveTypeId;
    private String leaveTypeName;
    private String leaveTypeCode;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal numberOfDays;
    private boolean halfDay;
    private String halfDayPeriod;
    private String reason;
    private String status;
    private LocalDateTime submittedAt;
    private Long approvedById;
    private String approvedByName;
    private LocalDateTime approvedAt;
    private String approvalNotes;
    private Long rejectedById;
    private LocalDateTime rejectedAt;
    private String rejectionReason;
    private LocalDateTime cancelledAt;
    private String cancellationReason;
    private Long delegateId;
    private String delegateName;
    private String attachmentUrl;
    private String attachmentFilename;
    private LocalDateTime createdAt;

    public LeaveRequestResponse(LeaveRequest request) {
        this.id = request.getId();
        this.employeeId = request.getEmployee().getId();
        this.employeeName = request.getEmployee().getFullName();
        this.leaveTypeId = request.getLeaveType().getId();
        this.leaveTypeName = request.getLeaveType().getName();
        this.leaveTypeCode = request.getLeaveType().getCode();
        this.startDate = request.getStartDate();
        this.endDate = request.getEndDate();
        this.numberOfDays = request.getNumberOfDays();
        this.halfDay = request.isHalfDay();
        this.halfDayPeriod = request.getHalfDayPeriod() != null
                ? request.getHalfDayPeriod().name() : null;
        this.reason = request.getReason();
        this.status = request.getStatus().name();
        this.submittedAt = request.getSubmittedAt();
        if (request.getApprovedBy() != null) {
            this.approvedById = request.getApprovedBy().getId();
            this.approvedByName = request.getApprovedBy().getFullName();
        }
        this.approvedAt = request.getApprovedAt();
        this.approvalNotes = request.getApprovalNotes();
        if (request.getRejectedBy() != null) {
            this.rejectedById = request.getRejectedBy().getId();
        }
        this.rejectedAt = request.getRejectedAt();
        this.rejectionReason = request.getRejectionReason();
        this.cancelledAt = request.getCancelledAt();
        this.cancellationReason = request.getCancellationReason();
        if (request.getDelegate() != null) {
            this.delegateId = request.getDelegate().getId();
            this.delegateName = request.getDelegate().getFullName();
        }
        this.attachmentUrl = request.getAttachmentUrl();
        this.attachmentFilename = request.getAttachmentFilename();
        this.createdAt = request.getCreatedAt();
    }

    public static LeaveRequestResponse fromEntity(LeaveRequest request) {
        return new LeaveRequestResponse(request);
    }

    // Getters
    public Long getId() { return id; }
    public Long getEmployeeId() { return employeeId; }
    public String getEmployeeName() { return employeeName; }
    public Long getLeaveTypeId() { return leaveTypeId; }
    public String getLeaveTypeName() { return leaveTypeName; }
    public String getLeaveTypeCode() { return leaveTypeCode; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public BigDecimal getNumberOfDays() { return numberOfDays; }
    public boolean isHalfDay() { return halfDay; }
    public String getHalfDayPeriod() { return halfDayPeriod; }
    public String getReason() { return reason; }
    public String getStatus() { return status; }
    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public Long getApprovedById() { return approvedById; }
    public String getApprovedByName() { return approvedByName; }
    public LocalDateTime getApprovedAt() { return approvedAt; }
    public String getApprovalNotes() { return approvalNotes; }
    public Long getRejectedById() { return rejectedById; }
    public LocalDateTime getRejectedAt() { return rejectedAt; }
    public String getRejectionReason() { return rejectionReason; }
    public LocalDateTime getCancelledAt() { return cancelledAt; }
    public String getCancellationReason() { return cancellationReason; }
    public Long getDelegateId() { return delegateId; }
    public String getDelegateName() { return delegateName; }
    public String getAttachmentUrl() { return attachmentUrl; }
    public String getAttachmentFilename() { return attachmentFilename; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
