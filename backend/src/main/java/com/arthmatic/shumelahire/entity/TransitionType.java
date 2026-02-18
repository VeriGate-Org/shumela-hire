package com.arthmatic.shumelahire.entity;

public enum TransitionType {
    PROGRESSION("Progression", "Moving forward in the pipeline"),
    REGRESSION("Regression", "Moving backward for re-evaluation"),
    REJECTION("Rejection", "Candidate rejected at this stage"),
    WITHDRAWAL("Withdrawal", "Candidate withdrew from process"),
    REACTIVATION("Reactivation", "Candidate reactivated after withdrawal"),
    HOLD("On Hold", "Process paused temporarily"),
    SKIP("Skip Stage", "Stage skipped due to qualifications"),
    RESTART("Restart", "Process restarted from earlier stage");

    private final String displayName;
    private final String description;

    TransitionType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isPositive() {
        return this == PROGRESSION || this == REACTIVATION || this == SKIP;
    }

    public boolean isNegative() {
        return this == REJECTION || this == WITHDRAWAL;
    }

    public boolean isNeutral() {
        return this == REGRESSION || this == HOLD || this == RESTART;
    }

    public String getCssClass() {
        if (isPositive()) {
            return "bg-green-100 text-green-800";
        } else if (isNegative()) {
            return "bg-red-100 text-red-800";
        } else {
            return "bg-yellow-100 text-yellow-800";
        }
    }

    public String getIcon() {
        switch (this) {
            case PROGRESSION: return "➡️";
            case REGRESSION: return "⬅️";
            case REJECTION: return "❌";
            case WITHDRAWAL: return "↩️";
            case REACTIVATION: return "🔄";
            case HOLD: return "⏸️";
            case SKIP: return "⏭️";
            case RESTART: return "🔄";
            default: return "📝";
        }
    }

    public boolean requiresReason() {
        return this == REJECTION || this == WITHDRAWAL || this == REGRESSION || this == HOLD;
    }

    public boolean allowsNotes() {
        return true; // All transitions can have notes
    }

    public boolean canBeAutomated() {
        return this == PROGRESSION || this == SKIP;
    }
}