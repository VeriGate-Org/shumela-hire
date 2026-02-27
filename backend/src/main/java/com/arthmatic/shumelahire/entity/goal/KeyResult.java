package com.arthmatic.shumelahire.entity.goal;

import com.arthmatic.shumelahire.entity.TenantAwareEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "key_results")
public class KeyResult extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Goal is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id", nullable = false)
    private Goal goal;

    @NotBlank(message = "Metric name is required")
    @Column(nullable = false, length = 200)
    private String metric;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Target value is required")
    @Positive(message = "Target value must be positive")
    @Column(name = "target_value", nullable = false, precision = 15, scale = 2)
    private BigDecimal targetValue;

    @Column(name = "current_value", nullable = false, precision = 15, scale = 2)
    private BigDecimal currentValue = BigDecimal.ZERO;

    @Column(name = "unit_of_measure", length = 50)
    private String unitOfMeasure;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private KeyResultStatus status = KeyResultStatus.ON_TRACK;

    @Column(name = "last_updated")
    private LocalDate lastUpdated;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public KeyResult() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * Calculates progress percentage as (currentValue / targetValue) * 100.
     * Returns 0 if targetValue is zero or null.
     */
    public BigDecimal getProgressPct() {
        if (targetValue == null || targetValue.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        return currentValue.multiply(BigDecimal.valueOf(100))
                .divide(targetValue, 2, RoundingMode.HALF_UP);
    }

    // Getters and Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Goal getGoal() { return goal; }
    public void setGoal(Goal goal) { this.goal = goal; }

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

    public LocalDate getLastUpdated() { return lastUpdated; }
    public void setLastUpdated(LocalDate lastUpdated) { this.lastUpdated = lastUpdated; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
