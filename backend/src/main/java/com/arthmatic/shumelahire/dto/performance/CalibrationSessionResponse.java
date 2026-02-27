package com.arthmatic.shumelahire.dto.performance;

import com.arthmatic.shumelahire.entity.performance.CalibrationSession;

import java.time.LocalDateTime;

public class CalibrationSessionResponse {

    private Long id;
    private Long cycleId;
    private String name;
    private String description;
    private String department;
    private String jobLevel;
    private String facilitatorId;
    private String facilitatorName;
    private LocalDateTime scheduledDate;
    private LocalDateTime startedAt;
    private LocalDateTime completedAt;
    private String status;
    private String notes;
    private String distributionTarget;
    private LocalDateTime createdAt;
    private String createdBy;
    private int ratingCount;

    public static CalibrationSessionResponse fromEntity(CalibrationSession session) {
        CalibrationSessionResponse response = new CalibrationSessionResponse();
        response.setId(session.getId());
        response.setCycleId(session.getCycle() != null ? session.getCycle().getId() : null);
        response.setName(session.getName());
        response.setDescription(session.getDescription());
        response.setDepartment(session.getDepartment());
        response.setJobLevel(session.getJobLevel());
        response.setFacilitatorId(session.getFacilitatorId());
        response.setFacilitatorName(session.getFacilitatorName());
        response.setScheduledDate(session.getScheduledDate());
        response.setStartedAt(session.getStartedAt());
        response.setCompletedAt(session.getCompletedAt());
        response.setStatus(session.getStatus().name());
        response.setNotes(session.getNotes());
        response.setDistributionTarget(session.getDistributionTarget());
        response.setCreatedAt(session.getCreatedAt());
        response.setCreatedBy(session.getCreatedBy());
        response.setRatingCount(session.getRatings() != null ? session.getRatings().size() : 0);
        return response;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getCycleId() { return cycleId; }
    public void setCycleId(Long cycleId) { this.cycleId = cycleId; }
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
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
    public String getDistributionTarget() { return distributionTarget; }
    public void setDistributionTarget(String distributionTarget) { this.distributionTarget = distributionTarget; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public int getRatingCount() { return ratingCount; }
    public void setRatingCount(int ratingCount) { this.ratingCount = ratingCount; }
}
