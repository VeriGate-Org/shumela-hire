package com.arthmatic.shumelahire.dto.goal;

import com.arthmatic.shumelahire.entity.goal.GoalPeriod;
import com.arthmatic.shumelahire.entity.goal.GoalType;
import com.arthmatic.shumelahire.entity.goal.OwnerType;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public class GoalRequest {

    @NotBlank(message = "Goal title is required")
    private String title;

    private String description;

    @NotNull(message = "Goal type is required")
    private GoalType type;

    @NotNull(message = "Owner type is required")
    private OwnerType ownerType;

    @NotBlank(message = "Owner ID is required")
    private String ownerId;

    @NotNull(message = "Period is required")
    private GoalPeriod period;

    private LocalDate startDate;

    private LocalDate endDate;

    private Long parentGoalId;

    private Integer sortOrder;

    @Valid
    private List<KeyResultRequest> keyResults;

    // Getters and Setters

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public GoalType getType() { return type; }
    public void setType(GoalType type) { this.type = type; }

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

    public Long getParentGoalId() { return parentGoalId; }
    public void setParentGoalId(Long parentGoalId) { this.parentGoalId = parentGoalId; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public List<KeyResultRequest> getKeyResults() { return keyResults; }
    public void setKeyResults(List<KeyResultRequest> keyResults) { this.keyResults = keyResults; }
}
