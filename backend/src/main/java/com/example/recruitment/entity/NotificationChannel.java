package com.example.recruitment.entity;

public enum NotificationChannel {
    IN_APP("In-App", "In-application notification", "🔔"),
    EMAIL("Email", "Email notification", "📧"),
    SMS("SMS", "Text message notification", "📱"),
    PUSH("Push", "Push notification", "📲"),
    SLACK("Slack", "Slack message", "💬"),
    WEBHOOK("Webhook", "Webhook notification", "🔗"),
    BROWSER("Browser", "Browser notification", "🌐");

    private final String displayName;
    private final String description;
    private final String icon;

    NotificationChannel(String displayName, String description, String icon) {
        this.displayName = displayName;
        this.description = description;
        this.icon = icon;
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

    public boolean requiresExternalService() {
        return this == EMAIL || this == SMS || this == SLACK || this == WEBHOOK;
    }

    public boolean isRealTime() {
        return this == IN_APP || this == PUSH || this == BROWSER;
    }

    public boolean supportsRichContent() {
        return this == IN_APP || this == EMAIL || this == SLACK;
    }

    public boolean requiresDeviceToken() {
        return this == PUSH;
    }

    public boolean requiresPhoneNumber() {
        return this == SMS;
    }

    public boolean requiresEmailAddress() {
        return this == EMAIL;
    }

    public int getDeliveryPriority() {
        switch (this) {
            case IN_APP: return 1;
            case PUSH: return 2;
            case EMAIL: return 3;
            case SMS: return 4;
            case SLACK: return 5;
            case WEBHOOK: return 6;
            case BROWSER: return 7;
            default: return 99;
        }
    }
}