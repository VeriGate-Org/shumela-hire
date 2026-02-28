package com.arthmatic.shumelahire.entity.attendance;

public enum AttendanceStatus {
    PRESENT("Present"),
    ABSENT("Absent"),
    LATE("Late"),
    HALF_DAY("Half Day"),
    ON_LEAVE("On Leave"),
    PUBLIC_HOLIDAY("Public Holiday");

    private final String displayName;

    AttendanceStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
