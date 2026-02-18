package com.arthmatic.shumelahire.entity;

public enum PipelineStage {
    APPLICATION_RECEIVED("Application Received", 1, true, false),
    INITIAL_SCREENING("Initial Screening", 2, true, false),
    PHONE_SCREENING("Phone Screening", 3, true, false),
    FIRST_INTERVIEW("First Interview", 4, true, false),
    TECHNICAL_ASSESSMENT("Technical Assessment", 5, true, false),
    SECOND_INTERVIEW("Second Interview", 6, true, false),
    PANEL_INTERVIEW("Panel Interview", 7, true, false),
    MANAGER_INTERVIEW("Manager Interview", 8, true, false),
    FINAL_INTERVIEW("Final Interview", 9, true, false),
    REFERENCE_CHECK("Reference Check", 10, true, false),
    BACKGROUND_CHECK("Background Check", 11, true, false),
    OFFER_PREPARATION("Offer Preparation", 12, true, false),
    OFFER_EXTENDED("Offer Extended", 13, true, false),
    OFFER_NEGOTIATION("Offer Negotiation", 14, true, false),
    OFFER_ACCEPTED("Offer Accepted", 15, false, true),
    HIRED("Hired", 16, false, true),
    
    // Terminal stages
    WITHDRAWN("Withdrawn", 90, false, true),
    REJECTED("Rejected", 91, false, true),
    OFFER_DECLINED("Offer Declined", 92, false, true),
    NO_SHOW("No Show", 93, false, true),
    DUPLICATE("Duplicate Application", 94, false, true);

    private final String displayName;
    private final int order;
    private final boolean isActive;
    private final boolean isTerminal;

    PipelineStage(String displayName, int order, boolean isActive, boolean isTerminal) {
        this.displayName = displayName;
        this.order = order;
        this.isActive = isActive;
        this.isTerminal = isTerminal;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getOrder() {
        return order;
    }

    public boolean isActive() {
        return isActive;
    }

    public boolean isTerminal() {
        return isTerminal;
    }

    public boolean isSuccessful() {
        return this == OFFER_ACCEPTED || this == HIRED;
    }

    public boolean isRejected() {
        return this == REJECTED || this == OFFER_DECLINED || this == NO_SHOW || this == DUPLICATE;
    }

    public boolean isWithdrawn() {
        return this == WITHDRAWN;
    }

    public boolean canProgressTo(PipelineStage nextStage) {
        // Cannot move backwards (except for special cases)
        if (nextStage.order < this.order && !isSpecialTransition(this, nextStage)) {
            return false;
        }
        
        // Cannot move from terminal stages
        if (this.isTerminal) {
            return false;
        }
        
        // Can always move to terminal stages (except from other terminals)
        if (nextStage.isTerminal) {
            return true;
        }
        
        // Normal progression rules
        return nextStage.order <= this.order + 3; // Allow skipping up to 2 stages
    }

    private boolean isSpecialTransition(PipelineStage from, PipelineStage to) {
        // Allow moving back from certain stages for re-evaluation
        if (from == REFERENCE_CHECK || from == BACKGROUND_CHECK) {
            return to.order >= PHONE_SCREENING.order && to.order < from.order;
        }
        
        // Allow moving from offer stages back to final interview
        if (from == OFFER_PREPARATION || from == OFFER_EXTENDED || from == OFFER_NEGOTIATION) {
            return to == FINAL_INTERVIEW || to == MANAGER_INTERVIEW;
        }
        
        return false;
    }

    public PipelineStage getNextStage() {
        switch (this) {
            case APPLICATION_RECEIVED: return INITIAL_SCREENING;
            case INITIAL_SCREENING: return PHONE_SCREENING;
            case PHONE_SCREENING: return FIRST_INTERVIEW;
            case FIRST_INTERVIEW: return TECHNICAL_ASSESSMENT;
            case TECHNICAL_ASSESSMENT: return SECOND_INTERVIEW;
            case SECOND_INTERVIEW: return PANEL_INTERVIEW;
            case PANEL_INTERVIEW: return MANAGER_INTERVIEW;
            case MANAGER_INTERVIEW: return FINAL_INTERVIEW;
            case FINAL_INTERVIEW: return REFERENCE_CHECK;
            case REFERENCE_CHECK: return BACKGROUND_CHECK;
            case BACKGROUND_CHECK: return OFFER_PREPARATION;
            case OFFER_PREPARATION: return OFFER_EXTENDED;
            case OFFER_EXTENDED: return OFFER_NEGOTIATION;
            case OFFER_NEGOTIATION: return OFFER_ACCEPTED;
            case OFFER_ACCEPTED: return HIRED;
            default: return null;
        }
    }

    public PipelineStage getPreviousStage() {
        switch (this) {
            case INITIAL_SCREENING: return APPLICATION_RECEIVED;
            case PHONE_SCREENING: return INITIAL_SCREENING;
            case FIRST_INTERVIEW: return PHONE_SCREENING;
            case TECHNICAL_ASSESSMENT: return FIRST_INTERVIEW;
            case SECOND_INTERVIEW: return TECHNICAL_ASSESSMENT;
            case PANEL_INTERVIEW: return SECOND_INTERVIEW;
            case MANAGER_INTERVIEW: return PANEL_INTERVIEW;
            case FINAL_INTERVIEW: return MANAGER_INTERVIEW;
            case REFERENCE_CHECK: return FINAL_INTERVIEW;
            case BACKGROUND_CHECK: return REFERENCE_CHECK;
            case OFFER_PREPARATION: return BACKGROUND_CHECK;
            case OFFER_EXTENDED: return OFFER_PREPARATION;
            case OFFER_NEGOTIATION: return OFFER_EXTENDED;
            case OFFER_ACCEPTED: return OFFER_NEGOTIATION;
            case HIRED: return OFFER_ACCEPTED;
            default: return null;
        }
    }

    public static PipelineStage[] getActiveStages() {
        return java.util.Arrays.stream(values())
                .filter(PipelineStage::isActive)
                .toArray(PipelineStage[]::new);
    }

    public static PipelineStage[] getTerminalStages() {
        return java.util.Arrays.stream(values())
                .filter(PipelineStage::isTerminal)
                .toArray(PipelineStage[]::new);
    }

    public static PipelineStage[] getOrderedStages() {
        return java.util.Arrays.stream(values())
                .sorted((a, b) -> Integer.compare(a.order, b.order))
                .toArray(PipelineStage[]::new);
    }

    public String getCssClass() {
        if (isSuccessful()) {
            return "bg-green-100 text-green-800 border-green-200";
        } else if (isRejected()) {
            return "bg-red-100 text-red-800 border-red-200";
        } else if (isWithdrawn()) {
            return "bg-gray-100 text-gray-800 border-gray-200";
        } else if (isTerminal()) {
            return "bg-purple-100 text-purple-800 border-purple-200";
        } else {
            return "bg-blue-100 text-blue-800 border-blue-200";
        }
    }

    public String getStatusIcon() {
        if (isSuccessful()) {
            return "✅";
        } else if (isRejected()) {
            return "❌";
        } else if (isWithdrawn()) {
            return "↩️";
        } else if (isTerminal()) {
            return "⏹️";
        } else {
            return "⏳";
        }
    }

    public double getProgressPercentage() {
        if (isTerminal()) {
            return isSuccessful() ? 100.0 : 0.0;
        }
        
        // Calculate progress based on order (excluding terminal stages)
        int maxActiveOrder = java.util.Arrays.stream(getActiveStages())
                .mapToInt(PipelineStage::getOrder)
                .max()
                .orElse(16);
        
        return (double) this.order / maxActiveOrder * 100.0;
    }
}