package com.arthmatic.shumelahire.dto.compensation;

import com.arthmatic.shumelahire.entity.compensation.SalaryBand;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class SalaryBandResponse {

    private Long id;
    private Long payGradeId;
    private String payGradeCode;
    private String bandName;
    private String jobFamily;
    private String jobLevel;
    private BigDecimal minSalary;
    private BigDecimal maxSalary;
    private String currency;
    private LocalDateTime effectiveDate;
    private LocalDateTime expiryDate;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static SalaryBandResponse fromEntity(SalaryBand band) {
        SalaryBandResponse response = new SalaryBandResponse();
        response.setId(band.getId());
        if (band.getPayGrade() != null) {
            response.setPayGradeId(band.getPayGrade().getId());
            response.setPayGradeCode(band.getPayGrade().getCode());
        }
        response.setBandName(band.getBandName());
        response.setJobFamily(band.getJobFamily());
        response.setJobLevel(band.getJobLevel());
        response.setMinSalary(band.getMinSalary());
        response.setMaxSalary(band.getMaxSalary());
        response.setCurrency(band.getCurrency());
        response.setEffectiveDate(band.getEffectiveDate());
        response.setExpiryDate(band.getExpiryDate());
        response.setIsActive(band.getIsActive());
        response.setCreatedAt(band.getCreatedAt());
        response.setUpdatedAt(band.getUpdatedAt());
        return response;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getPayGradeId() { return payGradeId; }
    public void setPayGradeId(Long payGradeId) { this.payGradeId = payGradeId; }

    public String getPayGradeCode() { return payGradeCode; }
    public void setPayGradeCode(String payGradeCode) { this.payGradeCode = payGradeCode; }

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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
