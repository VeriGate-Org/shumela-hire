package com.arthmatic.shumelahire.dto.engagement;

import com.arthmatic.shumelahire.entity.engagement.QuestionType;
import jakarta.validation.constraints.NotBlank;

public class SurveyQuestionRequest {

    @NotBlank(message = "Question text is required")
    private String questionText;

    private QuestionType questionType = QuestionType.RATING;
    private Integer displayOrder = 0;
    private Boolean isRequired = true;
    private String options;

    // Getters and Setters
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
