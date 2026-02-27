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
    @Column(name = "overtime_hours", nullable = false, precision = 5, scale = 2)
    private BigDecimal overtimeHours;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "overtime_type", nullable = false, length = 30)
    private OvertimeType overtimeType;

    @NotNull
    @Column(name = "rate_multiplier", nullable = false, precision = 4, scale = 2)
    private BigDecimal rateMultiplier;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private OvertimeStatus status = OvertimeStatus.PENDING;

    @Column(name = "is_pre_approved", nullable = false)
    private Boolean isPreApproved = false;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by")
    private Employee approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @Column(name = "weekly_overtime_total", precision = 5, scale = 2)
    private BigDecimal weeklyOvertimeTotal;

    @Column(name = "monthly_overtime_total", precision = 6, scale = 2)
    private BigDecimal monthlyOvertimeTotal;

    @Column(name = "exceeds_bcea_weekly_limit")
    private Boolean exceedsBceaWeeklyLimit = false;

    @Column(name = "bcea_weekly_limit_hours", precision = 4, scale = 2)
    private BigDecimal bceaWeeklyLimitHours;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum OvertimeType {
        WEEKDAY, SATURDAY, SUNDAY, PUBLIC_HOLIDAY
    }

    public enum OvertimeStatus {
        PENDING, APPROVED, REJECTED, CANCELLED
    }

    // SA BCEA rate multiplier constants
    public static final BigDecimal WEEKDAY_RATE = new BigDecimal("1.5");
    public static final BigDecimal SATURDAY_RATE = new BigDecimal("1.5");
    public static final BigDecimal SUNDAY_RATE = new BigDecimal("2.0");
    public static final BigDecimal PUBLIC_HOLIDAY_RATE = new BigDecimal("2.0");
    public static final BigDecimal BCEA_MAX_WEEKLY_OVERTIME = new BigDecimal("10.0");
    public static final BigDecimal BCEA_MAX_DAILY_OVERTIME = new BigDecimal("3.0");

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

    public AttendanceRecord getAttendanceRecord() { return attendanceRecord; }
    public void setAttendanceRecord(AttendanceRecord attendanceRecord) { this.attendanceRecord = attendanceRecord; }

    public LocalDate getOvertimeDate() { return overtimeDate; }
    public void setOvertimeDate(LocalDate overtimeDate) { this.overtimeDate = overtimeDate; }

    public BigDecimal getOvertimeHours() { return overtimeHours; }
    public void setOvertimeHours(BigDecimal overtimeHours) { this.overtimeHours = overtimeHours; }

    public OvertimeType getOvertimeType() { return overtimeType; }
    public void setOvertimeType(OvertimeType overtimeType) { this.overtimeType = overtimeType; }

    public BigDecimal getRateMultiplier() { return rateMultiplier; }
    public void setRateMultiplier(BigDecimal rateMultiplier) { this.rateMultiplier = rateMultiplier; }

    public OvertimeStatus getStatus() { return status; }
    public void setStatus(OvertimeStatus status) { this.status = status; }

    public Boolean getIsPreApproved() { return isPreApproved; }
    public void setIsPreApproved(Boolean isPreApproved) { this.isPreApproved = isPreApproved; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public Employee getApprovedBy() { return approvedBy; }
    public void setApprovedBy(Employee approvedBy) { this.approvedBy = approvedBy; }

    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public BigDecimal getWeeklyOvertimeTotal() { return weeklyOvertimeTotal; }
    public void setWeeklyOvertimeTotal(BigDecimal weeklyOvertimeTotal) { this.weeklyOvertimeTotal = weeklyOvertimeTotal; }

    public BigDecimal getMonthlyOvertimeTotal() { return monthlyOvertimeTotal; }
    public void setMonthlyOvertimeTotal(BigDecimal monthlyOvertimeTotal) { this.monthlyOvertimeTotal = monthlyOvertimeTotal; }

    public Boolean getExceedsBceaWeeklyLimit() { return exceedsBceaWeeklyLimit; }
    public void setExceedsBceaWeeklyLimit(Boolean exceedsBceaWeeklyLimit) { this.exceedsBceaWeeklyLimit = exceedsBceaWeeklyLimit; }

    public BigDecimal getBceaWeeklyLimitHours() { return bceaWeeklyLimitHours; }
    public void setBceaWeeklyLimitHours(BigDecimal bceaWeeklyLimitHours) { this.bceaWeeklyLimitHours = bceaWeeklyLimitHours; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
