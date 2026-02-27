package com.arthmatic.shumelahire.dto.engagement;

import com.arthmatic.shumelahire.entity.engagement.SurveyType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

public class SurveyRequest {

    @NotBlank(message = "Title is required")
    private String title;

    private String description;

    @NotNull(message = "Survey type is required")
    private SurveyType surveyType;

    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean isAnonymous = true;
    private List<SurveyQuestionRequest> questions;

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public SurveyType getSurveyType() { return surveyType; }
    public void setSurveyType(SurveyType surveyType) { this.surveyType = surveyType; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public Boolean getIsAnonymous() { return isAnonymous; }
    public void setIsAnonymous(Boolean isAnonymous) { this.isAnonymous = isAnonymous; }

    public List<SurveyQuestionRequest> getQuestions() { return questions; }
    public void setQuestions(List<SurveyQuestionRequest> questions) { this.questions = questions; }
}
