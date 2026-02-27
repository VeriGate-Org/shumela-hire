package com.arthmatic.shumelahire.entity.attendance;

public enum GeofenceType {
    RADIUS("Radius"),
    POLYGON("Polygon");

    private final String displayName;

    GeofenceType(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }
}
