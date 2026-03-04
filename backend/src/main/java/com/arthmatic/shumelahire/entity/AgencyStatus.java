package com.arthmatic.shumelahire.entity;

public enum AgencyStatus {
    PENDING_APPROVAL("Pending Approval"),
    APPROVED("Approved"),
    SUSPENDED("Suspended"),
    TERMINATED("Terminated");

    private final String displayName;

    AgencyStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() { return displayName; }

    public boolean isActive() { return this == APPROVED; }

    public boolean canTransitionTo(AgencyStatus target) {
        return switch (this) {
            case PENDING_APPROVAL -> target == APPROVED || target == TERMINATED;
            case APPROVED -> target == SUSPENDED || target == TERMINATED;
            case SUSPENDED -> target == APPROVED || target == TERMINATED;
            case TERMINATED -> false;
        };
    }

    public String getCssClass() {
        return switch (this) {
            case PENDING_APPROVAL -> "bg-yellow-100 text-yellow-800 border-yellow-200";
            case APPROVED -> "bg-green-100 text-green-800 border-green-200";
            case SUSPENDED -> "bg-orange-100 text-orange-800 border-orange-200";
            case TERMINATED -> "bg-red-100 text-red-800 border-red-200";
        };
    }
}
