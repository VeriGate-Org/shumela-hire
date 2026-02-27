package com.arthmatic.shumelahire.dto.performance;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class CalibrationRatingRequest {

    @NotNull(message = "Review ID is required")
    private Long reviewId;

    private String employeeId;
    private String employeeName;

    @NotNull(message = "Calibrated rating is required")
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "5.0")
    private BigDecimal calibratedRating;

    private String adjustmentReason;

    // Getters and Setters
    public Long getReviewId() { return reviewId; }
    public void setReviewId(Long reviewId) { this.reviewId = reviewId; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public BigDecimal getCalibratedRating() { return calibratedRating; }
    public void setCalibratedRating(BigDecimal calibratedRating) { this.calibratedRating = calibratedRating; }

    public String getAdjustmentReason() { return adjustmentReason; }
    public void setAdjustmentReason(String adjustmentReason) { this.adjustmentReason = adjustmentReason; }
}
