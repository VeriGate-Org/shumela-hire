package com.arthmatic.shumelahire.entity;

public enum HalfDayPeriod {
    MORNING("Morning"),
    AFTERNOON("Afternoon");

    private final String displayName;

    HalfDayPeriod(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
