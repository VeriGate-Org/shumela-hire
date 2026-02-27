package com.arthmatic.shumelahire.dto.attendance;

import com.arthmatic.shumelahire.entity.OvertimeRecord;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class OvertimeRecordResponse {

    private Long id;
    private Long employeeId;
    private String employeeName;
    private Long attendanceRecordId;
    private LocalDate overtimeDate;
    private BigDecimal overtimeHours;
    private String overtimeType;
    private BigDecimal rateMultiplier;
    private String status;
    private Boolean isPreApproved;
    private String reason;
    private String approvedByName;
    private LocalDateTime approvedAt;
    private String rejectionReason;
    private BigDecimal weeklyOvertimeTotal;
    private BigDecimal monthlyOvertimeTotal;
    private Boolean exceedsBceaWeeklyLimit;
    private BigDecimal bceaWeeklyLimitHours;
    private LocalDateTime createdAt;

    public static OvertimeRecordResponse fromEntity(OvertimeRecord o) {
        OvertimeRecordResponse r = new OvertimeRecordResponse();
        r.id = o.getId();
        r.employeeId = o.getEmployee().getId();
        r.employeeName = o.getEmployee().getFullName();
        r.attendanceRecordId = o.getAttendanceRecord() != null ? o.getAttendanceRecord().getId() : null;
        r.overtimeDate = o.getOvertimeDate();
        r.overtimeHours = o.getOvertimeHours();
        r.overtimeType = o.getOvertimeType().name();
        r.rateMultiplier = o.getRateMultiplier();
        r.status = o.getStatus().name();
        r.isPreApproved = o.getIsPreApproved();
        r.reason = o.getReason();
        r.approvedByName = o.getApprovedBy() != null ? o.getApprovedBy().getFullName() : null;
        r.approvedAt = o.getApprovedAt();
        r.rejectionReason = o.getRejectionReason();
        r.weeklyOvertimeTotal = o.getWeeklyOvertimeTotal();
        r.monthlyOvertimeTotal = o.getMonthlyOvertimeTotal();
        r.exceedsBceaWeeklyLimit = o.getExceedsBceaWeeklyLimit();
        r.bceaWeeklyLimitHours = o.getBceaWeeklyLimitHours();
        r.createdAt = o.getCreatedAt();
        return r;
    }

    public Long getId() { return id; }
    public Long getEmployeeId() { return employeeId; }
    public String getEmployeeName() { return employeeName; }
    public Long getAttendanceRecordId() { return attendanceRecordId; }
    public LocalDate getOvertimeDate() { return overtimeDate; }
    public BigDecimal getOvertimeHours() { return overtimeHours; }
    public String getOvertimeType() { return overtimeType; }
    public BigDecimal getRateMultiplier() { return rateMultiplier; }
    public String getStatus() { return status; }
    public Boolean getIsPreApproved() { return isPreApproved; }
    public String getReason() { return reason; }
    public String getApprovedByName() { return approvedByName; }
    public LocalDateTime getApprovedAt() { return approvedAt; }
    public String getRejectionReason() { return rejectionReason; }
    public BigDecimal getWeeklyOvertimeTotal() { return weeklyOvertimeTotal; }
    public BigDecimal getMonthlyOvertimeTotal() { return monthlyOvertimeTotal; }
    public Boolean getExceedsBceaWeeklyLimit() { return exceedsBceaWeeklyLimit; }
    public BigDecimal getBceaWeeklyLimitHours() { return bceaWeeklyLimitHours; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
