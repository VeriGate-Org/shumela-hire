package com.arthmatic.shumelahire.dto.attendance;

import com.arthmatic.shumelahire.entity.attendance.ShiftSchedule;

import java.time.LocalDateTime;

public class ShiftScheduleResponse {

    private Long id;
    private Long employeeId;
    private String employeeName;
    private Long shiftId;
    private String shiftName;
    private String scheduleDate;
    private Long shiftPatternId;
    private String shiftPatternName;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ShiftScheduleResponse fromEntity(ShiftSchedule schedule) {
        ShiftScheduleResponse response = new ShiftScheduleResponse();
        response.setId(schedule.getId());
        response.setEmployeeId(schedule.getEmployee().getId());
        response.setEmployeeName(schedule.getEmployee().getFullName());
        response.setShiftId(schedule.getShift().getId());
        response.setShiftName(schedule.getShift().getName());
        response.setScheduleDate(schedule.getScheduleDate().toString());
        if (schedule.getShiftPattern() != null) {
            response.setShiftPatternId(schedule.getShiftPattern().getId());
            response.setShiftPatternName(schedule.getShiftPattern().getName());
        }
        response.setStatus(schedule.getStatus().name());
        response.setCreatedAt(schedule.getCreatedAt());
        response.setUpdatedAt(schedule.getUpdatedAt());
        return response;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public Long getShiftId() { return shiftId; }
    public void setShiftId(Long shiftId) { this.shiftId = shiftId; }

    public String getShiftName() { return shiftName; }
    public void setShiftName(String shiftName) { this.shiftName = shiftName; }

    public String getScheduleDate() { return scheduleDate; }
    public void setScheduleDate(String scheduleDate) { this.scheduleDate = scheduleDate; }

    public Long getShiftPatternId() { return shiftPatternId; }
    public void setShiftPatternId(Long shiftPatternId) { this.shiftPatternId = shiftPatternId; }

    public String getShiftPatternName() { return shiftPatternName; }
    public void setShiftPatternName(String shiftPatternName) { this.shiftPatternName = shiftPatternName; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
