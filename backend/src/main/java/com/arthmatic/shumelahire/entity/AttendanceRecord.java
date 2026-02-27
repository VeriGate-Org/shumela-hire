package com.arthmatic.shumelahire.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "attendance_records")
public class AttendanceRecord extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @NotNull
    @Column(name = "attendance_date", nullable = false)
    private LocalDate attendanceDate;

    @Column(name = "clock_in_time")
    private LocalDateTime clockInTime;

    @Column(name = "clock_out_time")
    private LocalDateTime clockOutTime;

    @Enumerated(EnumType.STRING)
    @Column(name = "clock_in_method", length = 30)
    private ClockMethod clockInMethod;

    @Enumerated(EnumType.STRING)
    @Column(name = "clock_out_method", length = 30)
    private ClockMethod clockOutMethod;

    // Geolocation for clock-in
    @Column(name = "clock_in_latitude", precision = 10, scale = 7)
    private BigDecimal clockInLatitude;

    @Column(name = "clock_in_longitude", precision = 10, scale = 7)
    private BigDecimal clockInLongitude;

    // Geolocation for clock-out
    @Column(name = "clock_out_latitude", precision = 10, scale = 7)
    private BigDecimal clockOutLatitude;

    @Column(name = "clock_out_longitude", precision = 10, scale = 7)
    private BigDecimal clockOutLongitude;

    @Column(name = "clock_in_within_geofence")
    private Boolean clockInWithinGeofence;

    @Column(name = "clock_out_within_geofence")
    private Boolean clockOutWithinGeofence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "geofence_id")
    private Geofence geofence;

    @Column(name = "clock_in_ip_address", length = 50)
    private String clockInIpAddress;

    @Column(name = "clock_out_ip_address", length = 50)
    private String clockOutIpAddress;

    @Column(name = "scheduled_start_time")
    private LocalTime scheduledStartTime;

    @Column(name = "scheduled_end_time")
    private LocalTime scheduledEndTime;

    @Column(name = "total_hours_worked", precision = 5, scale = 2)
    private BigDecimal totalHoursWorked;

    @Column(name = "regular_hours", precision = 5, scale = 2)
    private BigDecimal regularHours;

    @Column(name = "overtime_hours", precision = 5, scale = 2)
    private BigDecimal overtimeHours;

    @Column(name = "break_duration_minutes")
    private Integer breakDurationMinutes;

    @Column(name = "is_late_arrival")
    private Boolean lateArrival = false;

    @Column(name = "late_minutes")
    private Integer lateMinutes;

    @Column(name = "is_early_departure")
    private Boolean earlyDeparture = false;

    @Column(name = "early_departure_minutes")
    private Integer earlyDepartureMinutes;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AttendanceStatus status = AttendanceStatus.CLOCKED_IN;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_id")
    private Shift shift;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private Employee approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "device_info", length = 500)
    private String deviceInfo;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum ClockMethod {
        BIOMETRIC, WEB_PORTAL, MOBILE_APP, KIOSK, MANUAL, AD_LOGIN
    }

    public enum AttendanceStatus {
        CLOCKED_IN, CLOCKED_OUT, ON_BREAK, ABSENT, LEAVE, HOLIDAY, PENDING_APPROVAL, APPROVED, REJECTED
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

    public LocalDate getAttendanceDate() { return attendanceDate; }
    public void setAttendanceDate(LocalDate attendanceDate) { this.attendanceDate = attendanceDate; }

    public LocalDateTime getClockInTime() { return clockInTime; }
    public void setClockInTime(LocalDateTime clockInTime) { this.clockInTime = clockInTime; }

    public LocalDateTime getClockOutTime() { return clockOutTime; }
    public void setClockOutTime(LocalDateTime clockOutTime) { this.clockOutTime = clockOutTime; }

    public ClockMethod getClockInMethod() { return clockInMethod; }
    public void setClockInMethod(ClockMethod clockInMethod) { this.clockInMethod = clockInMethod; }

    public ClockMethod getClockOutMethod() { return clockOutMethod; }
    public void setClockOutMethod(ClockMethod clockOutMethod) { this.clockOutMethod = clockOutMethod; }

    public BigDecimal getClockInLatitude() { return clockInLatitude; }
    public void setClockInLatitude(BigDecimal clockInLatitude) { this.clockInLatitude = clockInLatitude; }

    public BigDecimal getClockInLongitude() { return clockInLongitude; }
    public void setClockInLongitude(BigDecimal clockInLongitude) { this.clockInLongitude = clockInLongitude; }

    public BigDecimal getClockOutLatitude() { return clockOutLatitude; }
    public void setClockOutLatitude(BigDecimal clockOutLatitude) { this.clockOutLatitude = clockOutLatitude; }

    public BigDecimal getClockOutLongitude() { return clockOutLongitude; }
    public void setClockOutLongitude(BigDecimal clockOutLongitude) { this.clockOutLongitude = clockOutLongitude; }

    public Boolean getClockInWithinGeofence() { return clockInWithinGeofence; }
    public void setClockInWithinGeofence(Boolean clockInWithinGeofence) { this.clockInWithinGeofence = clockInWithinGeofence; }

    public Boolean getClockOutWithinGeofence() { return clockOutWithinGeofence; }
    public void setClockOutWithinGeofence(Boolean clockOutWithinGeofence) { this.clockOutWithinGeofence = clockOutWithinGeofence; }

    public Geofence getGeofence() { return geofence; }
    public void setGeofence(Geofence geofence) { this.geofence = geofence; }

    public String getClockInIpAddress() { return clockInIpAddress; }
    public void setClockInIpAddress(String clockInIpAddress) { this.clockInIpAddress = clockInIpAddress; }

    public String getClockOutIpAddress() { return clockOutIpAddress; }
    public void setClockOutIpAddress(String clockOutIpAddress) { this.clockOutIpAddress = clockOutIpAddress; }

    public LocalTime getScheduledStartTime() { return scheduledStartTime; }
    public void setScheduledStartTime(LocalTime scheduledStartTime) { this.scheduledStartTime = scheduledStartTime; }

    public LocalTime getScheduledEndTime() { return scheduledEndTime; }
    public void setScheduledEndTime(LocalTime scheduledEndTime) { this.scheduledEndTime = scheduledEndTime; }

    public BigDecimal getTotalHoursWorked() { return totalHoursWorked; }
    public void setTotalHoursWorked(BigDecimal totalHoursWorked) { this.totalHoursWorked = totalHoursWorked; }

    public BigDecimal getRegularHours() { return regularHours; }
    public void setRegularHours(BigDecimal regularHours) { this.regularHours = regularHours; }

    public BigDecimal getOvertimeHours() { return overtimeHours; }
    public void setOvertimeHours(BigDecimal overtimeHours) { this.overtimeHours = overtimeHours; }

    public Integer getBreakDurationMinutes() { return breakDurationMinutes; }
    public void setBreakDurationMinutes(Integer breakDurationMinutes) { this.breakDurationMinutes = breakDurationMinutes; }

    public Boolean getLateArrival() { return lateArrival; }
    public void setLateArrival(Boolean lateArrival) { this.lateArrival = lateArrival; }

    public Integer getLateMinutes() { return lateMinutes; }
    public void setLateMinutes(Integer lateMinutes) { this.lateMinutes = lateMinutes; }

    public Boolean getEarlyDeparture() { return earlyDeparture; }
    public void setEarlyDeparture(Boolean earlyDeparture) { this.earlyDeparture = earlyDeparture; }

    public Integer getEarlyDepartureMinutes() { return earlyDepartureMinutes; }
    public void setEarlyDepartureMinutes(Integer earlyDepartureMinutes) { this.earlyDepartureMinutes = earlyDepartureMinutes; }

    public AttendanceStatus getStatus() { return status; }
    public void setStatus(AttendanceStatus status) { this.status = status; }

    public Shift getShift() { return shift; }
    public void setShift(Shift shift) { this.shift = shift; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Employee getApprovedBy() { return approvedBy; }
    public void setApprovedBy(Employee approvedBy) { this.approvedBy = approvedBy; }

    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }

    public String getDeviceInfo() { return deviceInfo; }
    public void setDeviceInfo(String deviceInfo) { this.deviceInfo = deviceInfo; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
