package com.arthmatic.shumelahire.dto.attendance;

import com.arthmatic.shumelahire.entity.attendance.OvertimeRecord;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class OvertimeRecordResponse {

    private Long id;
    private Long employeeId;
    private String employeeName;
    private Long attendanceRecordId;
    private String overtimeDate;
    private String overtimeType;
    private BigDecimal hours;
    private BigDecimal rateMultiplier;
    private String status;
    private String requestedBy;
    private String approvedBy;
    private LocalDateTime approvedAt;
    private String rejectionReason;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static OvertimeRecordResponse fromEntity(OvertimeRecord record) {
        OvertimeRecordResponse response = new OvertimeRecordResponse();
        response.setId(record.getId());
        response.setEmployeeId(record.getEmployee().getId());
        response.setEmployeeName(record.getEmployee().getFullName());
        if (record.getAttendanceRecord() != null) {
            response.setAttendanceRecordId(record.getAttendanceRecord().getId());
        }
        response.setOvertimeDate(record.getOvertimeDate().toString());
        response.setOvertimeType(record.getOvertimeType().name());
        response.setHours(record.getHours());
        response.setRateMultiplier(record.getRateMultiplier());
        response.setStatus(record.getStatus().name());
        response.setRequestedBy(record.getRequestedBy());
        response.setApprovedBy(record.getApprovedBy());
        response.setApprovedAt(record.getApprovedAt());
        response.setRejectionReason(record.getRejectionReason());
        response.setNotes(record.getNotes());
        response.setCreatedAt(record.getCreatedAt());
        response.setUpdatedAt(record.getUpdatedAt());
        return response;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public Long getAttendanceRecordId() { return attendanceRecordId; }
    public void setAttendanceRecordId(Long attendanceRecordId) { this.attendanceRecordId = attendanceRecordId; }

    public String getOvertimeDate() { return overtimeDate; }
    public void setOvertimeDate(String overtimeDate) { this.overtimeDate = overtimeDate; }

    public String getOvertimeType() { return overtimeType; }
    public void setOvertimeType(String overtimeType) { this.overtimeType = overtimeType; }

    public BigDecimal getHours() { return hours; }
    public void setHours(BigDecimal hours) { this.hours = hours; }

    public BigDecimal getRateMultiplier() { return rateMultiplier; }
    public void setRateMultiplier(BigDecimal rateMultiplier) { this.rateMultiplier = rateMultiplier; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getRequestedBy() { return requestedBy; }
    public void setRequestedBy(String requestedBy) { this.requestedBy = requestedBy; }

    public String getApprovedBy() { return approvedBy; }
    public void setApprovedBy(String approvedBy) { this.approvedBy = approvedBy; }

    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
