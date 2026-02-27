package com.arthmatic.shumelahire.dto.attendance;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class ShiftSwapRequestDto {

    @NotNull
    private Long requesterScheduleId;

    @NotNull
    private Long targetEmployeeId;

    private Long targetScheduleId;

    @NotNull
    private LocalDate swapDate;

    private LocalDate targetDate;
    private String reason;

    public Long getRequesterScheduleId() { return requesterScheduleId; }
    public void setRequesterScheduleId(Long requesterScheduleId) { this.requesterScheduleId = requesterScheduleId; }

    public Long getTargetEmployeeId() { return targetEmployeeId; }
    public void setTargetEmployeeId(Long targetEmployeeId) { this.targetEmployeeId = targetEmployeeId; }

    public Long getTargetScheduleId() { return targetScheduleId; }
    public void setTargetScheduleId(Long targetScheduleId) { this.targetScheduleId = targetScheduleId; }

    public LocalDate getSwapDate() { return swapDate; }
    public void setSwapDate(LocalDate swapDate) { this.swapDate = swapDate; }

    public LocalDate getTargetDate() { return targetDate; }
    public void setTargetDate(LocalDate targetDate) { this.targetDate = targetDate; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }
}
