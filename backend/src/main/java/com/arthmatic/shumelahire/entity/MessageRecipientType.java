package com.arthmatic.shumelahire.entity;

public enum MessageRecipientType {
    DIRECT("Direct", "Direct message to specific users"),
    DEPARTMENT("Department", "Message to entire department"),
    ROLE("Role", "Message to users with specific role"),
    GROUP("Group", "Message to predefined group"),
    ALL_USERS("All Users", "Message to all system users"),
    BROADCAST("Broadcast", "System-wide broadcast message");

    private final String displayName;
    private final String description;

    MessageRecipientType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public boolean requiresSpecificRecipients() {
        return this == DIRECT || this == GROUP;
    }

    public boolean isSystemWide() {
        return this == ALL_USERS || this == BROADCAST;
    }

    public boolean requiresRoleFilter() {
        return this == ROLE;
    }

    public boolean requiresDepartmentFilter() {
        return this == DEPARTMENT;
    }
}