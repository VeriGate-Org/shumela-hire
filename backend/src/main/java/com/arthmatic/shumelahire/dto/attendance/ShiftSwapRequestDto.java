package com.arthmatic.shumelahire.dto.attendance;

import jakarta.validation.constraints.NotNull;

public class ShiftSwapRequestDto {

    @NotNull(message = "Requester employee ID is required")
    private Long requesterEmployeeId;

    @NotNull(message = "Target employee ID is required")
    private Long targetEmployeeId;

    @NotNull(message = "Requester schedule ID is required")
    private Long requesterScheduleId;

    @NotNull(message = "Target schedule ID is required")
    private Long targetScheduleId;

    private String reason;

    // Getters and Setters
    public Long getRequesterEmployeeId() { return requesterEmployeeId; }
    public void setRequesterEmployeeId(Long requesterEmployeeId) { this.requesterEmployeeId = requesterEmployeeId; }

    public Long getTargetEmployeeId() { return targetEmployeeId; }
    public void setTargetEmployeeId(Long targetEmployeeId) { this.targetEmployeeId = targetEmployeeId; }

    public Long getRequesterScheduleId() { return requesterScheduleId; }
    public void setRequesterScheduleId(Long requesterScheduleId) { this.requesterScheduleId = requesterScheduleId; }

    public Long getTargetScheduleId() { return targetScheduleId; }
    public void setTargetScheduleId(Long targetScheduleId) { this.targetScheduleId = targetScheduleId; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
