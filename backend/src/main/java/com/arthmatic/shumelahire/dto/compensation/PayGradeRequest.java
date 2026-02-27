package com.arthmatic.shumelahire.dto.compensation;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class PayGradeRequest {

    @NotBlank(message = "Code is required")
    private String code;

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @NotNull(message = "Minimum salary is required")
    @Positive
    private BigDecimal minSalary;

    @NotNull(message = "Mid salary is required")
    @Positive
    private BigDecimal midSalary;

    @NotNull(message = "Maximum salary is required")
    @Positive
    private BigDecimal maxSalary;

    private String currency = "ZAR";

    private Boolean isActive = true;

    // Getters and Setters
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
}
