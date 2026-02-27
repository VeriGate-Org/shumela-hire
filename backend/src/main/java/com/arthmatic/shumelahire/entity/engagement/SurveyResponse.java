package com.arthmatic.shumelahire.entity.engagement;

import com.arthmatic.shumelahire.entity.Employee;
import com.arthmatic.shumelahire.entity.TenantAwareEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "survey_responses")
public class SurveyResponse extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "survey_id", nullable = false)
    @NotNull
    private Survey survey;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "question_id", nullable = false)
    @NotNull
    private SurveyQuestion question;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "respondent_id")
    private Employee respondent;

    @Column(name = "anonymous_token", length = 255)
    private String anonymousToken;

    @Min(1) @Max(10)
    @Column(name = "rating_value")
    private Integer ratingValue;

    @Column(name = "text_value", columnDefinition = "TEXT")
    private String textValue;

    @Column(name = "selected_option", length = 255)
    private String selectedOption;

    @CreationTimestamp
    @Column(name = "submitted_at", nullable = false)
    private LocalDateTime submittedAt;

    public SurveyResponse() {}

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Survey getSurvey() { return survey; }
    public void setSurvey(Survey survey) { this.survey = survey; }

    public SurveyQuestion getQuestion() { return question; }
    public void setQuestion(SurveyQuestion question) { this.question = question; }

    public Employee getRespondent() { return respondent; }
    public void setRespondent(Employee respondent) { this.respondent = respondent; }

    public String getAnonymousToken() { return anonymousToken; }
    public void setAnonymousToken(String anonymousToken) { this.anonymousToken = anonymousToken; }

    public Integer getRatingValue() { return ratingValue; }
    public void setRatingValue(Integer ratingValue) { this.ratingValue = ratingValue; }

    public String getTextValue() { return textValue; }
    public void setTextValue(String textValue) { this.textValue = textValue; }

    public String getSelectedOption() { return selectedOption; }
    public void setSelectedOption(String selectedOption) { this.selectedOption = selectedOption; }

    public LocalDateTime getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(LocalDateTime submittedAt) { this.submittedAt = submittedAt; }
}
