package com.arthmatic.shumelahire.entity;

public enum TransmissionStatus {

    PENDING("Pending", "Transmission queued, awaiting processing"),
    VALIDATING("Validating", "Employee data being validated against SAP schema"),
    TRANSMITTED("Transmitted", "Payload sent to SAP, awaiting confirmation"),
    CONFIRMED("Confirmed", "SAP confirmed employee creation with employee number"),
    FAILED("Failed", "Transmission failed — check error details"),
    RETRY_PENDING("Retry Pending", "Scheduled for automatic retry"),
    CANCELLED("Cancelled", "Transmission cancelled by user");

    private final String displayName;
    private final String description;

    TransmissionStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isTerminal() {
        return this == CONFIRMED || this == CANCELLED;
    }

    public boolean isRetryable() {
        return this == FAILED || this == RETRY_PENDING;
    }

    public boolean isActive() {
        return this == PENDING || this == VALIDATING || this == TRANSMITTED || this == RETRY_PENDING;
    }

    public boolean canBeCancelled() {
        return this == PENDING || this == FAILED || this == RETRY_PENDING;
    }
}
