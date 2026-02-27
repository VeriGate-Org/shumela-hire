package com.arthmatic.shumelahire.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Represents a background/verification check initiated for a candidate
 * through an external verification provider (e.g. Dots Africa).
 */
@Entity
@Table(name = "background_checks", indexes = {
        @Index(name = "idx_bgcheck_tenant", columnList = "tenant_id"),
        @Index(name = "idx_bgcheck_application", columnList = "application_id"),
        @Index(name = "idx_bgcheck_reference", columnList = "reference_id"),
        @Index(name = "idx_bgcheck_status", columnList = "status"),
})
public class BackgroundCheck extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "application_id", nullable = false)
    @NotNull(message = "Application is required")
    private Application application;

    @Column(name = "reference_id", unique = true, length = 100)
    private String referenceId;

    @NotBlank(message = "Candidate ID number is required")
    @Column(name = "candidate_id_number", nullable = false, length = 20)
    private String candidateIdNumber;

    @Column(name = "candidate_name", length = 200)
    private String candidateName;

    @Column(name = "candidate_email", length = 200)
    private String candidateEmail;

    @Column(name = "check_types", length = 10000)
    private String checkTypes; // JSON array of check type strings

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private BackgroundCheckStatus status = BackgroundCheckStatus.INITIATED;

    @Enumerated(EnumType.STRING)
    @Column(name = "overall_result", length = 30)
    private BackgroundCheckResult overallResult;

    @Column(name = "results_json", length = 10000)
    private String resultsJson; // JSON object with per-check-type results

    @Column(name = "consent_obtained", nullable = false)
    private Boolean consentObtained = false;

    @Column(name = "consent_obtained_at")
    private LocalDateTime consentObtainedAt;

    @Column(name = "initiated_by", nullable = false)
    @NotNull(message = "Initiator is required")
    private Long initiatedBy; // User ID who initiated the check

    @Column(name = "provider", length = 50)
    private String provider; // e.g. "dots-africa"

    @Column(name = "external_screening_id", length = 200)
    private String externalScreeningId; // Provider's internal screening reference

    @Column(name = "report_url", length = 500)
    private String reportUrl;

    @Column(name = "error_message", length = 10000)
    private String errorMessage;

    @Column(name = "notes", length = 10000)
    private String notes;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "submitted_at")
    private LocalDateTime submittedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    // Constructors
    public BackgroundCheck() {
        this.createdAt = LocalDateTime.now();
    }

    // Lifecycle callbacks
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Business methods
    public boolean canBeCancelled() {
        return status == BackgroundCheckStatus.INITIATED
                || status == BackgroundCheckStatus.PENDING_CONSENT
                || status == BackgroundCheckStatus.IN_PROGRESS;
    }

    public boolean isComplete() {
        return status == BackgroundCheckStatus.COMPLETED;
    }

    public boolean hasFailed() {
        return status == BackgroundCheckStatus.FAILED;
    }

    public boolean hasAdverseFindings() {
        return overallResult == BackgroundCheckResult.ADVERSE;
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Application getApplication() {
        return application;
    }

    public void setApplication(Application application) {
        this.application = application;
    }

    public String getReferenceId() {
        return referenceId;
    }

    public void setReferenceId(String referenceId) {
        this.referenceId = referenceId;
    }

    public String getCandidateIdNumber() {
        return candidateIdNumber;
    }

    public void setCandidateIdNumber(String candidateIdNumber) {
        this.candidateIdNumber = candidateIdNumber;
    }

    public String getCandidateName() {
        return candidateName;
    }

    public void setCandidateName(String candidateName) {
        this.candidateName = candidateName;
    }

    public String getCandidateEmail() {
        return candidateEmail;
    }

    public void setCandidateEmail(String candidateEmail) {
        this.candidateEmail = candidateEmail;
    }

    public String getCheckTypes() {
        return checkTypes;
    }

    public void setCheckTypes(String checkTypes) {
        this.checkTypes = checkTypes;
    }

    public BackgroundCheckStatus getStatus() {
        return status;
    }

    public void setStatus(BackgroundCheckStatus status) {
        this.status = status;
    }

    public BackgroundCheckResult getOverallResult() {
        return overallResult;
    }

    public void setOverallResult(BackgroundCheckResult overallResult) {
        this.overallResult = overallResult;
    }

    public String getResultsJson() {
        return resultsJson;
    }

    public void setResultsJson(String resultsJson) {
        this.resultsJson = resultsJson;
    }

    public Boolean getConsentObtained() {
        return consentObtained;
    }

    public void setConsentObtained(Boolean consentObtained) {
        this.consentObtained = consentObtained;
    }

    public LocalDateTime getConsentObtainedAt() {
        return consentObtainedAt;
    }

    public void setConsentObtainedAt(LocalDateTime consentObtainedAt) {
        this.consentObtainedAt = consentObtainedAt;
    }

    public Long getInitiatedBy() {
        return initiatedBy;
    }

    public void setInitiatedBy(Long initiatedBy) {
        this.initiatedBy = initiatedBy;
    }

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getExternalScreeningId() {
        return externalScreeningId;
    }

    public void setExternalScreeningId(String externalScreeningId) {
        this.externalScreeningId = externalScreeningId;
    }

    public String getReportUrl() {
        return reportUrl;
    }

    public void setReportUrl(String reportUrl) {
        this.reportUrl = reportUrl;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
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

    public LocalDateTime getSubmittedAt() {
        return submittedAt;
    }

    public void setSubmittedAt(LocalDateTime submittedAt) {
        this.submittedAt = submittedAt;
    }

    public LocalDateTime getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(LocalDateTime completedAt) {
        this.completedAt = completedAt;
    }

    public LocalDateTime getCancelledAt() {
        return cancelledAt;
    }

    public void setCancelledAt(LocalDateTime cancelledAt) {
        this.cancelledAt = cancelledAt;
    }
}
