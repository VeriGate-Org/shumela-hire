package com.arthmatic.shumelahire.dto;

import com.arthmatic.shumelahire.entity.LeaveDelegation;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class LeaveDelegationResponse {

    private Long id;
    private Long delegatorId;
    private String delegatorName;
    private Long delegateId;
    private String delegateName;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private boolean canApproveLeave;
    private boolean canApproveEncashment;
    private boolean active;
    private LocalDateTime createdAt;

    public LeaveDelegationResponse(LeaveDelegation delegation) {
        this.id = delegation.getId();
        this.delegatorId = delegation.getDelegator().getId();
        this.delegatorName = delegation.getDelegator().getFullName();
        this.delegateId = delegation.getDelegate().getId();
        this.delegateName = delegation.getDelegate().getFullName();
        this.startDate = delegation.getStartDate();
        this.endDate = delegation.getEndDate();
        this.reason = delegation.getReason();
        this.canApproveLeave = delegation.isCanApproveLeave();
        this.canApproveEncashment = delegation.isCanApproveEncashment();
        this.active = delegation.isActive();
        this.createdAt = delegation.getCreatedAt();
    }

    public static LeaveDelegationResponse fromEntity(LeaveDelegation delegation) {
        return new LeaveDelegationResponse(delegation);
    }

    // Getters
    public Long getId() { return id; }
    public Long getDelegatorId() { return delegatorId; }
    public String getDelegatorName() { return delegatorName; }
    public Long getDelegateId() { return delegateId; }
    public String getDelegateName() { return delegateName; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public String getReason() { return reason; }
    public boolean isCanApproveLeave() { return canApproveLeave; }
    public boolean isCanApproveEncashment() { return canApproveEncashment; }
    public boolean isActive() { return active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
