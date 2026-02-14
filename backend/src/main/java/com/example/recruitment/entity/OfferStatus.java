package com.example.recruitment.entity;

public enum OfferStatus {
    DRAFT("Draft", "Offer is being prepared"),
    PENDING_APPROVAL("Pending Approval", "Waiting for management approval"),
    APPROVED("Approved", "Offer approved and ready to send"),
    SENT("Sent", "Offer has been sent to candidate"),
    AWAITING_SIGNATURE("Awaiting Signature", "Offer sent for e-signature"),
    SIGNED("Signed", "Offer has been electronically signed"),
    UNDER_NEGOTIATION("Under Negotiation", "Candidate is negotiating terms"),
    ACCEPTED("Accepted", "Candidate has accepted the offer"),
    DECLINED("Declined", "Candidate has declined the offer"),
    WITHDRAWN("Withdrawn", "Offer has been withdrawn by company"),
    EXPIRED("Expired", "Offer has expired"),
    SUPERSEDED("Superseded", "Replaced by a newer offer version");

    private final String displayName;
    private final String description;

    OfferStatus(String displayName, String description) {
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
        return this == DRAFT || this == PENDING_APPROVAL || this == APPROVED ||
               this == SENT || this == AWAITING_SIGNATURE || this == UNDER_NEGOTIATION;
    }

    public boolean isTerminal() {
        return this == ACCEPTED || this == SIGNED || this == DECLINED || this == WITHDRAWN ||
               this == EXPIRED || this == SUPERSEDED;
    }

    public boolean isSuccessful() {
        return this == ACCEPTED || this == SIGNED;
    }

    public boolean requiresApproval() {
        return this == DRAFT;
    }

    public boolean canBeEdited() {
        return this == DRAFT || this == UNDER_NEGOTIATION;
    }

    public boolean canBeSent() {
        return this == APPROVED;
    }

    public boolean canBeWithdrawn() {
        return this == SENT || this == UNDER_NEGOTIATION;
    }

    public boolean canBeNegotiated() {
        return this == SENT;
    }

    public boolean canExpire() {
        return this == SENT || this == UNDER_NEGOTIATION;
    }

    public String getCssClass() {
        switch (this) {
            case DRAFT:
                return "bg-gray-100 text-gray-800 border-gray-200";
            case PENDING_APPROVAL:
                return "bg-yellow-100 text-yellow-800 border-yellow-200";
            case APPROVED:
                return "bg-blue-100 text-blue-800 border-blue-200";
            case SENT:
                return "bg-purple-100 text-purple-800 border-purple-200";
            case AWAITING_SIGNATURE:
                return "bg-indigo-100 text-indigo-800 border-indigo-200";
            case SIGNED:
                return "bg-emerald-100 text-emerald-800 border-emerald-200";
            case UNDER_NEGOTIATION:
                return "bg-orange-100 text-orange-800 border-orange-200";
            case ACCEPTED:
                return "bg-green-100 text-green-800 border-green-200";
            case DECLINED:
            case WITHDRAWN:
            case EXPIRED:
                return "bg-red-100 text-red-800 border-red-200";
            case SUPERSEDED:
                return "bg-gray-100 text-gray-600 border-gray-200";
            default:
                return "bg-gray-100 text-gray-800 border-gray-200";
        }
    }

    public String getStatusIcon() {
        switch (this) {
            case DRAFT: return "📝";
            case PENDING_APPROVAL: return "⏳";
            case APPROVED: return "✅";
            case SENT: return "📤";
            case AWAITING_SIGNATURE: return "✍️";
            case SIGNED: return "📝";
            case UNDER_NEGOTIATION: return "🤝";
            case ACCEPTED: return "🎉";
            case DECLINED: return "❌";
            case WITHDRAWN: return "↩️";
            case EXPIRED: return "⏰";
            case SUPERSEDED: return "🔄";
            default: return "📄";
        }
    }

    public boolean canTransitionTo(OfferStatus targetStatus) {
        switch (this) {
            case DRAFT:
                return targetStatus == PENDING_APPROVAL || targetStatus == WITHDRAWN;
            case PENDING_APPROVAL:
                return targetStatus == APPROVED || targetStatus == DRAFT || targetStatus == WITHDRAWN;
            case APPROVED:
                return targetStatus == SENT || targetStatus == WITHDRAWN;
            case SENT:
                return targetStatus == UNDER_NEGOTIATION || targetStatus == ACCEPTED ||
                       targetStatus == DECLINED || targetStatus == WITHDRAWN || targetStatus == EXPIRED ||
                       targetStatus == AWAITING_SIGNATURE;
            case AWAITING_SIGNATURE:
                return targetStatus == SIGNED || targetStatus == DECLINED ||
                       targetStatus == WITHDRAWN || targetStatus == EXPIRED;
            case UNDER_NEGOTIATION:
                return targetStatus == ACCEPTED || targetStatus == DECLINED || 
                       targetStatus == WITHDRAWN || targetStatus == EXPIRED || targetStatus == SUPERSEDED;
            case SIGNED:
            case ACCEPTED:
            case DECLINED:
            case WITHDRAWN:
            case EXPIRED:
            case SUPERSEDED:
                return false; // Terminal states
            default:
                return false;
        }
    }

    public static OfferStatus[] getActiveStatuses() {
        return java.util.Arrays.stream(values())
                .filter(OfferStatus::isActive)
                .toArray(OfferStatus[]::new);
    }

    public static OfferStatus[] getTerminalStatuses() {
        return java.util.Arrays.stream(values())
                .filter(OfferStatus::isTerminal)
                .toArray(OfferStatus[]::new);
    }
}