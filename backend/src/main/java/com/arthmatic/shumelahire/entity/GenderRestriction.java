package com.arthmatic.shumelahire.entity;

public enum GenderRestriction {
    MALE("Male"),
    FEMALE("Female"),
    ALL("All");

    private final String displayName;

    GenderRestriction(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
