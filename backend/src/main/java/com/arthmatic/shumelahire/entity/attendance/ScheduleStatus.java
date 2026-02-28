package com.arthmatic.shumelahire.entity.attendance;

public enum ScheduleStatus {
    SCHEDULED("Scheduled"),
    SWAPPED("Swapped"),
    CANCELLED("Cancelled");

    private final String displayName;

    ScheduleStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
