package com.arthmatic.shumelahire.entity;

public enum NegotiationStatus {
    NOT_STARTED("Not Started", "No negotiation has begun"),
    IN_PROGRESS("In Progress", "Actively negotiating terms"),
    CANDIDATE_RESPONSE_PENDING("Candidate Response Pending", "Waiting for candidate response"),
    COMPANY_RESPONSE_PENDING("Company Response Pending", "Waiting for company response"),
    STALLED("Stalled", "Negotiation has stalled"),
    ESCALATED("Escalated", "Escalated to senior management"),
    FINAL_OFFER("Final Offer", "Company has made final offer"),
    AGREED("Agreed", "Terms have been agreed upon"),
    FAILED("Failed", "Negotiation has failed");

    private final String displayName;
    private final String description;

    NegotiationStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public boolean isActive() {
        return this == IN_PROGRESS || this == CANDIDATE_RESPONSE_PENDING || 
               this == COMPANY_RESPONSE_PENDING || this == ESCALATED;
    }

    public boolean isPending() {
        return this == CANDIDATE_RESPONSE_PENDING || this == COMPANY_RESPONSE_PENDING;
    }

    public boolean isCompleted() {
        return this == AGREED || this == FAILED;
    }

    public boolean requiresAction() {
        return this == COMPANY_RESPONSE_PENDING || this == STALLED || this == ESCALATED;
    }

    public String getCssClass() {
        switch (this) {
            case NOT_STARTED:
                return "bg-gray-100 text-gray-800";
            case IN_PROGRESS:
            case ESCALATED:
                return "bg-blue-100 text-blue-800";
            case CANDIDATE_RESPONSE_PENDING:
                return "bg-yellow-100 text-yellow-800";
            case COMPANY_RESPONSE_PENDING:
                return "bg-orange-100 text-orange-800";
            case STALLED:
                return "bg-red-100 text-red-800";
            case FINAL_OFFER:
                return "bg-purple-100 text-purple-800";
            case AGREED:
                return "bg-green-100 text-green-800";
            case FAILED:
                return "bg-red-100 text-red-800";
            default:
                return "bg-gray-100 text-gray-800";
        }
    }

    public String getIcon() {
        switch (this) {
            case NOT_STARTED: return "⏸️";
            case IN_PROGRESS: return "🤝";
            case CANDIDATE_RESPONSE_PENDING: return "⏳";
            case COMPANY_RESPONSE_PENDING: return "📤";
            case STALLED: return "⚠️";
            case ESCALATED: return "⬆️";
            case FINAL_OFFER: return "🎯";
            case AGREED: return "✅";
            case FAILED: return "❌";
            default: return "📄";
        }
    }
}