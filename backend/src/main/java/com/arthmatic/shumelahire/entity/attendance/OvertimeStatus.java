package com.arthmatic.shumelahire.entity.attendance;

public enum OvertimeStatus {
    PENDING("Pending"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    PROCESSED("Processed");

    private final String displayName;

    OvertimeStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
