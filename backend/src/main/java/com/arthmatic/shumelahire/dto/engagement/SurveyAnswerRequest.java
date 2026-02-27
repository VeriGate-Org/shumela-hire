package com.arthmatic.shumelahire.dto.engagement;

import jakarta.validation.constraints.NotNull;

import java.util.List;

public class SurveyAnswerRequest {

    @NotNull(message = "Survey ID is required")
    private Long surveyId;

    private String anonymousToken;
    private Long respondentId;
    private List<AnswerItem> answers;

    public static class AnswerItem {
        @NotNull
        private Long questionId;
        private Integer ratingValue;
        private String textValue;
        private String selectedOption;

        public Long getQuestionId() { return questionId; }
        public void setQuestionId(Long questionId) { this.questionId = questionId; }

        public Integer getRatingValue() { return ratingValue; }
        public void setRatingValue(Integer ratingValue) { this.ratingValue = ratingValue; }

        public String getTextValue() { return textValue; }
        public void setTextValue(String textValue) { this.textValue = textValue; }

        public String getSelectedOption() { return selectedOption; }
        public void setSelectedOption(String selectedOption) { this.selectedOption = selectedOption; }
    }

    // Getters and Setters
    public Long getSurveyId() { return surveyId; }
    public void setSurveyId(Long surveyId) { this.surveyId = surveyId; }

    public String getAnonymousToken() { return anonymousToken; }
    public void setAnonymousToken(String anonymousToken) { this.anonymousToken = anonymousToken; }

    public Long getRespondentId() { return respondentId; }
    public void setRespondentId(Long respondentId) { this.respondentId = respondentId; }

    public List<AnswerItem> getAnswers() { return answers; }
    public void setAnswers(List<AnswerItem> answers) { this.answers = answers; }
}
