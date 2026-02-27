package com.arthmatic.shumelahire.dto.compensation;

import com.arthmatic.shumelahire.entity.compensation.PayGrade;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PayGradeResponse {

    private Long id;
    private String code;
    private String name;
    private String description;
    private BigDecimal minSalary;
    private BigDecimal midSalary;
    private BigDecimal maxSalary;
    private String currency;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PayGradeResponse fromEntity(PayGrade payGrade) {
        PayGradeResponse response = new PayGradeResponse();
        response.setId(payGrade.getId());
        response.setCode(payGrade.getCode());
        response.setName(payGrade.getName());
        response.setDescription(payGrade.getDescription());
        response.setMinSalary(payGrade.getMinSalary());
        response.setMidSalary(payGrade.getMidSalary());
        response.setMaxSalary(payGrade.getMaxSalary());
        response.setCurrency(payGrade.getCurrency());
        response.setIsActive(payGrade.getIsActive());
        response.setCreatedAt(payGrade.getCreatedAt());
        response.setUpdatedAt(payGrade.getUpdatedAt());
        return response;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getMinSalary() { return minSalary; }
    public void setMinSalary(BigDecimal minSalary) { this.minSalary = minSalary; }

    public BigDecimal getMidSalary() { return midSalary; }
    public void setMidSalary(BigDecimal midSalary) { this.midSalary = midSalary; }

    public BigDecimal getMaxSalary() { return maxSalary; }
    public void setMaxSalary(BigDecimal maxSalary) { this.maxSalary = maxSalary; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
