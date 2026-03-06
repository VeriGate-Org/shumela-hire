package com.arthmatic.shumelahire.entity;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

@Entity
@Table(name = "interview_feedbacks",
       uniqueConstraints = @UniqueConstraint(
           columnNames = {"interview_id", "submitted_by"},
           name = "uk_interview_feedback_interviewer"
       ))
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class InterviewFeedback extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "interview_id", nullable = false)
    @JsonIgnoreProperties({"feedbacks", "hibernateLazyInitializer", "handler"})
    private Interview interview;

    @Column(name = "submitted_by", nullable = false)
    private Long submittedBy;

    @Column(name = "interviewer_name")
    private String interviewerName;

    @Column(name = "feedback", columnDefinition = "TEXT", nullable = false)
    @NotBlank(message = "Feedback text is required")
    private String feedback;

    @Column(name = "rating")
    @Min(1) @Max(5)
    private Integer rating;

    @Column(name = "communication_skills")
    @Min(1) @Max(5)
    private Integer communicationSkills;

    @Column(name = "technical_skills")
    @Min(1) @Max(5)
    private Integer technicalSkills;

    @Column(name = "cultural_fit")
    @Min(1) @Max(5)
    private Integer culturalFit;

    @Column(name = "overall_impression", columnDefinition = "TEXT")
    private String overallImpression;

    @Enumerated(EnumType.STRING)
    @Column(name = "recommendation", nullable = false)
    @NotNull(message = "Recommendation is required")
    private InterviewRecommendation recommendation;

    @Column(name = "next_steps", columnDefinition = "TEXT")
    private String nextSteps;

    @Column(name = "technical_assessment", columnDefinition = "TEXT")
    private String technicalAssessment;

    @Column(name = "candidate_questions", columnDefinition = "TEXT")
    private String candidateQuestions;

    @Column(name = "interviewer_notes", columnDefinition = "TEXT")
    private String interviewerNotes;

    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public InterviewFeedback() {
        this.submittedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Computed
    public Double getAverageSkillRating() {
        if (communicationSkills == null || technicalSkills == null || culturalFit == null) {
            return null;
        }
        return (communicationSkills + technicalSkills + culturalFit) / 3.0;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Interview getInterview() { return interview; }
    public void setInterview(Interview interview) { this.interview = interview; }

    public Long getSubmittedBy() { return submittedBy; }
    public void setSubmittedBy(Long submittedBy) { this.submittedBy = submittedBy; }

    public String getInterviewerName() { return interviewerName; }
    public void setInterviewerName(String interviewerName) { this.interviewerName = interviewerName; }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }

    public Integer getRating() { return rating; }
    public void setRating(Integer rating) { this.rating = rating; }

    public Integer getCommunicationSkills() { return communicationSkills; }
    public void setCommunicationSkills(Integer communicationSkills) { this.communicationSkills = communicationSkills; }

    public Integer getTechnicalSkills() { return technicalSkills; }
    public void setTechnicalSkills(Integer technicalSkills) { this.technicalSkills = technicalSkills; }

    public Integer getCulturalFit() { return culturalFit; }
    public void setCulturalFit(Integer culturalFit) { this.culturalFit = culturalFit; }

    public String getOverallImpression() { return overallImpression; }
    public void setOverallImpression(String overallImpression) { this.overallImpression = overallImpression; }

    public InterviewRecommendation getRecommendation() { return recommendation; }
    public void setRecommendation(InterviewRecommendation recommendation) { this.recommendation = recommendation; }

    public String getNextSteps() { return nextSteps; }
    public void setNextSteps(String nextSteps) { this.nextSteps = nextSteps; }

    public String getTechnicalAssessment() { return technicalAssessment; }
    public void setTechnicalAssessment(String technicalAssessment) { this.technicalAssessment = technicalAssessment; }

    public String getCandidateQuestions() { return candidateQuestions; }
    public void setCandidateQuestions(String candidateQuestions) { this.candidateQuestions = candidateQuestions; }

    public String getInterviewerNotes() { return interviewerNotes; }
    public void setInterviewerNotes(String interviewerNotes) { this.interviewerNotes = interviewerNotes; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
