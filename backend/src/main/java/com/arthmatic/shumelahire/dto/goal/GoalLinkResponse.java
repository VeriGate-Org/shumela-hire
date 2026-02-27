package com.arthmatic.shumelahire.dto.goal;

import com.arthmatic.shumelahire.entity.goal.GoalLink;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class GoalLinkResponse {

    private Long id;
    private Long goalId;
    private Long reviewCycleId;
    private String reviewCycleName;
    private BigDecimal weight;
    private String createdBy;
    private LocalDateTime createdAt;

    public static GoalLinkResponse fromEntity(GoalLink link) {
        GoalLinkResponse response = new GoalLinkResponse();
        response.id = link.getId();
        response.goalId = link.getGoal().getId();
        response.reviewCycleId = link.getReviewCycle().getId();
        response.reviewCycleName = link.getReviewCycle().getName();
        response.weight = link.getWeight();
        response.createdBy = link.getCreatedBy();
        response.createdAt = link.getCreatedAt();
        return response;
    }

    // Getters

    public Long getId() { return id; }
    public Long getGoalId() { return goalId; }
    public Long getReviewCycleId() { return reviewCycleId; }
    public String getReviewCycleName() { return reviewCycleName; }
    public BigDecimal getWeight() { return weight; }
    public String getCreatedBy() { return createdBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
