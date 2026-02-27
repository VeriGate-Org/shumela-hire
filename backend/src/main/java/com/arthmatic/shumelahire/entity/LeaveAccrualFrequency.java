package com.arthmatic.shumelahire.entity;

public enum LeaveAccrualFrequency {
    MONTHLY("Monthly"),
    QUARTERLY("Quarterly"),
    SEMI_ANNUALLY("Semi-Annually"),
    ANNUALLY("Annually"),
    ON_HIRE_DATE("On Hire Date");

    private final String displayName;

    LeaveAccrualFrequency(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
