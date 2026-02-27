package com.arthmatic.shumelahire.entity.goal;

import com.arthmatic.shumelahire.entity.TenantAwareEntity;
import com.arthmatic.shumelahire.entity.performance.PerformanceCycle;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "goal_links",
        uniqueConstraints = @UniqueConstraint(name = "uk_goal_links_goal_cycle",
                columnNames = {"goal_id", "review_cycle_id"}))
public class GoalLink extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Goal is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "goal_id", nullable = false)
    private Goal goal;

    @NotNull(message = "Review cycle is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_cycle_id", nullable = false)
    private PerformanceCycle reviewCycle;

    @NotNull(message = "Weight is required")
    @DecimalMin(value = "0.0", message = "Weight must be at least 0")
    @DecimalMax(value = "100.0", message = "Weight must be at most 100")
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal weight = BigDecimal.ZERO;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 50)
    private String createdBy;

    public GoalLink() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Goal getGoal() { return goal; }
    public void setGoal(Goal goal) { this.goal = goal; }

    public PerformanceCycle getReviewCycle() { return reviewCycle; }
    public void setReviewCycle(PerformanceCycle reviewCycle) { this.reviewCycle = reviewCycle; }

    public BigDecimal getWeight() { return weight; }
    public void setWeight(BigDecimal weight) { this.weight = weight; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
}
