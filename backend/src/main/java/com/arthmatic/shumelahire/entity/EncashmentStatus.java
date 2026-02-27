package com.arthmatic.shumelahire.entity;

public enum EncashmentStatus {
    PENDING("Pending"),
    APPROVED("Approved"),
    REJECTED("Rejected"),
    PAID("Paid"),
    CANCELLED("Cancelled");

    private final String displayName;

    EncashmentStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
