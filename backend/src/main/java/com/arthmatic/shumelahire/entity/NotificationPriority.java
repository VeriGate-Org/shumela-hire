package com.arthmatic.shumelahire.entity;

public enum NotificationPriority {
    LOW("Low", "Low priority notification", "🔵", "bg-blue-100 text-blue-800"),
    MEDIUM("Medium", "Medium priority notification", "🟡", "bg-yellow-100 text-yellow-800"),
    HIGH("High", "High priority notification", "🟠", "bg-orange-100 text-orange-800"),
    URGENT("Urgent", "Urgent notification requiring immediate attention", "🔴", "bg-red-100 text-red-800");

    private final String displayName;
    private final String description;
    private final String icon;
    private final String cssClass;

    NotificationPriority(String displayName, String description, String icon, String cssClass) {
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

    public int getNumericValue() {
        switch (this) {
            case LOW: return 1;
            case MEDIUM: return 2;
            case HIGH: return 3;
            case URGENT: return 4;
            default: return 2;
        }
    }

    public boolean requiresImmediateDelivery() {
        return this == HIGH || this == URGENT;
    }

    public boolean canBeGrouped() {
        return this == LOW || this == MEDIUM;
    }

    public boolean canBeScheduled() {
        return this != URGENT;
    }

    public long getMaxDelayMinutes() {
        switch (this) {
            case URGENT: return 0;
            case HIGH: return 5;
            case MEDIUM: return 30;
            case LOW: return 120;
            default: return 30;
        }
    }

    public static NotificationPriority fromString(String priority) {
        try {
            return valueOf(priority.toUpperCase());
        } catch (IllegalArgumentException e) {
            return MEDIUM;
        }
    }

    public static NotificationPriority[] getImmediatePriorities() {
        return new NotificationPriority[]{HIGH, URGENT};
    }

    public static NotificationPriority[] getSchedulablePriorities() {
        return new NotificationPriority[]{LOW, MEDIUM, HIGH};
    }
}