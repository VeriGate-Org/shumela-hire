package com.arthmatic.shumelahire.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "shortlist_scores")
public class ShortlistScore extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    private Application application;

    @Column(name = "total_score", nullable = false)
    private Double totalScore;

    @Column(name = "skills_match_score")
    private Double skillsMatchScore;

    @Column(name = "experience_score")
    private Double experienceScore;

    @Column(name = "education_score")
    private Double educationScore;

    @Column(name = "screening_score")
    private Double screeningScore;

    @Column(name = "keyword_match_score")
    private Double keywordMatchScore;

    @Column(name = "score_breakdown", columnDefinition = "TEXT")
    private String scoreBreakdown;

    @Column(name = "is_shortlisted")
    private Boolean isShortlisted = false;

    @Column(name = "manually_overridden")
    private Boolean manuallyOverridden = false;

    @Column(name = "override_reason")
    private String overrideReason;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public ShortlistScore() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Application getApplication() { return application; }
    public void setApplication(Application application) { this.application = application; }

    public Double getTotalScore() { return totalScore; }
    public void setTotalScore(Double totalScore) { this.totalScore = totalScore; }

    public Double getSkillsMatchScore() { return skillsMatchScore; }
    public void setSkillsMatchScore(Double skillsMatchScore) { this.skillsMatchScore = skillsMatchScore; }

    public Double getExperienceScore() { return experienceScore; }
    public void setExperienceScore(Double experienceScore) { this.experienceScore = experienceScore; }

    public Double getEducationScore() { return educationScore; }
    public void setEducationScore(Double educationScore) { this.educationScore = educationScore; }

    public Double getScreeningScore() { return screeningScore; }
    public void setScreeningScore(Double screeningScore) { this.screeningScore = screeningScore; }

    public Double getKeywordMatchScore() { return keywordMatchScore; }
    public void setKeywordMatchScore(Double keywordMatchScore) { this.keywordMatchScore = keywordMatchScore; }

    public String getScoreBreakdown() { return scoreBreakdown; }
    public void setScoreBreakdown(String scoreBreakdown) { this.scoreBreakdown = scoreBreakdown; }

    public Boolean getIsShortlisted() { return isShortlisted; }
    public void setIsShortlisted(Boolean isShortlisted) { this.isShortlisted = isShortlisted; }

    public Boolean getManuallyOverridden() { return manuallyOverridden; }
    public void setManuallyOverridden(Boolean manuallyOverridden) { this.manuallyOverridden = manuallyOverridden; }

    public String getOverrideReason() { return overrideReason; }
    public void setOverrideReason(String overrideReason) { this.overrideReason = overrideReason; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
