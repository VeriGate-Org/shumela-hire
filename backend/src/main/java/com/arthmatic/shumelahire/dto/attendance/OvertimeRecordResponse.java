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
    private BigDecimal hours;
    private String type;
    private BigDecimal rateMultiplier;
    private String status;
    private Long approvedById;
    private String approvedByName;
    private LocalDateTime approvedAt;
    private String reason;
    private String rejectionReason;
    private LocalDateTime createdAt;

    public OvertimeRecordResponse() {}

    public static OvertimeRecordResponse fromEntity(OvertimeRecord o) {
        OvertimeRecordResponse r = new OvertimeRecordResponse();
        r.setId(o.getId());
        r.setEmployeeId(o.getEmployee().getId());
        r.setEmployeeName(o.getEmployee().getFullName());
        r.setOvertimeDate(o.getOvertimeDate());
        r.setHours(o.getHours());
        r.setType(o.getType().name());
        r.setRateMultiplier(o.getRateMultiplier());
        r.setStatus(o.getStatus().name());
        r.setReason(o.getReason());
        r.setRejectionReason(o.getRejectionReason());
        r.setApprovedAt(o.getApprovedAt());
        r.setCreatedAt(o.getCreatedAt());

        if (o.getAttendanceRecord() != null) {
            r.setAttendanceRecordId(o.getAttendanceRecord().getId());
        }
        if (o.getApprovedBy() != null) {
            r.setApprovedById(o.getApprovedBy().getId());
            r.setApprovedByName(o.getApprovedBy().getFullName());
        }
        return r;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public Long getAttendanceRecordId() { return attendanceRecordId; }
    public void setAttendanceRecordId(Long attendanceRecordId) { this.attendanceRecordId = attendanceRecordId; }

    public LocalDate getOvertimeDate() { return overtimeDate; }
    public void setOvertimeDate(LocalDate overtimeDate) { this.overtimeDate = overtimeDate; }

    public BigDecimal getHours() { return hours; }
    public void setHours(BigDecimal hours) { this.hours = hours; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public BigDecimal getRateMultiplier() { return rateMultiplier; }
    public void setRateMultiplier(BigDecimal rateMultiplier) { this.rateMultiplier = rateMultiplier; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getApprovedById() { return approvedById; }
    public void setApprovedById(Long approvedById) { this.approvedById = approvedById; }

    public String getApprovedByName() { return approvedByName; }
    public void setApprovedByName(String approvedByName) { this.approvedByName = approvedByName; }

    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
