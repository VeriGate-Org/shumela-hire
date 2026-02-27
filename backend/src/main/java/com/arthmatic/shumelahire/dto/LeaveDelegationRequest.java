package com.arthmatic.shumelahire.dto;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class LeaveDelegationRequest {

    @NotNull(message = "Delegate ID is required")
    private Long delegateId;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    private String reason;
    private boolean canApproveLeave = true;
    private boolean canApproveEncashment;

    // Getters and Setters
    public Long getDelegateId() { return delegateId; }
    public void setDelegateId(Long delegateId) { this.delegateId = delegateId; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public boolean isCanApproveLeave() { return canApproveLeave; }
    public void setCanApproveLeave(boolean canApproveLeave) { this.canApproveLeave = canApproveLeave; }

    public boolean isCanApproveEncashment() { return canApproveEncashment; }
    public void setCanApproveEncashment(boolean canApproveEncashment) { this.canApproveEncashment = canApproveEncashment; }
}
