package com.arthmatic.shumelahire.entity;

public enum InterviewRecommendation {
    HIRE("Recommend for Hire"),
    STRONG_HIRE("Strongly Recommend for Hire"),
    CONSIDER("Consider with Reservations"),
    REJECT("Do Not Recommend"),
    STRONG_REJECT("Strongly Do Not Recommend"),
    ANOTHER_ROUND("Recommend Another Round"),
    ON_HOLD("Put on Hold"),
    SECOND_OPINION("Needs Second Opinion");

    private final String displayName;

    InterviewRecommendation(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    public boolean isPositive() {
        return this == HIRE || this == STRONG_HIRE || this == CONSIDER;
    }

    public boolean isNegative() {
        return this == REJECT || this == STRONG_REJECT;
    }

    public boolean requiresAction() {
        return this == ANOTHER_ROUND || this == SECOND_OPINION;
    }

    public boolean allowsProgressToNextRound() {
        return this == HIRE || this == STRONG_HIRE || this == ANOTHER_ROUND;
    }

    public String getActionSuggestion() {
        switch (this) {
            case HIRE:
            case STRONG_HIRE:
                return "Proceed with job offer";
            case CONSIDER:
                return "Review with hiring team before proceeding";
            case REJECT:
            case STRONG_REJECT:
                return "Send rejection communication";
            case ANOTHER_ROUND:
                return "Schedule additional interview round";
            case ON_HOLD:
                return "Keep candidate active but delay decision";
            case SECOND_OPINION:
                return "Schedule interview with additional team member";
            default:
                return "Review recommendation with hiring team";
        }
    }
}