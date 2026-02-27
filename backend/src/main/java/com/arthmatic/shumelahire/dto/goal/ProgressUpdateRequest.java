package com.arthmatic.shumelahire.dto.goal;

import com.arthmatic.shumelahire.entity.goal.KeyResultStatus;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class ProgressUpdateRequest {

    @NotNull(message = "Current value is required")
    @DecimalMin(value = "0.0", message = "Current value cannot be negative")
    private BigDecimal currentValue;

    private KeyResultStatus status;

    // Getters and Setters

    public BigDecimal getCurrentValue() { return currentValue; }
    public void setCurrentValue(BigDecimal currentValue) { this.currentValue = currentValue; }

    public KeyResultStatus getStatus() { return status; }
    public void setStatus(KeyResultStatus status) { this.status = status; }
}
