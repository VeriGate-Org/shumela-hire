package com.arthmatic.shumelahire.entity.engagement;

import com.arthmatic.shumelahire.entity.TenantAwareEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "surveys")
public class Survey extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(name = "title", nullable = false, length = 255)
    private String title;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Enumerated(EnumType.STRING)
    @Column(name = "survey_type", nullable = false, length = 30)
    @NotNull
    private SurveyType surveyType = SurveyType.PULSE;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private SurveyStatus status = SurveyStatus.DRAFT;

    @NotBlank
    @Column(name = "created_by", nullable = false, length = 255)
    private String createdBy;

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate;

    @Column(name = "is_anonymous", nullable = false)
    private Boolean isAnonymous = true;

    @OneToMany(mappedBy = "survey", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<SurveyQuestion> questions = new ArrayList<>();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public Survey() {}

    public void activate() {
        if (status != SurveyStatus.DRAFT) {
            throw new IllegalStateException("Survey can only be activated from DRAFT status");
        }
        this.status = SurveyStatus.ACTIVE;
        if (this.startDate == null) {
            this.startDate = LocalDateTime.now();
        }
    }

    public void close() {
        if (status != SurveyStatus.ACTIVE) {
            throw new IllegalStateException("Survey can only be closed from ACTIVE status");
        }
        this.status = SurveyStatus.CLOSED;
        this.endDate = LocalDateTime.now();
    }

    public boolean isAcceptingResponses() {
        return status == SurveyStatus.ACTIVE;
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

    public List<SurveyQuestion> getQuestions() { return questions; }
    public void setQuestions(List<SurveyQuestion> questions) { this.questions = questions; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
