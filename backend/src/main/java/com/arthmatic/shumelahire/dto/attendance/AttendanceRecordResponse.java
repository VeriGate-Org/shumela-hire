package com.arthmatic.shumelahire.dto.attendance;

import com.arthmatic.shumelahire.entity.AttendanceRecord;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class AttendanceRecordResponse {

    private Long id;
    private Long employeeId;
    private String employeeName;
    private String employeeNumber;
    private LocalDate attendanceDate;
    private LocalDateTime clockInTime;
    private LocalDateTime clockOutTime;
    private String clockInMethod;
    private String clockOutMethod;
    private BigDecimal clockInLatitude;
    private BigDecimal clockInLongitude;
    private BigDecimal clockOutLatitude;
    private BigDecimal clockOutLongitude;
    private Boolean clockInWithinGeofence;
    private Boolean clockOutWithinGeofence;
    private LocalTime scheduledStartTime;
    private LocalTime scheduledEndTime;
    private BigDecimal totalHoursWorked;
    private BigDecimal regularHours;
    private BigDecimal overtimeHours;
    private Integer breakDurationMinutes;
    private Boolean lateArrival;
    private Integer lateMinutes;
    private Boolean earlyDeparture;
    private Integer earlyDepartureMinutes;
    private String status;
    private String shiftName;
    private String notes;
    private String deviceInfo;
    private LocalDateTime createdAt;

    public AttendanceRecordResponse() {}

    public static AttendanceRecordResponse fromEntity(AttendanceRecord record) {
        AttendanceRecordResponse r = new AttendanceRecordResponse();
        r.id = record.getId();
        r.employeeId = record.getEmployee().getId();
        r.employeeName = record.getEmployee().getFullName();
        r.employeeNumber = record.getEmployee().getEmployeeNumber();
        r.attendanceDate = record.getAttendanceDate();
        r.clockInTime = record.getClockInTime();
        r.clockOutTime = record.getClockOutTime();
        r.clockInMethod = record.getClockInMethod() != null ? record.getClockInMethod().name() : null;
        r.clockOutMethod = record.getClockOutMethod() != null ? record.getClockOutMethod().name() : null;
        r.clockInLatitude = record.getClockInLatitude();
        r.clockInLongitude = record.getClockInLongitude();
        r.clockOutLatitude = record.getClockOutLatitude();
        r.clockOutLongitude = record.getClockOutLongitude();
        r.clockInWithinGeofence = record.getClockInWithinGeofence();
        r.clockOutWithinGeofence = record.getClockOutWithinGeofence();
        r.scheduledStartTime = record.getScheduledStartTime();
        r.scheduledEndTime = record.getScheduledEndTime();
        r.totalHoursWorked = record.getTotalHoursWorked();
        r.regularHours = record.getRegularHours();
        r.overtimeHours = record.getOvertimeHours();
        r.breakDurationMinutes = record.getBreakDurationMinutes();
        r.lateArrival = record.getLateArrival();
        r.lateMinutes = record.getLateMinutes();
        r.earlyDeparture = record.getEarlyDeparture();
        r.earlyDepartureMinutes = record.getEarlyDepartureMinutes();
        r.status = record.getStatus().name();
        r.shiftName = record.getShift() != null ? record.getShift().getName() : null;
        r.notes = record.getNotes();
        r.deviceInfo = record.getDeviceInfo();
        r.createdAt = record.getCreatedAt();
        return r;
    }

    // Getters
    public Long getId() { return id; }
    public Long getEmployeeId() { return employeeId; }
    public String getEmployeeName() { return employeeName; }
    public String getEmployeeNumber() { return employeeNumber; }
    public LocalDate getAttendanceDate() { return attendanceDate; }
    public LocalDateTime getClockInTime() { return clockInTime; }
    public LocalDateTime getClockOutTime() { return clockOutTime; }
    public String getClockInMethod() { return clockInMethod; }
    public String getClockOutMethod() { return clockOutMethod; }
    public BigDecimal getClockInLatitude() { return clockInLatitude; }
    public BigDecimal getClockInLongitude() { return clockInLongitude; }
    public BigDecimal getClockOutLatitude() { return clockOutLatitude; }
    public BigDecimal getClockOutLongitude() { return clockOutLongitude; }
    public Boolean getClockInWithinGeofence() { return clockInWithinGeofence; }
    public Boolean getClockOutWithinGeofence() { return clockOutWithinGeofence; }
    public LocalTime getScheduledStartTime() { return scheduledStartTime; }
    public LocalTime getScheduledEndTime() { return scheduledEndTime; }
    public BigDecimal getTotalHoursWorked() { return totalHoursWorked; }
    public BigDecimal getRegularHours() { return regularHours; }
    public BigDecimal getOvertimeHours() { return overtimeHours; }
    public Integer getBreakDurationMinutes() { return breakDurationMinutes; }
    public Boolean getLateArrival() { return lateArrival; }
    public Integer getLateMinutes() { return lateMinutes; }
    public Boolean getEarlyDeparture() { return earlyDeparture; }
    public Integer getEarlyDepartureMinutes() { return earlyDepartureMinutes; }
    public String getStatus() { return status; }
    public String getShiftName() { return shiftName; }
    public String getNotes() { return notes; }
    public String getDeviceInfo() { return deviceInfo; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
