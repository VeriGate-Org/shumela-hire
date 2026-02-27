package com.arthmatic.shumelahire.entity;

public enum LeaveRequestStatus {
    PENDING("Pending"),
    MANAGER_APPROVED("Manager Approved"),
    HR_APPROVED("HR Approved"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    CANCELLED("Cancelled"),
    RECALLED("Recalled");

    private final String displayName;

    LeaveRequestStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
