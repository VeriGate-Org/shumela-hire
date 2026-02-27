package com.arthmatic.shumelahire.entity.performance;

import com.arthmatic.shumelahire.entity.TenantAwareEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.DecimalMax;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "key_result_areas")
public class KeyResultArea extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", nullable = false)
    private PerformanceContract contract;

    @NotBlank(message = "KRA name is required")
    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(precision = 5, scale = 2)
    @DecimalMin(value = "0.0", message = "Weighting must be positive")
    @DecimalMax(value = "100.0", message = "Weighting cannot exceed 100%")
    private BigDecimal weighting;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @OneToMany(mappedBy = "kra", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PerformanceGoal> goals;

    public KeyResultArea() {
        this.createdAt = LocalDateTime.now();
    }

    public KeyResultArea(PerformanceContract contract, String name, BigDecimal weighting) {
        this();
        this.contract = contract;
        this.name = name;
        this.weighting = weighting;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    public boolean canBeEdited() {
        return contract != null && contract.canBeEdited() && isActive;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PerformanceContract getContract() { return contract; }
    public void setContract(PerformanceContract contract) { this.contract = contract; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getWeighting() { return weighting; }
    public void setWeighting(BigDecimal weighting) { this.weighting = weighting; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public List<PerformanceGoal> getGoals() { return goals; }
    public void setGoals(List<PerformanceGoal> goals) { this.goals = goals; }
}
