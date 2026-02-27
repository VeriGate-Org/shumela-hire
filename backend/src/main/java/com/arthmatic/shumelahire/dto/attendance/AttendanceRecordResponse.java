package com.arthmatic.shumelahire.dto.attendance;

import com.arthmatic.shumelahire.entity.AttendanceRecord;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class AttendanceRecordResponse {

    private Long id;
    private Long employeeId;
    private String employeeName;
    private LocalDate recordDate;
    private LocalDateTime clockIn;
    private LocalDateTime clockOut;
    private Double clockInLatitude;
    private Double clockInLongitude;
    private Double clockOutLatitude;
    private Double clockOutLongitude;
    private Long geofenceId;
    private String geofenceName;
    private Boolean clockInWithinGeofence;
    private Boolean clockOutWithinGeofence;
    private String status;
    private BigDecimal regularHours;
    private BigDecimal overtimeHours;
    private Integer breakMinutes;
    private Integer lateMinutes;
    private Integer earlyDepartureMins;
    private Boolean autoClockedOut;
    private String notes;
    private Long shiftScheduleId;
    private LocalDateTime createdAt;

    public AttendanceRecordResponse() {}

    public static AttendanceRecordResponse fromEntity(AttendanceRecord record) {
        AttendanceRecordResponse r = new AttendanceRecordResponse();
        r.setId(record.getId());
        r.setEmployeeId(record.getEmployee().getId());
        r.setEmployeeName(record.getEmployee().getFullName());
        r.setRecordDate(record.getRecordDate());
        r.setClockIn(record.getClockIn());
        r.setClockOut(record.getClockOut());
        r.setClockInLatitude(record.getClockInLatitude());
        r.setClockInLongitude(record.getClockInLongitude());
        r.setClockOutLatitude(record.getClockOutLatitude());
        r.setClockOutLongitude(record.getClockOutLongitude());
        r.setClockInWithinGeofence(record.getClockInWithinGeofence());
        r.setClockOutWithinGeofence(record.getClockOutWithinGeofence());
        r.setStatus(record.getStatus().name());
        r.setRegularHours(record.getRegularHours());
        r.setOvertimeHours(record.getOvertimeHours());
        r.setBreakMinutes(record.getBreakMinutes());
        r.setLateMinutes(record.getLateMinutes());
        r.setEarlyDepartureMins(record.getEarlyDepartureMins());
        r.setAutoClockedOut(record.getAutoClockedOut());
        r.setNotes(record.getNotes());
        r.setCreatedAt(record.getCreatedAt());

        if (record.getGeofence() != null) {
            r.setGeofenceId(record.getGeofence().getId());
            r.setGeofenceName(record.getGeofence().getName());
        }
        if (record.getShiftSchedule() != null) {
            r.setShiftScheduleId(record.getShiftSchedule().getId());
        }
        return r;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public LocalDate getRecordDate() { return recordDate; }
    public void setRecordDate(LocalDate recordDate) { this.recordDate = recordDate; }

    public LocalDateTime getClockIn() { return clockIn; }
    public void setClockIn(LocalDateTime clockIn) { this.clockIn = clockIn; }

    public LocalDateTime getClockOut() { return clockOut; }
    public void setClockOut(LocalDateTime clockOut) { this.clockOut = clockOut; }

    public Double getClockInLatitude() { return clockInLatitude; }
    public void setClockInLatitude(Double clockInLatitude) { this.clockInLatitude = clockInLatitude; }

    public Double getClockInLongitude() { return clockInLongitude; }
    public void setClockInLongitude(Double clockInLongitude) { this.clockInLongitude = clockInLongitude; }

    public Double getClockOutLatitude() { return clockOutLatitude; }
    public void setClockOutLatitude(Double clockOutLatitude) { this.clockOutLatitude = clockOutLatitude; }

    public Double getClockOutLongitude() { return clockOutLongitude; }
    public void setClockOutLongitude(Double clockOutLongitude) { this.clockOutLongitude = clockOutLongitude; }

    public Long getGeofenceId() { return geofenceId; }
    public void setGeofenceId(Long geofenceId) { this.geofenceId = geofenceId; }

    public String getGeofenceName() { return geofenceName; }
    public void setGeofenceName(String geofenceName) { this.geofenceName = geofenceName; }

    public Boolean getClockInWithinGeofence() { return clockInWithinGeofence; }
    public void setClockInWithinGeofence(Boolean clockInWithinGeofence) { this.clockInWithinGeofence = clockInWithinGeofence; }

    public Boolean getClockOutWithinGeofence() { return clockOutWithinGeofence; }
    public void setClockOutWithinGeofence(Boolean clockOutWithinGeofence) { this.clockOutWithinGeofence = clockOutWithinGeofence; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public BigDecimal getRegularHours() { return regularHours; }
    public void setRegularHours(BigDecimal regularHours) { this.regularHours = regularHours; }

    public BigDecimal getOvertimeHours() { return overtimeHours; }
    public void setOvertimeHours(BigDecimal overtimeHours) { this.overtimeHours = overtimeHours; }

    public Integer getBreakMinutes() { return breakMinutes; }
    public void setBreakMinutes(Integer breakMinutes) { this.breakMinutes = breakMinutes; }

    public Integer getLateMinutes() { return lateMinutes; }
    public void setLateMinutes(Integer lateMinutes) { this.lateMinutes = lateMinutes; }

    public Integer getEarlyDepartureMins() { return earlyDepartureMins; }
    public void setEarlyDepartureMins(Integer earlyDepartureMins) { this.earlyDepartureMins = earlyDepartureMins; }

    public Boolean getAutoClockedOut() { return autoClockedOut; }
    public void setAutoClockedOut(Boolean autoClockedOut) { this.autoClockedOut = autoClockedOut; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Long getShiftScheduleId() { return shiftScheduleId; }
    public void setShiftScheduleId(Long shiftScheduleId) { this.shiftScheduleId = shiftScheduleId; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
