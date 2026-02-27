package com.arthmatic.shumelahire.dto.goal;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class GoalLinkRequest {

    @NotNull(message = "Weight is required")
    @DecimalMin(value = "0.0", message = "Weight must be at least 0")
    @DecimalMax(value = "100.0", message = "Weight must be at most 100")
    private BigDecimal weight;

    // Getters and Setters

    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }
}
