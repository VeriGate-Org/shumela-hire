package com.arthmatic.shumelahire.dto.attendance;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class OvertimeRecordRequest {

    @NotNull
    private Long employeeId;

    @NotNull
    private LocalDate overtimeDate;

    @NotNull
    private BigDecimal overtimeHours;

    @NotNull
    private String overtimeType;

    private Long attendanceRecordId;
    private Boolean isPreApproved;
    private String reason;

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public LocalDate getOvertimeDate() { return overtimeDate; }
    public void setOvertimeDate(LocalDate overtimeDate) { this.overtimeDate = overtimeDate; }

    public BigDecimal getOvertimeHours() { return overtimeHours; }
    public void setOvertimeHours(BigDecimal overtimeHours) { this.overtimeHours = overtimeHours; }

    public String getOvertimeType() { return overtimeType; }
    public void setOvertimeType(String overtimeType) { this.overtimeType = overtimeType; }

    public Long getAttendanceRecordId() { return attendanceRecordId; }
    public void setAttendanceRecordId(Long attendanceRecordId) { this.attendanceRecordId = attendanceRecordId; }

    public Boolean getIsPreApproved() { return isPreApproved; }
    public void setIsPreApproved(Boolean isPreApproved) { this.isPreApproved = isPreApproved; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
