package com.arthmatic.shumelahire.entity.attendance;

public enum OvertimeType {
    WEEKDAY("Weekday"),
    WEEKEND("Weekend"),
    PUBLIC_HOLIDAY("Public Holiday"),
    NIGHT("Night");

    private final String displayName;

    OvertimeType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
