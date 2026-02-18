package com.arthmatic.shumelahire.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "screening_answers", 
       uniqueConstraints = @UniqueConstraint(columnNames = {"application_id", "screening_question_id"}))
public class ScreeningAnswer {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Application ID is required")
    @Column(name = "application_id", nullable = false)
    private Long applicationId;
    
    @NotNull(message = "Screening question is required")
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screening_question_id", nullable = false)
    private ScreeningQuestion screeningQuestion;
    
    @Column(name = "answer_value", columnDefinition = "TEXT")
    private String answerValue;
    
    @Column(name = "answer_file_url")
    private String answerFileUrl; // For file upload questions
    
    @Column(name = "answer_file_name")
    private String answerFileName;
    
    @Column(name = "is_valid")
    private Boolean isValid = true;
    
    @Column(name = "validation_message", length = 500)
    private String validationMessage;
    
    @CreationTimestamp
    @Column(name = "answered_at", nullable = false, updatable = false)
    private LocalDateTime answeredAt;
    
    // Constructors
    public ScreeningAnswer() {}
    
    public ScreeningAnswer(Long applicationId, ScreeningQuestion screeningQuestion, String answerValue) {
        this.applicationId = applicationId;
        this.screeningQuestion = screeningQuestion;
        this.answerValue = answerValue;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getApplicationId() {
        return applicationId;
    }
    
    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }
    
    public ScreeningQuestion getScreeningQuestion() {
        return screeningQuestion;
    }
    
    public void setScreeningQuestion(ScreeningQuestion screeningQuestion) {
        this.screeningQuestion = screeningQuestion;
    }
    
    public String getAnswerValue() {
        return answerValue;
    }
    
    public void setAnswerValue(String answerValue) {
        this.answerValue = answerValue;
    }
    
    public String getAnswerFileUrl() {
        return answerFileUrl;
    }
    
    public void setAnswerFileUrl(String answerFileUrl) {
        this.answerFileUrl = answerFileUrl;
    }
    
    public String getAnswerFileName() {
        return answerFileName;
    }
    
    public void setAnswerFileName(String answerFileName) {
        this.answerFileName = answerFileName;
    }
    
    public Boolean getIsValid() {
        return isValid;
    }
    
    public void setIsValid(Boolean isValid) {
        this.isValid = isValid;
    }
    
    public String getValidationMessage() {
        return validationMessage;
    }
    
    public void setValidationMessage(String validationMessage) {
        this.validationMessage = validationMessage;
    }
    
    public LocalDateTime getAnsweredAt() {
        return answeredAt;
    }
    
    public void setAnsweredAt(LocalDateTime answeredAt) {
        this.answeredAt = answeredAt;
    }
    
    // Helper methods
    public boolean isValid() {
        return Boolean.TRUE.equals(isValid);
    }
    
    public boolean hasFileAttachment() {
        return answerFileUrl != null && !answerFileUrl.trim().isEmpty();
    }
    
    public boolean isEmpty() {
        return (answerValue == null || answerValue.trim().isEmpty()) && 
               (answerFileUrl == null || answerFileUrl.trim().isEmpty());
    }
    
    @Override
    public String toString() {
        return "ScreeningAnswer{" +
                "id=" + id +
                ", applicationId=" + applicationId +
                ", questionId=" + (screeningQuestion != null ? screeningQuestion.getId() : null) +
                ", answerValue='" + answerValue + '\'' +
                ", isValid=" + isValid +
                ", answeredAt=" + answeredAt +
                '}';
    }
}