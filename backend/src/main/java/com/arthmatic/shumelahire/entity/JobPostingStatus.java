package com.arthmatic.shumelahire.entity;

public enum JobPostingStatus {
    DRAFT("Draft", "Job posting is being created/edited"),
    PENDING_APPROVAL("Pending Approval", "Job posting submitted for approval"),
    APPROVED("Approved", "Job posting approved and ready for publication"),
    PUBLISHED("Published", "Job posting is live and accepting applications"),
    UNPUBLISHED("Unpublished", "Job posting was published but is now temporarily unavailable"),
    REJECTED("Rejected", "Job posting approval was rejected"),
    CLOSED("Closed", "Job posting is closed and no longer accepting applications"),
    CANCELLED("Cancelled", "Job posting was cancelled before completion");
    
    private final String displayName;
    private final String description;
    
    JobPostingStatus(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }
    
    public String getDisplayName() {
        return displayName;
    }
    
    public String getDescription() {
        return description;
    }
    
    // Helper methods for status transitions
    public boolean canTransitionTo(JobPostingStatus newStatus) {
        switch (this) {
            case DRAFT:
                return newStatus == PENDING_APPROVAL || newStatus == CANCELLED;
            case PENDING_APPROVAL:
                return newStatus == APPROVED || newStatus == REJECTED;
            case APPROVED:
                return newStatus == PUBLISHED || newStatus == CANCELLED;
            case PUBLISHED:
                return newStatus == UNPUBLISHED || newStatus == CLOSED;
            case UNPUBLISHED:
                return newStatus == PUBLISHED || newStatus == CLOSED;
            case REJECTED:
                return newStatus == DRAFT || newStatus == CANCELLED;
            case CLOSED:
            case CANCELLED:
                return false; // Terminal states
            default:
                return false;
        }
    }
    
    public boolean isTerminal() {
        return this == CLOSED || this == CANCELLED;
    }
    
    public boolean isActive() {
        return this == PUBLISHED;
    }
    
    public boolean requiresApproval() {
        return this == PENDING_APPROVAL;
    }
    
    // Get CSS class for status styling
    public String getCssClass() {
        switch (this) {
            case DRAFT:
                return "bg-gray-100 text-gray-800";
            case PENDING_APPROVAL:
                return "bg-yellow-100 text-yellow-800";
            case APPROVED:
                return "bg-blue-100 text-blue-800";
            case PUBLISHED:
                return "bg-green-100 text-green-800";
            case UNPUBLISHED:
                return "bg-orange-100 text-orange-800";
            case REJECTED:
                return "bg-red-100 text-red-800";
            case CLOSED:
                return "bg-purple-100 text-purple-800";
            case CANCELLED:
                return "bg-red-100 text-red-800";
            default:
                return "bg-gray-100 text-gray-800";
        }
    }
    
    // Get icon for status
    public String getIcon() {
        switch (this) {
            case DRAFT:
                return "📝";
            case PENDING_APPROVAL:
                return "⏳";
            case APPROVED:
                return "✅";
            case PUBLISHED:
                return "🌐";
            case UNPUBLISHED:
                return "⏸️";
            case REJECTED:
                return "❌";
            case CLOSED:
                return "🔒";
            case CANCELLED:
                return "🚫";
            default:
                return "📄";
        }
    }
}