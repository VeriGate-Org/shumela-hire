package com.example.recruitment.entity;

public enum AgencySubmissionStatus {
    SUBMITTED("Submitted"),
    UNDER_REVIEW("Under Review"),
    ACCEPTED("Accepted"),
    REJECTED("Rejected"),
    WITHDRAWN("Withdrawn");

    private final String displayName;

    AgencySubmissionStatus(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() { return displayName; }
}
