package com.arthmatic.shumelahire.dto.goal;

import com.arthmatic.shumelahire.entity.goal.KeyResult;
import com.arthmatic.shumelahire.entity.goal.KeyResultStatus;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class KeyResultResponse {

    private Long id;
    private Long goalId;
    private String metric;
    private String description;
    private BigDecimal targetValue;
    private BigDecimal currentValue;
    private BigDecimal progressPct;
    private String unitOfMeasure;
    private KeyResultStatus status;
    private LocalDate lastUpdated;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static KeyResultResponse fromEntity(KeyResult kr) {
        KeyResultResponse response = new KeyResultResponse();
        response.id = kr.getId();
        response.goalId = kr.getGoal().getId();
        response.metric = kr.getMetric();
        response.description = kr.getDescription();
        response.targetValue = kr.getTargetValue();
        response.currentValue = kr.getCurrentValue();
        response.progressPct = kr.getProgressPct();
        response.unitOfMeasure = kr.getUnitOfMeasure();
        response.status = kr.getStatus();
        response.lastUpdated = kr.getLastUpdated();
        response.sortOrder = kr.getSortOrder();
        response.createdAt = kr.getCreatedAt();
        response.updatedAt = kr.getUpdatedAt();
        return response;
    }

    // Getters

    public Long getId() { return id; }
    public Long getGoalId() { return goalId; }
    public String getMetric() { return metric; }
    public String getDescription() { return description; }
    public BigDecimal getTargetValue() { return targetValue; }
    public BigDecimal getCurrentValue() { return currentValue; }
    public BigDecimal getProgressPct() { return progressPct; }
    public String getUnitOfMeasure() { return unitOfMeasure; }
    public KeyResultStatus getStatus() { return status; }
    public LocalDate getLastUpdated() { return lastUpdated; }
    public Integer getSortOrder() { return sortOrder; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
