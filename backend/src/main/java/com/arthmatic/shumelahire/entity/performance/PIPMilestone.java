package com.arthmatic.shumelahire.entity.performance;

import com.arthmatic.shumelahire.entity.TenantAwareEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "pip_milestones")
public class PIPMilestone extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pip_id", nullable = false)
    @NotNull(message = "PIP reference is required")
    private PerformanceImprovementPlan pip;

    @NotBlank(message = "Milestone title is required")
    @Column(nullable = false, length = 200)
    private String title;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "success_criteria", columnDefinition = "TEXT")
    private String successCriteria;

    @NotNull(message = "Target date is required")
    @Column(name = "target_date", nullable = false)
    private LocalDate targetDate;

    @Column(name = "completed_date")
    private LocalDate completedDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PIPMilestoneStatus status = PIPMilestoneStatus.PENDING;

    @Column(name = "manager_notes", columnDefinition = "TEXT")
    private String managerNotes;

    @Column(name = "employee_notes", columnDefinition = "TEXT")
    private String employeeNotes;

    @Column(name = "sort_order")
    private Integer sortOrder;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public PIPMilestone() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Business methods
    public boolean isOverdue() {
        return status != PIPMilestoneStatus.COMPLETED
                && LocalDate.now().isAfter(targetDate);
    }

    public void complete(String notes) {
        this.status = PIPMilestoneStatus.COMPLETED;
        this.completedDate = LocalDate.now();
        this.managerNotes = notes;
    }

    public void markMissed(String notes) {
        this.status = PIPMilestoneStatus.MISSED;
        this.managerNotes = notes;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PerformanceImprovementPlan getPip() { return pip; }
    public void setPip(PerformanceImprovementPlan pip) { this.pip = pip; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSuccessCriteria() { return successCriteria; }
    public void setSuccessCriteria(String successCriteria) { this.successCriteria = successCriteria; }

    public LocalDate getTargetDate() { return targetDate; }
    public void setTargetDate(LocalDate targetDate) { this.targetDate = targetDate; }

    public LocalDate getCompletedDate() { return completedDate; }
    public void setCompletedDate(LocalDate completedDate) { this.completedDate = completedDate; }

    public PIPMilestoneStatus getStatus() { return status; }
    public void setStatus(PIPMilestoneStatus status) { this.status = status; }

    public String getManagerNotes() { return managerNotes; }
    public void setManagerNotes(String managerNotes) { this.managerNotes = managerNotes; }

    public String getEmployeeNotes() { return employeeNotes; }
    public void setEmployeeNotes(String employeeNotes) { this.employeeNotes = employeeNotes; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
