package com.arthmatic.shumelahire.entity.performance;

import com.arthmatic.shumelahire.entity.TenantAwareEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "calibration_ratings")
public class CalibrationRating extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "session_id", nullable = false)
    @NotNull(message = "Calibration session is required")
    private CalibrationSession session;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id", nullable = false)
    @NotNull(message = "Performance review is required")
    private PerformanceReview review;

    @Column(name = "employee_id", nullable = false, length = 50)
    private String employeeId;

    @Column(name = "employee_name", length = 100)
    private String employeeName;

    @Column(name = "original_rating", precision = 3, scale = 2)
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "5.0")
    private BigDecimal originalRating;

    @Column(name = "calibrated_rating", precision = 3, scale = 2)
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "5.0")
    private BigDecimal calibratedRating;

    @Column(name = "adjustment_reason", columnDefinition = "TEXT")
    private String adjustmentReason;

    @Column(name = "calibrated_by", length = 50)
    private String calibratedBy;

    @Column(name = "calibrated_at")
    private LocalDateTime calibratedAt;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public CalibrationRating() {
        this.createdAt = LocalDateTime.now();
    }

    // Business methods
    public boolean hasAdjustment() {
        return calibratedRating != null && originalRating != null
                && calibratedRating.compareTo(originalRating) != 0;
    }

    public BigDecimal getAdjustmentDelta() {
        if (calibratedRating == null || originalRating == null) return BigDecimal.ZERO;
        return calibratedRating.subtract(originalRating);
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public CalibrationSession getSession() { return session; }
    public void setSession(CalibrationSession session) { this.session = session; }

    public PerformanceReview getReview() { return review; }
    public void setReview(PerformanceReview review) { this.review = review; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public BigDecimal getOriginalRating() { return originalRating; }
    public void setOriginalRating(BigDecimal originalRating) { this.originalRating = originalRating; }

    public BigDecimal getCalibratedRating() { return calibratedRating; }
    public void setCalibratedRating(BigDecimal calibratedRating) { this.calibratedRating = calibratedRating; }

    public String getAdjustmentReason() { return adjustmentReason; }
    public void setAdjustmentReason(String adjustmentReason) { this.adjustmentReason = adjustmentReason; }

    public String getCalibratedBy() { return calibratedBy; }
    public void setCalibratedBy(String calibratedBy) { this.calibratedBy = calibratedBy; }

    public LocalDateTime getCalibratedAt() { return calibratedAt; }
    public void setCalibratedAt(LocalDateTime calibratedAt) { this.calibratedAt = calibratedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
