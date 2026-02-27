package com.arthmatic.shumelahire.entity.goal;

import com.arthmatic.shumelahire.entity.TenantAwareEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "goals")
public class Goal extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Goal title is required")
    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull(message = "Goal type is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private GoalType type;

    @NotNull(message = "Goal status is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private GoalStatus status = GoalStatus.DRAFT;

    @NotNull(message = "Owner type is required")
    @Enumerated(EnumType.STRING)
    @Column(name = "owner_type", nullable = false, length = 20)
    private OwnerType ownerType;

    @NotBlank(message = "Owner ID is required")
    @Column(name = "owner_id", nullable = false, length = 50)
    private String ownerId;

    @NotNull(message = "Period is required")
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private GoalPeriod period;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_goal_id")
    private Goal parentGoal;

    @OneToMany(mappedBy = "parentGoal", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<Goal> childGoals = new ArrayList<>();

    @OneToMany(mappedBy = "goal", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<KeyResult> keyResults = new ArrayList<>();

    @OneToMany(mappedBy = "goal", cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    private List<GoalLink> goalLinks = new ArrayList<>();

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by", length = 50)
    private String createdBy;

    public Goal() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Business methods

    public boolean canBeActivated() {
        return status == GoalStatus.DRAFT;
    }

    public boolean canBeCompleted() {
        return status == GoalStatus.ACTIVE;
    }

    public boolean canBeCancelled() {
        return status == GoalStatus.DRAFT || status == GoalStatus.ACTIVE;
    }

    public boolean isEditable() {
        return status == GoalStatus.DRAFT || status == GoalStatus.ACTIVE;
    }

    // Getters and Setters

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public GoalType getType() { return type; }
    public void setType(GoalType type) { this.type = type; }

    public GoalStatus getStatus() { return status; }
    public void setStatus(GoalStatus status) { this.status = status; }

    public OwnerType getOwnerType() { return ownerType; }
    public void setOwnerType(OwnerType ownerType) { this.ownerType = ownerType; }

    public String getOwnerId() { return ownerId; }
    public void setOwnerId(String ownerId) { this.ownerId = ownerId; }

    public GoalPeriod getPeriod() { return period; }
    public void setPeriod(GoalPeriod period) { this.period = period; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Goal getParentGoal() { return parentGoal; }
    public void setParentGoal(Goal parentGoal) { this.parentGoal = parentGoal; }

    public List<Goal> getChildGoals() { return childGoals; }
    public void setChildGoals(List<Goal> childGoals) { this.childGoals = childGoals; }

    public List<KeyResult> getKeyResults() { return keyResults; }
    public void setKeyResults(List<KeyResult> keyResults) { this.keyResults = keyResults; }

    public List<GoalLink> getGoalLinks() { return goalLinks; }
    public void setGoalLinks(List<GoalLink> goalLinks) { this.goalLinks = goalLinks; }

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
}
