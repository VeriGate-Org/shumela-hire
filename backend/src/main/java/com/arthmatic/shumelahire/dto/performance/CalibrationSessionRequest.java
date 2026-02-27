package com.arthmatic.shumelahire.dto.performance;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

public class CalibrationSessionRequest {

    @NotNull(message = "Cycle ID is required")
    private Long cycleId;

    @NotBlank(message = "Session name is required")
    private String name;

    private String description;
    private String department;
    private String jobLevel;

    @NotBlank(message = "Facilitator ID is required")
    private String facilitatorId;

    private String facilitatorName;

    private LocalDateTime scheduledDate;

    private String distributionTarget;

    // Getters and Setters
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

    public String getDistributionTarget() { return distributionTarget; }
    public void setDistributionTarget(String distributionTarget) { this.distributionTarget = distributionTarget; }
}
