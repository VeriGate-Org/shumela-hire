package com.arthmatic.shumelahire.dto.performance;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.List;

public class SelfAssessmentRequest {

    @NotNull(message = "Review ID is required")
    private Long reviewId;

    private String assessmentNotes;

    @NotNull(message = "Self rating is required")
    @DecimalMin(value = "0.0")
    @DecimalMax(value = "5.0")
    private BigDecimal selfRating;

    private List<GoalScoreRequest> goalScores;

    public static class GoalScoreRequest {
        private Long goalId;

        @DecimalMin(value = "0.0")
        @DecimalMax(value = "5.0")
        private BigDecimal score;

        private String comment;

        public Long getGoalId() { return goalId; }
        public void setGoalId(Long goalId) { this.goalId = goalId; }
        public BigDecimal getScore() { return score; }
        public void setScore(BigDecimal score) { this.score = score; }
        public String getComment() { return comment; }
        public void setComment(String comment) { this.comment = comment; }
    }

    // Getters and Setters
    public Long getReviewId() { return reviewId; }
    public void setReviewId(Long reviewId) { this.reviewId = reviewId; }

    public String getAssessmentNotes() { return assessmentNotes; }
    public void setAssessmentNotes(String assessmentNotes) { this.assessmentNotes = assessmentNotes; }

    public BigDecimal getSelfRating() { return selfRating; }
    public void setSelfRating(BigDecimal selfRating) { this.selfRating = selfRating; }

    public List<GoalScoreRequest> getGoalScores() { return goalScores; }
    public void setGoalScores(List<GoalScoreRequest> goalScores) { this.goalScores = goalScores; }
}
