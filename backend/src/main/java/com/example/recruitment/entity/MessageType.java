package com.example.recruitment.entity;

public enum MessageType {
    DIRECT_MESSAGE("Direct Message", "One-to-one message", "💬", "bg-blue-100 text-blue-800"),
    GROUP_MESSAGE("Group Message", "Message to multiple recipients", "👥", "bg-purple-100 text-purple-800"),
    BROADCAST("Broadcast", "System-wide announcement", "📢", "bg-green-100 text-green-800"),
    NOTIFICATION("Notification", "System notification message", "🔔", "bg-yellow-100 text-yellow-800"),
    ANNOUNCEMENT("Announcement", "Official announcement", "📣", "bg-orange-100 text-orange-800"),
    ALERT("Alert", "Important alert message", "⚠️", "bg-red-100 text-red-800"),
    REMINDER("Reminder", "Reminder message", "⏰", "bg-gray-100 text-gray-800"),
    FEEDBACK_REQUEST("Feedback Request", "Request for feedback", "💭", "bg-indigo-100 text-indigo-800"),
    STATUS_UPDATE("Status Update", "Status update message", "📊", "bg-teal-100 text-teal-800"),
    DOCUMENT_SHARE("Document Share", "Document sharing message", "📎", "bg-green-100 text-green-800");

    private final String displayName;
    private final String description;
    private final String icon;
    private final String cssClass;

    MessageType(String displayName, String description, String icon, String cssClass) {
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

    public boolean isSystemGenerated() {
        return this == NOTIFICATION || this == BROADCAST || this == ALERT || this == REMINDER;
    }

    public boolean requiresMultipleRecipients() {
        return this == GROUP_MESSAGE || this == BROADCAST || this == ANNOUNCEMENT;
    }

    public boolean canBeScheduled() {
        return this != ALERT && this != NOTIFICATION;
    }

    public boolean requiresAcknowledgment() {
        return this == ALERT || this == ANNOUNCEMENT || this == FEEDBACK_REQUEST;
    }

    public boolean canHaveAttachments() {
        return this == DIRECT_MESSAGE || this == GROUP_MESSAGE || this == DOCUMENT_SHARE;
    }

    public static MessageType[] getUserGeneratedTypes() {
        return new MessageType[]{
            DIRECT_MESSAGE, GROUP_MESSAGE, ANNOUNCEMENT, 
            FEEDBACK_REQUEST, STATUS_UPDATE, DOCUMENT_SHARE
        };
    }

    public static MessageType[] getSystemTypes() {
        return new MessageType[]{
            NOTIFICATION, BROADCAST, ALERT, REMINDER
        };
    }
}