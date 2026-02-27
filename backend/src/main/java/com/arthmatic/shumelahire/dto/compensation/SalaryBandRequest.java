package com.arthmatic.shumelahire.dto.compensation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SalaryBandRequest {

    @NotNull(message = "Pay grade ID is required")
    private Long payGradeId;

    @NotBlank(message = "Band name is required")
    private String bandName;

    private String jobFamily;
    private String jobLevel;

    @NotNull(message = "Minimum salary is required")
    @Positive
    private BigDecimal minSalary;

    @NotNull(message = "Maximum salary is required")
    @Positive
    private BigDecimal maxSalary;

    private String currency = "ZAR";
    private LocalDateTime effectiveDate;
    private LocalDateTime expiryDate;
    private Boolean isActive = true;

    // Getters and Setters
    public Long getPayGradeId() { return payGradeId; }
    public void setPayGradeId(Long payGradeId) { this.payGradeId = payGradeId; }

    public String getBandName() { return bandName; }
    public void setBandName(String bandName) { this.bandName = bandName; }

    public String getJobFamily() { return jobFamily; }
    public void setJobFamily(String jobFamily) { this.jobFamily = jobFamily; }

    public String getJobLevel() { return jobLevel; }
    public void setJobLevel(String jobLevel) { this.jobLevel = jobLevel; }

    public BigDecimal getMinSalary() { return minSalary; }
    public void setMinSalary(BigDecimal minSalary) { this.minSalary = minSalary; }

    public BigDecimal getMaxSalary() { return maxSalary; }
    public void setMaxSalary(BigDecimal maxSalary) { this.maxSalary = maxSalary; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public LocalDateTime getEffectiveDate() { return effectiveDate; }
    public void setEffectiveDate(LocalDateTime effectiveDate) { this.effectiveDate = effectiveDate; }

    public LocalDateTime getExpiryDate() { return expiryDate; }
    public void setExpiryDate(LocalDateTime expiryDate) { this.expiryDate = expiryDate; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
