package com.arthmatic.shumelahire.dto.goal;

import com.arthmatic.shumelahire.entity.goal.KeyResultStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public class KeyResultRequest {

    @NotBlank(message = "Metric name is required")
    private String metric;

    private String description;

    @NotNull(message = "Target value is required")
    @Positive(message = "Target value must be positive")
    private BigDecimal targetValue;

    private BigDecimal currentValue;

    private String unitOfMeasure;

    private KeyResultStatus status;

    private Integer sortOrder;

    // Getters and Setters

    public String getMetric() { return metric; }
    public void setMetric(String metric) { this.metric = metric; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getTargetValue() { return targetValue; }
    public void setTargetValue(BigDecimal targetValue) { this.targetValue = targetValue; }

    public BigDecimal getCurrentValue() { return currentValue; }
    public void setCurrentValue(BigDecimal currentValue) { this.currentValue = currentValue; }

    public String getUnitOfMeasure() { return unitOfMeasure; }
    public void setUnitOfMeasure(String unitOfMeasure) { this.unitOfMeasure = unitOfMeasure; }

    public KeyResultStatus getStatus() { return status; }
    public void setStatus(KeyResultStatus status) { this.status = status; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
}
