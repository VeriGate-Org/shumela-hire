package com.arthmatic.shumelahire.dto.engagement;

import com.arthmatic.shumelahire.entity.engagement.Survey;
import com.arthmatic.shumelahire.entity.engagement.SurveyStatus;
import com.arthmatic.shumelahire.entity.engagement.SurveyType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

public class SurveyResponseDTO {

    private Long id;
    private String title;
    private String description;
    private SurveyType surveyType;
    private SurveyStatus status;
    private String createdBy;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private Boolean isAnonymous;
    private List<SurveyQuestionResponseDTO> questions;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static SurveyResponseDTO fromEntity(Survey survey) {
        SurveyResponseDTO dto = new SurveyResponseDTO();
        dto.setId(survey.getId());
        dto.setTitle(survey.getTitle());
        dto.setDescription(survey.getDescription());
        dto.setSurveyType(survey.getSurveyType());
        dto.setStatus(survey.getStatus());
        dto.setCreatedBy(survey.getCreatedBy());
        dto.setStartDate(survey.getStartDate());
        dto.setEndDate(survey.getEndDate());
        dto.setIsAnonymous(survey.getIsAnonymous());
        dto.setCreatedAt(survey.getCreatedAt());
        dto.setUpdatedAt(survey.getUpdatedAt());
        return dto;
    }

    public static SurveyResponseDTO fromEntityWithQuestions(Survey survey) {
        SurveyResponseDTO dto = fromEntity(survey);
        if (survey.getQuestions() != null) {
            dto.setQuestions(survey.getQuestions().stream()
                    .map(SurveyQuestionResponseDTO::fromEntity)
                    .collect(Collectors.toList()));
        }
        return dto;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public SurveyType getSurveyType() { return surveyType; }
    public void setSurveyType(SurveyType surveyType) { this.surveyType = surveyType; }

    public SurveyStatus getStatus() { return status; }
    public void setStatus(SurveyStatus status) { this.status = status; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public LocalDateTime getStartDate() { return startDate; }
    public void setStartDate(LocalDateTime startDate) { this.startDate = startDate; }

    public LocalDateTime getEndDate() { return endDate; }
    public void setEndDate(LocalDateTime endDate) { this.endDate = endDate; }

    public Boolean getIsAnonymous() { return isAnonymous; }
    public void setIsAnonymous(Boolean isAnonymous) { this.isAnonymous = isAnonymous; }

    public List<SurveyQuestionResponseDTO> getQuestions() { return questions; }
    public void setQuestions(List<SurveyQuestionResponseDTO> questions) { this.questions = questions; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
