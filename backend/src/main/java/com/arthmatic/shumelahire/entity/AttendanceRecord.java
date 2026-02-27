package com.arthmatic.shumelahire.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance_records", uniqueConstraints = {
    @UniqueConstraint(name = "uk_attendance_employee_date", columnNames = {"employee_id", "record_date", "tenant_id"})
})
public class AttendanceRecord extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @NotNull
    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;

    @Column(name = "clock_in")
    private LocalDateTime clockIn;

    @Column(name = "clock_out")
    private LocalDateTime clockOut;

    @Column(name = "clock_in_latitude")
    private Double clockInLatitude;

    @Column(name = "clock_in_longitude")
    private Double clockInLongitude;

    @Column(name = "clock_out_latitude")
    private Double clockOutLatitude;

    @Column(name = "clock_out_longitude")
    private Double clockOutLongitude;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "geofence_id")
    private Geofence geofence;

    @Column(name = "clock_in_within_geofence")
    private Boolean clockInWithinGeofence;

    @Column(name = "clock_out_within_geofence")
    private Boolean clockOutWithinGeofence;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private AttendanceStatus status = AttendanceStatus.PRESENT;

    @Column(name = "regular_hours", nullable = false, precision = 5, scale = 2)
    private BigDecimal regularHours = BigDecimal.ZERO;

    @Column(name = "overtime_hours", nullable = false, precision = 5, scale = 2)
    private BigDecimal overtimeHours = BigDecimal.ZERO;

    @Column(name = "break_minutes", nullable = false)
    private Integer breakMinutes = 0;

    @Column(name = "late_minutes", nullable = false)
    private Integer lateMinutes = 0;

    @Column(name = "early_departure_mins", nullable = false)
    private Integer earlyDepartureMins = 0;

    @Column(name = "auto_clocked_out", nullable = false)
    private Boolean autoClockedOut = false;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_schedule_id")
    private ShiftSchedule shiftSchedule;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public enum AttendanceStatus {
        PRESENT, ABSENT, LATE, HALF_DAY, ON_LEAVE, PUBLIC_HOLIDAY
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

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

    public Geofence getGeofence() { return geofence; }
    public void setGeofence(Geofence geofence) { this.geofence = geofence; }

    public Boolean getClockInWithinGeofence() { return clockInWithinGeofence; }
    public void setClockInWithinGeofence(Boolean clockInWithinGeofence) { this.clockInWithinGeofence = clockInWithinGeofence; }

    public Boolean getClockOutWithinGeofence() { return clockOutWithinGeofence; }
    public void setClockOutWithinGeofence(Boolean clockOutWithinGeofence) { this.clockOutWithinGeofence = clockOutWithinGeofence; }

    public AttendanceStatus getStatus() { return status; }
    public void setStatus(AttendanceStatus status) { this.status = status; }

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

    public ShiftSchedule getShiftSchedule() { return shiftSchedule; }
    public void setShiftSchedule(ShiftSchedule shiftSchedule) { this.shiftSchedule = shiftSchedule; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
