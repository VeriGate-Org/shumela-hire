package com.arthmatic.shumelahire.entity.performance;

import com.arthmatic.shumelahire.entity.TenantAwareEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "performance_improvement_plans")
public class PerformanceImprovementPlan extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contract_id", nullable = false)
    @NotNull(message = "Performance contract is required")
    private PerformanceContract contract;

    @Column(name = "employee_id", nullable = false, length = 50)
    @NotBlank(message = "Employee ID is required")
    private String employeeId;

    @Column(name = "employee_name", nullable = false, length = 100)
    private String employeeName;

    @Column(name = "manager_id", nullable = false, length = 50)
    @NotBlank(message = "Manager ID is required")
    private String managerId;

    @Column(name = "manager_name", nullable = false, length = 100)
    private String managerName;

    @NotBlank(message = "PIP reason is required")
    @Column(nullable = false, columnDefinition = "TEXT")
    private String reason;

    @Column(name = "performance_gaps", columnDefinition = "TEXT")
    private String performanceGaps;

    @Column(name = "expected_improvements", columnDefinition = "TEXT")
    private String expectedImprovements;

    @Column(name = "support_provided", columnDefinition = "TEXT")
    private String supportProvided;

    @NotNull(message = "Start date is required")
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(name = "original_end_date")
    private LocalDate originalEndDate;

    @Column(name = "extension_reason", columnDefinition = "TEXT")
    private String extensionReason;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PIPStatus status = PIPStatus.DRAFT;

    @Column(name = "outcome_notes", columnDefinition = "TEXT")
    private String outcomeNotes;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Column(name = "completed_by", length = 50)
    private String completedBy;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by", nullable = false, length = 50)
    private String createdBy;

    @OneToMany(mappedBy = "pip", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PIPMilestone> milestones;

    public PerformanceImprovementPlan() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Business methods
    public boolean canBeActivated() {
        return status == PIPStatus.DRAFT;
    }

    public boolean canBeExtended() {
        return status == PIPStatus.ACTIVE;
    }

    public boolean canBeCompleted() {
        return status == PIPStatus.ACTIVE || status == PIPStatus.EXTENDED;
    }

    public boolean canBeTerminated() {
        return status == PIPStatus.ACTIVE || status == PIPStatus.EXTENDED;
    }

    public boolean isOverdue() {
        return (status == PIPStatus.ACTIVE || status == PIPStatus.EXTENDED)
                && LocalDate.now().isAfter(endDate);
    }

    public void activate() {
        if (!canBeActivated()) {
            throw new IllegalStateException("PIP cannot be activated in current state: " + status);
        }
        this.status = PIPStatus.ACTIVE;
    }

    public void extend(LocalDate newEndDate, String reason) {
        if (!canBeExtended()) {
            throw new IllegalStateException("PIP cannot be extended in current state: " + status);
        }
        if (this.originalEndDate == null) {
            this.originalEndDate = this.endDate;
        }
        this.endDate = newEndDate;
        this.extensionReason = reason;
        this.status = PIPStatus.EXTENDED;
    }

    public void completeSuccessfully(String notes, String completerId) {
        if (!canBeCompleted()) {
            throw new IllegalStateException("PIP cannot be completed in current state: " + status);
        }
        this.status = PIPStatus.COMPLETED_SUCCESSFULLY;
        this.outcomeNotes = notes;
        this.completedAt = LocalDateTime.now();
        this.completedBy = completerId;
    }

    public void completeUnsuccessfully(String notes, String completerId) {
        if (!canBeCompleted()) {
            throw new IllegalStateException("PIP cannot be completed in current state: " + status);
        }
        this.status = PIPStatus.COMPLETED_UNSUCCESSFULLY;
        this.outcomeNotes = notes;
        this.completedAt = LocalDateTime.now();
        this.completedBy = completerId;
    }

    public void terminate(String notes, String completerId) {
        if (!canBeTerminated()) {
            throw new IllegalStateException("PIP cannot be terminated in current state: " + status);
        }
        this.status = PIPStatus.TERMINATED;
        this.outcomeNotes = notes;
        this.completedAt = LocalDateTime.now();
        this.completedBy = completerId;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PerformanceContract getContract() { return contract; }
    public void setContract(PerformanceContract contract) { this.contract = contract; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getManagerId() { return managerId; }
    public void setManagerId(String managerId) { this.managerId = managerId; }

    public String getManagerName() { return managerName; }
    public void setManagerName(String managerName) { this.managerName = managerName; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getPerformanceGaps() { return performanceGaps; }
    public void setPerformanceGaps(String performanceGaps) { this.performanceGaps = performanceGaps; }

    public String getExpectedImprovements() { return expectedImprovements; }
    public void setExpectedImprovements(String expectedImprovements) { this.expectedImprovements = expectedImprovements; }

    public String getSupportProvided() { return supportProvided; }
    public void setSupportProvided(String supportProvided) { this.supportProvided = supportProvided; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public LocalDate getOriginalEndDate() { return originalEndDate; }
    public void setOriginalEndDate(LocalDate originalEndDate) { this.originalEndDate = originalEndDate; }

    public String getExtensionReason() { return extensionReason; }
    public void setExtensionReason(String extensionReason) { this.extensionReason = extensionReason; }

    public PIPStatus getStatus() { return status; }
    public void setStatus(PIPStatus status) { this.status = status; }

    public String getOutcomeNotes() { return outcomeNotes; }
    public void setOutcomeNotes(String outcomeNotes) { this.outcomeNotes = outcomeNotes; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public String getCompletedBy() { return completedBy; }
    public void setCompletedBy(String completedBy) { this.completedBy = completedBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public List<PIPMilestone> getMilestones() { return milestones; }
    public void setMilestones(List<PIPMilestone> milestones) { this.milestones = milestones; }
}
