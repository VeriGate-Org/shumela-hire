package com.arthmatic.shumelahire.dto.attendance;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class OvertimeRecordRequest {

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    private Long attendanceRecordId;

    @NotNull(message = "Overtime date is required")
    private String overtimeDate;

    @NotNull(message = "Overtime type is required")
    private String overtimeType;

    @NotNull(message = "Hours is required")
    @Positive
    private BigDecimal hours;

    private BigDecimal rateMultiplier;
    private String requestedBy;
    private String notes;

    // Getters and Setters
    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

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

    public String getRequestedBy() { return requestedBy; }
    public void setRequestedBy(String requestedBy) { this.requestedBy = requestedBy; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
