package com.arthmatic.shumelahire.dto.engagement;

import com.arthmatic.shumelahire.entity.engagement.QuestionType;
import com.arthmatic.shumelahire.entity.engagement.SurveyQuestion;

public class SurveyQuestionResponseDTO {

    private Long id;
    private Long surveyId;
    private String questionText;
    private QuestionType questionType;
    private Integer displayOrder;
    private Boolean isRequired;
    private String options;

    public static SurveyQuestionResponseDTO fromEntity(SurveyQuestion question) {
        SurveyQuestionResponseDTO dto = new SurveyQuestionResponseDTO();
        dto.setId(question.getId());
        if (question.getSurvey() != null) {
            dto.setSurveyId(question.getSurvey().getId());
        }
        dto.setQuestionText(question.getQuestionText());
        dto.setQuestionType(question.getQuestionType());
        dto.setDisplayOrder(question.getDisplayOrder());
        dto.setIsRequired(question.getIsRequired());
        dto.setOptions(question.getOptions());
        return dto;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getSurveyId() { return surveyId; }
    public void setSurveyId(Long surveyId) { this.surveyId = surveyId; }

    public String getQuestionText() { return questionText; }
    public void setQuestionText(String questionText) { this.questionText = questionText; }

    public QuestionType getQuestionType() { return questionType; }
    public void setQuestionType(QuestionType questionType) { this.questionType = questionType; }

    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }

    public Boolean getIsRequired() { return isRequired; }
    public void setIsRequired(Boolean isRequired) { this.isRequired = isRequired; }

    public String getOptions() { return options; }
    public void setOptions(String options) { this.options = options; }
}
