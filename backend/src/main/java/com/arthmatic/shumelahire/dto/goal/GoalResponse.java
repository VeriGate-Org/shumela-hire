package com.arthmatic.shumelahire.dto.goal;

import com.arthmatic.shumelahire.entity.goal.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class GoalResponse {

    private Long id;
    private String title;
    private String description;
    private GoalType type;
    private GoalStatus status;
    private OwnerType ownerType;
    private String ownerId;
    private GoalPeriod period;
    private LocalDate startDate;
    private LocalDate endDate;
    private Long parentGoalId;
    private Integer sortOrder;
    private Boolean isActive;
    private String tenantId;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private List<KeyResultResponse> keyResults;

    public static GoalResponse fromEntity(Goal goal) {
        GoalResponse response = new GoalResponse();
        response.id = goal.getId();
        response.title = goal.getTitle();
        response.description = goal.getDescription();
        response.type = goal.getType();
        response.status = goal.getStatus();
        response.ownerType = goal.getOwnerType();
        response.ownerId = goal.getOwnerId();
        response.period = goal.getPeriod();
        response.startDate = goal.getStartDate();
        response.endDate = goal.getEndDate();
        response.parentGoalId = goal.getParentGoal() != null ? goal.getParentGoal().getId() : null;
        response.sortOrder = goal.getSortOrder();
        response.isActive = goal.getIsActive();
        response.tenantId = goal.getTenantId();
        response.createdBy = goal.getCreatedBy();
        response.createdAt = goal.getCreatedAt();
        response.updatedAt = goal.getUpdatedAt();

        if (goal.getKeyResults() != null) {
            response.keyResults = goal.getKeyResults().stream()
                    .map(KeyResultResponse::fromEntity)
                    .collect(Collectors.toList());
        }

        return response;
    }

    // Getters

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public GoalType getType() { return type; }
    public GoalStatus getStatus() { return status; }
    public OwnerType getOwnerType() { return ownerType; }
    public String getOwnerId() { return ownerId; }
    public GoalPeriod getPeriod() { return period; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public Long getParentGoalId() { return parentGoalId; }
    public Integer getSortOrder() { return sortOrder; }
    public Boolean getIsActive() { return isActive; }
    public String getTenantId() { return tenantId; }
    public String getCreatedBy() { return createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public List<KeyResultResponse> getKeyResults() { return keyResults; }
}
