package com.arthmatic.shumelahire.entity.attendance;

import com.arthmatic.shumelahire.entity.Employee;
import com.arthmatic.shumelahire.entity.TenantAwareEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "shift_schedule_id")
    private ShiftSchedule shiftSchedule;

    @NotNull
    @Column(name = "record_date", nullable = false)
    private LocalDate recordDate;

    @Column(name = "clock_in_time")
    private LocalDateTime clockInTime;

    @Column(name = "clock_out_time")
    private LocalDateTime clockOutTime;

    @Column(name = "clock_in_latitude")
    private Double clockInLatitude;

    @Column(name = "clock_in_longitude")
    private Double clockInLongitude;

    @Column(name = "clock_out_latitude")
    private Double clockOutLatitude;

    @Column(name = "clock_out_longitude")
    private Double clockOutLongitude;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clock_in_geofence_id")
    private Geofence clockInGeofence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clock_out_geofence_id")
    private Geofence clockOutGeofence;

    @Column(name = "clock_in_within_geofence")
    private Boolean clockInWithinGeofence;

    @Column(name = "clock_out_within_geofence")
    private Boolean clockOutWithinGeofence;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private AttendanceStatus status = AttendanceStatus.PRESENT;

    @Column(name = "total_hours", precision = 5, scale = 2)
    private BigDecimal totalHours;

    @Column(name = "regular_hours", precision = 5, scale = 2)
    private BigDecimal regularHours;

    @Column(name = "overtime_hours", precision = 5, scale = 2)
    private BigDecimal overtimeHours;

    @Column(name = "break_minutes")
    private Integer breakMinutes = 0;

    @Column(name = "is_late_arrival", nullable = false)
    private Boolean isLateArrival = false;

    @Column(name = "is_early_departure", nullable = false)
    private Boolean isEarlyDeparture = false;

    @Column(name = "late_minutes")
    private Integer lateMinutes = 0;

    @Column(name = "early_departure_minutes")
    private Integer earlyDepartureMinutes = 0;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "auto_clocked_out", nullable = false)
    private Boolean autoClockedOut = false;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public AttendanceRecord() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

    public ShiftSchedule getShiftSchedule() { return shiftSchedule; }
    public void setShiftSchedule(ShiftSchedule shiftSchedule) { this.shiftSchedule = shiftSchedule; }

    public LocalDate getRecordDate() { return recordDate; }
    public void setRecordDate(LocalDate recordDate) { this.recordDate = recordDate; }

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

    public Geofence getClockInGeofence() { return clockInGeofence; }
    public void setClockInGeofence(Geofence clockInGeofence) { this.clockInGeofence = clockInGeofence; }

    public Geofence getClockOutGeofence() { return clockOutGeofence; }
    public void setClockOutGeofence(Geofence clockOutGeofence) { this.clockOutGeofence = clockOutGeofence; }

    public Boolean getClockInWithinGeofence() { return clockInWithinGeofence; }
    public void setClockInWithinGeofence(Boolean clockInWithinGeofence) { this.clockInWithinGeofence = clockInWithinGeofence; }

    public Boolean getClockOutWithinGeofence() { return clockOutWithinGeofence; }
    public void setClockOutWithinGeofence(Boolean clockOutWithinGeofence) { this.clockOutWithinGeofence = clockOutWithinGeofence; }

    public AttendanceStatus getStatus() { return status; }
    public void setStatus(AttendanceStatus status) { this.status = status; }

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
