package com.arthmatic.shumelahire.entity.performance;

import com.arthmatic.shumelahire.entity.TenantAwareEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "calibration_sessions")
public class CalibrationSession extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cycle_id", nullable = false)
    @NotNull(message = "Performance cycle is required")
    private PerformanceCycle cycle;

    @NotBlank(message = "Session name is required")
    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(length = 100)
    private String department;

    @Column(name = "job_level", length = 50)
    private String jobLevel;

    @Column(name = "facilitator_id", nullable = false, length = 50)
    @NotBlank(message = "Facilitator ID is required")
    private String facilitatorId;

    @Column(name = "facilitator_name", nullable = false, length = 100)
    private String facilitatorName;

    @Column(name = "scheduled_date")
    private LocalDateTime scheduledDate;

    @Column(name = "started_at")
    private LocalDateTime startedAt;

    @Column(name = "completed_at")
    private LocalDateTime completedAt;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private CalibrationStatus status = CalibrationStatus.SCHEDULED;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "distribution_target", columnDefinition = "TEXT")
    private String distributionTarget;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by", nullable = false, length = 50)
    private String createdBy;

    @OneToMany(mappedBy = "session", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<CalibrationRating> ratings;

    public CalibrationSession() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Business methods
    public boolean canBeStarted() {
        return status == CalibrationStatus.SCHEDULED;
    }

    public boolean canBeCompleted() {
        return status == CalibrationStatus.IN_PROGRESS;
    }

    public boolean canBeCancelled() {
        return status == CalibrationStatus.SCHEDULED || status == CalibrationStatus.IN_PROGRESS;
    }

    public void start() {
        if (!canBeStarted()) {
            throw new IllegalStateException("Calibration session cannot be started in current state: " + status);
        }
        this.status = CalibrationStatus.IN_PROGRESS;
        this.startedAt = LocalDateTime.now();
    }

    public void complete(String notes) {
        if (!canBeCompleted()) {
            throw new IllegalStateException("Calibration session cannot be completed in current state: " + status);
        }
        this.status = CalibrationStatus.COMPLETED;
        this.completedAt = LocalDateTime.now();
        this.notes = notes;
    }

    public void cancel(String reason) {
        if (!canBeCancelled()) {
            throw new IllegalStateException("Calibration session cannot be cancelled in current state: " + status);
        }
        this.status = CalibrationStatus.CANCELLED;
        this.notes = reason;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public PerformanceCycle getCycle() { return cycle; }
    public void setCycle(PerformanceCycle cycle) { this.cycle = cycle; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getJobLevel() { return jobLevel; }
    public void setJobLevel(String jobLevel) { this.jobLevel = jobLevel; }

    public String getFacilitatorId() { return facilitatorId; }
    public void setFacilitatorId(String facilitatorId) { this.facilitatorId = facilitatorId; }

    public String getFacilitatorName() { return facilitatorName; }
    public void setFacilitatorName(String facilitatorName) { this.facilitatorName = facilitatorName; }

    public LocalDateTime getScheduledDate() { return scheduledDate; }
    public void setScheduledDate(LocalDateTime scheduledDate) { this.scheduledDate = scheduledDate; }

    public LocalDateTime getStartedAt() { return startedAt; }
    public void setStartedAt(LocalDateTime startedAt) { this.startedAt = startedAt; }

    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }

    public CalibrationStatus getStatus() { return status; }
    public void setStatus(CalibrationStatus status) { this.status = status; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getDistributionTarget() { return distributionTarget; }
    public void setDistributionTarget(String distributionTarget) { this.distributionTarget = distributionTarget; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }

    public List<CalibrationRating> getRatings() { return ratings; }
    public void setRatings(List<CalibrationRating> ratings) { this.ratings = ratings; }
}
