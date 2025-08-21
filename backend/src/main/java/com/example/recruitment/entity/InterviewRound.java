package com.example.recruitment.entity;

public enum InterviewRound {
    SCREENING("Phone Screening", 1),
    FIRST_ROUND("First Interview", 2),
    TECHNICAL("Technical Assessment", 3),
    SECOND_ROUND("Second Interview", 4),
    PANEL("Panel Interview", 5),
    MANAGER("Manager Interview", 6),
    FINAL("Final Interview", 7),
    OFFER("Offer Discussion", 8);

    private final String displayName;
    private final int order;

    InterviewRound(String displayName, int order) {
        this.displayName = displayName;
        this.order = order;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getOrder() {
        return order;
    }

    public boolean isInitialRound() {
        return this == SCREENING || this == FIRST_ROUND;
    }

    public boolean isFinalRound() {
        return this == FINAL || this == OFFER;
    }

    public boolean requiresTechnicalAssessment() {
        return this == TECHNICAL;
    }

    public InterviewRound getNextRound() {
        switch (this) {
            case SCREENING: return FIRST_ROUND;
            case FIRST_ROUND: return TECHNICAL;
            case TECHNICAL: return SECOND_ROUND;
            case SECOND_ROUND: return PANEL;
            case PANEL: return MANAGER;
            case MANAGER: return FINAL;
            case FINAL: return OFFER;
            default: return null;
        }
    }

    public static InterviewRound[] getOrderedRounds() {
        return new InterviewRound[]{SCREENING, FIRST_ROUND, TECHNICAL, SECOND_ROUND, PANEL, MANAGER, FINAL, OFFER};
    }
}