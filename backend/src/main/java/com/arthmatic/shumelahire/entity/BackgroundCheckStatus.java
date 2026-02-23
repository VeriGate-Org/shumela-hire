package com.arthmatic.shumelahire.entity;

/**
 * Status lifecycle for background/verification checks.
 */
public enum BackgroundCheckStatus {

    INITIATED("Initiated", "Check has been initiated and sent to the verification provider"),
    PENDING_CONSENT("Pending Consent", "Awaiting candidate consent before processing"),
    IN_PROGRESS("In Progress", "Verification checks are currently being processed"),
    PARTIAL_RESULTS("Partial Results", "Some check results are available, others still pending"),
    COMPLETED("Completed", "All verification checks have been completed"),
    FAILED("Failed", "Verification check failed due to a technical or processing error"),
    CANCELLED("Cancelled", "Verification check was cancelled before completion");

    private final String displayName;
    private final String description;

    BackgroundCheckStatus(String displayName, String description) {
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
        return this == COMPLETED || this == FAILED || this == CANCELLED;
    }

    public boolean isActive() {
        return this == INITIATED || this == PENDING_CONSENT || this == IN_PROGRESS || this == PARTIAL_RESULTS;
    }
}
