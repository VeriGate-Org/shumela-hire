package com.example.recruitment.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "screening_questions")
public class ScreeningQuestion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotNull(message = "Job posting ID is required")
    @Column(name = "job_posting_id", nullable = false)
    private Long jobPostingId;
    
    @NotBlank(message = "Question text is required")
    @Column(name = "question_text", nullable = false, length = 1000)
    private String questionText;
    
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Question type is required")
    @Column(name = "question_type", nullable = false)
    private QuestionType questionType;
    
    @Column(name = "is_required", nullable = false)
    private Boolean isRequired = false;
    
    @Column(name = "display_order", nullable = false)
    private Integer displayOrder = 0;
    
    @Column(name = "question_options", columnDefinition = "TEXT")
    private String questionOptions; // JSON array for dropdown/multiple choice options
    
    @Column(name = "validation_rules", columnDefinition = "TEXT")
    private String validationRules; // JSON for custom validation
    
    @Column(name = "help_text", length = 500)
    private String helpText;
    
    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;
    
    @NotBlank(message = "Created by is required")
    @Column(name = "created_by", nullable = false, length = 100)
    private String createdBy;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;
    
    @OneToMany(mappedBy = "screeningQuestion", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<com.example.recruitment.entity.ScreeningAnswer> answers = new ArrayList<>();
    
    // Constructors
    public ScreeningQuestion() {}
    
    public ScreeningQuestion(Long jobPostingId, String questionText, QuestionType questionType, String createdBy) {
        this.jobPostingId = jobPostingId;
        this.questionText = questionText;
        this.questionType = questionType;
        this.createdBy = createdBy;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getJobPostingId() {
        return jobPostingId;
    }
    
    public void setJobPostingId(Long jobPostingId) {
        this.jobPostingId = jobPostingId;
    }
    
    public String getQuestionText() {
        return questionText;
    }
    
    public void setQuestionText(String questionText) {
        this.questionText = questionText;
    }
    
    public QuestionType getQuestionType() {
        return questionType;
    }
    
    public void setQuestionType(QuestionType questionType) {
        this.questionType = questionType;
    }
    
    public Boolean getIsRequired() {
        return isRequired;
    }
    
    public void setIsRequired(Boolean isRequired) {
        this.isRequired = isRequired;
    }
    
    public Integer getDisplayOrder() {
        return displayOrder;
    }
    
    public void setDisplayOrder(Integer displayOrder) {
        this.displayOrder = displayOrder;
    }
    
    public String getQuestionOptions() {
        return questionOptions;
    }
    
    public void setQuestionOptions(String questionOptions) {
        this.questionOptions = questionOptions;
    }
    
    public String getValidationRules() {
        return validationRules;
    }
    
    public void setValidationRules(String validationRules) {
        this.validationRules = validationRules;
    }
    
    public String getHelpText() {
        return helpText;
    }
    
    public void setHelpText(String helpText) {
        this.helpText = helpText;
    }
    
    public Boolean getIsActive() {
        return isActive;
    }
    
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }
    
    public String getCreatedBy() {
        return createdBy;
    }
    
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    public List<com.example.recruitment.entity.ScreeningAnswer> getAnswers() {
        return answers;
    }
    
    public void setAnswers(List<com.example.recruitment.entity.ScreeningAnswer> answers) {
        this.answers = answers;
    }
    
    // Helper methods
    public boolean isRequired() {
        return Boolean.TRUE.equals(isRequired);
    }
    
    public boolean isActive() {
        return Boolean.TRUE.equals(isActive);
    }
    
    public boolean hasOptions() {
        return questionType == QuestionType.DROPDOWN || 
               questionType == QuestionType.MULTIPLE_CHOICE || 
               questionType == QuestionType.CHECKBOX;
    }
    
    @Override
    public String toString() {
        return "ScreeningQuestion{" +
                "id=" + id +
                ", jobPostingId=" + jobPostingId +
                ", questionText='" + questionText + '\'' +
                ", questionType=" + questionType +
                ", isRequired=" + isRequired +
                ", displayOrder=" + displayOrder +
                '}';
    }
}