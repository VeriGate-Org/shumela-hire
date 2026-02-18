package com.arthmatic.shumelahire.entity;

public enum TrendDirection {
    UP("Up", "Increasing trend", "📈", "text-green-600"),
    DOWN("Down", "Decreasing trend", "📉", "text-red-600"),
    STABLE("Stable", "No significant change", "➡️", "text-gray-600"),
    VOLATILE("Volatile", "High variability", "📊", "text-yellow-600"),
    UNKNOWN("Unknown", "Trend not determined", "❓", "text-gray-400");

    private final String displayName;
    private final String description;
    private final String icon;
    private final String cssClass;

    TrendDirection(String displayName, String description, String icon, String cssClass) {
        this.displayName = displayName;
        this.description = description;
        this.icon = icon;
        this.cssClass = cssClass;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }

    public String getCssClass() {
        return cssClass;
    }

    public static TrendDirection fromVariance(double variancePercentage) {
        if (Math.abs(variancePercentage) < 2.0) {
            return STABLE;
        } else if (variancePercentage > 5.0) {
            return UP;
        } else if (variancePercentage < -5.0) {
            return DOWN;
        } else {
            return VOLATILE;
        }
    }

    public boolean isPositive() {
        return this == UP;
    }

    public boolean isNegative() {
        return this == DOWN;
    }

    public boolean isNeutral() {
        return this == STABLE || this == UNKNOWN;
    }
}