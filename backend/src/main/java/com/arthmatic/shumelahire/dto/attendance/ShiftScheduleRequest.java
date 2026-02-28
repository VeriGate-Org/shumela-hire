package com.arthmatic.shumelahire.dto.attendance;

import jakarta.validation.constraints.NotNull;

public class ShiftScheduleRequest {

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    @NotNull(message = "Shift ID is required")
    private Long shiftId;

    @NotNull(message = "Schedule date is required")
    private String scheduleDate;

    private Long shiftPatternId;

    // Getters and Setters
    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public Long getShiftId() { return shiftId; }
    public void setShiftId(Long shiftId) { this.shiftId = shiftId; }

    public String getScheduleDate() { return scheduleDate; }
    public void setScheduleDate(String scheduleDate) { this.scheduleDate = scheduleDate; }

    public Long getShiftPatternId() { return shiftPatternId; }
    public void setShiftPatternId(Long shiftPatternId) { this.shiftPatternId = shiftPatternId; }
}
