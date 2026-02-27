package com.arthmatic.shumelahire.dto.attendance;

import com.arthmatic.shumelahire.entity.attendance.AttendanceRecord;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class AttendanceRecordResponse {

    private Long id;
    private Long employeeId;
    private String employeeName;
    private Long shiftScheduleId;
    private String recordDate;
    private LocalDateTime clockInTime;
    private LocalDateTime clockOutTime;
    private Double clockInLatitude;
    private Double clockInLongitude;
    private Double clockOutLatitude;
    private Double clockOutLongitude;
    private Long clockInGeofenceId;
    private String clockInGeofenceName;
    private Long clockOutGeofenceId;
    private String clockOutGeofenceName;
    private Boolean clockInWithinGeofence;
    private Boolean clockOutWithinGeofence;
    private String status;
    private BigDecimal totalHours;
    private BigDecimal regularHours;
    private BigDecimal overtimeHours;
    private Integer breakMinutes;
    private Boolean isLateArrival;
    private Boolean isEarlyDeparture;
    private Integer lateMinutes;
    private Integer earlyDepartureMinutes;
    private String notes;
    private Boolean autoClockedOut;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static AttendanceRecordResponse fromEntity(AttendanceRecord record) {
        AttendanceRecordResponse response = new AttendanceRecordResponse();
        response.setId(record.getId());
        response.setEmployeeId(record.getEmployee().getId());
        response.setEmployeeName(record.getEmployee().getFullName());
        if (record.getShiftSchedule() != null) {
            response.setShiftScheduleId(record.getShiftSchedule().getId());
        }
        response.setRecordDate(record.getRecordDate().toString());
        response.setClockInTime(record.getClockInTime());
        response.setClockOutTime(record.getClockOutTime());
        response.setClockInLatitude(record.getClockInLatitude());
        response.setClockInLongitude(record.getClockInLongitude());
        response.setClockOutLatitude(record.getClockOutLatitude());
        response.setClockOutLongitude(record.getClockOutLongitude());
        if (record.getClockInGeofence() != null) {
            response.setClockInGeofenceId(record.getClockInGeofence().getId());
            response.setClockInGeofenceName(record.getClockInGeofence().getName());
        }
        if (record.getClockOutGeofence() != null) {
            response.setClockOutGeofenceId(record.getClockOutGeofence().getId());
            response.setClockOutGeofenceName(record.getClockOutGeofence().getName());
        }
        response.setClockInWithinGeofence(record.getClockInWithinGeofence());
        response.setClockOutWithinGeofence(record.getClockOutWithinGeofence());
        response.setStatus(record.getStatus().name());
        response.setTotalHours(record.getTotalHours());
        response.setRegularHours(record.getRegularHours());
        response.setOvertimeHours(record.getOvertimeHours());
        response.setBreakMinutes(record.getBreakMinutes());
        response.setIsLateArrival(record.getIsLateArrival());
        response.setIsEarlyDeparture(record.getIsEarlyDeparture());
        response.setLateMinutes(record.getLateMinutes());
        response.setEarlyDepartureMinutes(record.getEarlyDepartureMinutes());
        response.setNotes(record.getNotes());
        response.setAutoClockedOut(record.getAutoClockedOut());
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

    public Long getShiftScheduleId() { return shiftScheduleId; }
    public void setShiftScheduleId(Long shiftScheduleId) { this.shiftScheduleId = shiftScheduleId; }

    public String getRecordDate() { return recordDate; }
    public void setRecordDate(String recordDate) { this.recordDate = recordDate; }

    public LocalDateTime getClockInTime() { return clockInTime; }
    public void setClockInTime(LocalDateTime clockInTime) { this.clockInTime = clockInTime; }

    public LocalDateTime getClockOutTime() { return clockOutTime; }
    public void setClockOutTime(LocalDateTime clockOutTime) { this.clockOutTime = clockOutTime; }

    public Double getClockInLatitude() { return clockInLatitude; }
    public void setClockInLatitude(Double clockInLatitude) { this.clockInLatitude = clockInLatitude; }

    public Double getClockInLongitude() { return clockInLongitude; }
    public void setClockInLongitude(Double clockInLongitude) { this.clockInLongitude = clockInLongitude; }

    public Double getClockOutLatitude() { return clockOutLatitude; }
    public void setClockOutLatitude(Double clockOutLatitude) { this.clockOutLatitude = clockOutLatitude; }

    public Double getClockOutLongitude() { return clockOutLongitude; }
    public void setClockOutLongitude(Double clockOutLongitude) { this.clockOutLongitude = clockOutLongitude; }

    public Long getClockInGeofenceId() { return clockInGeofenceId; }
    public void setClockInGeofenceId(Long clockInGeofenceId) { this.clockInGeofenceId = clockInGeofenceId; }

    public String getClockInGeofenceName() { return clockInGeofenceName; }
    public void setClockInGeofenceName(String clockInGeofenceName) { this.clockInGeofenceName = clockInGeofenceName; }

    public Long getClockOutGeofenceId() { return clockOutGeofenceId; }
    public void setClockOutGeofenceId(Long clockOutGeofenceId) { this.clockOutGeofenceId = clockOutGeofenceId; }

    public String getClockOutGeofenceName() { return clockOutGeofenceName; }
    public void setClockOutGeofenceName(String clockOutGeofenceName) { this.clockOutGeofenceName = clockOutGeofenceName; }

    public Boolean getClockInWithinGeofence() { return clockInWithinGeofence; }
    public void setClockInWithinGeofence(Boolean clockInWithinGeofence) { this.clockInWithinGeofence = clockInWithinGeofence; }

    public Boolean getClockOutWithinGeofence() { return clockOutWithinGeofence; }
    public void setClockOutWithinGeofence(Boolean clockOutWithinGeofence) { this.clockOutWithinGeofence = clockOutWithinGeofence; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getTotalHours() { return totalHours; }
    public void setTotalHours(BigDecimal totalHours) { this.totalHours = totalHours; }

    public BigDecimal getRegularHours() { return regularHours; }
    public void setRegularHours(BigDecimal regularHours) { this.regularHours = regularHours; }

    public BigDecimal getOvertimeHours() { return overtimeHours; }
    public void setOvertimeHours(BigDecimal overtimeHours) { this.overtimeHours = overtimeHours; }

    public Integer getBreakMinutes() { return breakMinutes; }
    public void setBreakMinutes(Integer breakMinutes) { this.breakMinutes = breakMinutes; }

    public Boolean getIsLateArrival() { return isLateArrival; }
    public void setIsLateArrival(Boolean isLateArrival) { this.isLateArrival = isLateArrival; }

    public Boolean getIsEarlyDeparture() { return isEarlyDeparture; }
    public void setIsEarlyDeparture(Boolean isEarlyDeparture) { this.isEarlyDeparture = isEarlyDeparture; }

    public Integer getLateMinutes() { return lateMinutes; }
    public void setLateMinutes(Integer lateMinutes) { this.lateMinutes = lateMinutes; }

    public Integer getEarlyDepartureMinutes() { return earlyDepartureMinutes; }
    public void setEarlyDepartureMinutes(Integer earlyDepartureMinutes) { this.earlyDepartureMinutes = earlyDepartureMinutes; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Boolean getAutoClockedOut() { return autoClockedOut; }
    public void setAutoClockedOut(Boolean autoClockedOut) { this.autoClockedOut = autoClockedOut; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
