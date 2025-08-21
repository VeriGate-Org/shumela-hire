package com.example.recruitment.entity;

public enum MessagePriority {
    LOW("Low", "Low priority message", "🔵", "bg-blue-100 text-blue-800"),
    NORMAL("Normal", "Normal priority message", "⚪", "bg-gray-100 text-gray-800"),
    HIGH("High", "High priority message", "🟡", "bg-yellow-100 text-yellow-800"),
    URGENT("Urgent", "Urgent message requiring immediate attention", "🔴", "bg-red-100 text-red-800");

    private final String displayName;
    private final String description;
    private final String icon;
    private final String cssClass;

    MessagePriority(String displayName, String description, String icon, String cssClass) {
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
            case NORMAL: return 2;
            case HIGH: return 3;
            case URGENT: return 4;
            default: return 2;
        }
    }

    public boolean requiresImmediateDelivery() {
        return this == URGENT;
    }

    public boolean requiresAcknowledgment() {
        return this == HIGH || this == URGENT;
    }

    public boolean canBeGrouped() {
        return this == LOW || this == NORMAL;
    }

    public long getDisplayOrderMinutes() {
        switch (this) {
            case URGENT: return 0;
            case HIGH: return 5;
            case NORMAL: return 30;
            case LOW: return 120;
            default: return 30;
        }
    }

    public static MessagePriority fromString(String priority) {
        try {
            return valueOf(priority.toUpperCase());
        } catch (IllegalArgumentException e) {
            return NORMAL;
        }
    }
}