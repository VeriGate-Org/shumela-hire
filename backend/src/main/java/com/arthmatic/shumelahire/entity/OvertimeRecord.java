package com.arthmatic.shumelahire.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "overtime_records")
public class OvertimeRecord extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "attendance_record_id")
    private AttendanceRecord attendanceRecord;

    @NotNull
    @Column(name = "overtime_date", nullable = false)
    private LocalDate overtimeDate;

    @NotNull
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal hours;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OvertimeType type;

    @Column(name = "rate_multiplier", nullable = false, precision = 4, scale = 2)
    private BigDecimal rateMultiplier = new BigDecimal("1.5");

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OvertimeStatus status = OvertimeStatus.PENDING;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by_id")
    private Employee approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public enum OvertimeType {
        WEEKDAY, WEEKEND, PUBLIC_HOLIDAY, NIGHT
    }

    public enum OvertimeStatus {
        PENDING, APPROVED, REJECTED, PAID
    }

    // SA overtime rate multiplier defaults
    public static BigDecimal getDefaultMultiplier(OvertimeType type) {
        return switch (type) {
            case WEEKDAY -> new BigDecimal("1.5");
            case WEEKEND, NIGHT -> new BigDecimal("2.0");
            case PUBLIC_HOLIDAY -> new BigDecimal("2.0");
        };
    }

    // SA max weekly overtime hours
    public static final BigDecimal MAX_WEEKLY_OVERTIME_HOURS = new BigDecimal("10");

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

    public AttendanceRecord getAttendanceRecord() { return attendanceRecord; }
    public void setAttendanceRecord(AttendanceRecord attendanceRecord) { this.attendanceRecord = attendanceRecord; }

    public LocalDate getOvertimeDate() { return overtimeDate; }
    public void setOvertimeDate(LocalDate overtimeDate) { this.overtimeDate = overtimeDate; }

    public BigDecimal getHours() { return hours; }
    public void setHours(BigDecimal hours) { this.hours = hours; }

    public OvertimeType getType() { return type; }
    public void setType(OvertimeType type) { this.type = type; }

    public BigDecimal getRateMultiplier() { return rateMultiplier; }
    public void setRateMultiplier(BigDecimal rateMultiplier) { this.rateMultiplier = rateMultiplier; }

    public OvertimeStatus getStatus() { return status; }
    public void setStatus(OvertimeStatus status) { this.status = status; }

    public Employee getApprovedBy() { return approvedBy; }
    public void setApprovedBy(Employee approvedBy) { this.approvedBy = approvedBy; }

    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
