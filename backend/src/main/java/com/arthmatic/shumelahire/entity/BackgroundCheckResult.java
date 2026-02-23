package com.arthmatic.shumelahire.entity;

/**
 * Overall result classification for a completed background check.
 */
public enum BackgroundCheckResult {

    CLEAR("Clear", "All checks passed with no adverse findings", "emerald"),
    ADVERSE("Adverse", "One or more checks returned adverse findings", "red"),
    PENDING_REVIEW("Pending Review", "Results require manual review before a determination", "amber"),
    INCONCLUSIVE("Inconclusive", "Unable to determine a definitive result", "gray");

    private final String displayName;
    private final String description;
    private final String colorClass;

    BackgroundCheckResult(String displayName, String description, String colorClass) {
        this.displayName = displayName;
        this.description = description;
        this.colorClass = colorClass;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public String getColorClass() {
        return colorClass;
    }
}
