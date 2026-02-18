package com.arthmatic.shumelahire.entity;

public enum ApplicationStatus {
    SUBMITTED("Submitted", "Application has been submitted and is pending review"),
    SCREENING("Screening", "Application is being reviewed by HR/Recruiter"),
    INTERVIEW_SCHEDULED("Interview Scheduled", "Interview has been scheduled with the candidate"),
    INTERVIEW_COMPLETED("Interview Completed", "Interview has been completed, awaiting decision"),
    REFERENCE_CHECK("Reference Check", "Checking candidate references"),
    OFFER_PENDING("Offer Pending", "Offer is being prepared"),
    OFFERED("Offered", "Job offer has been extended to candidate"),
    OFFER_ACCEPTED("Offer Accepted", "Candidate has accepted the job offer"),
    OFFER_DECLINED("Offer Declined", "Candidate has declined the job offer"),
    REJECTED("Rejected", "Application has been rejected"),
    WITHDRAWN("Withdrawn", "Application has been withdrawn by candidate"),
    HIRED("Hired", "Candidate has been successfully hired and onboarded");
    
    private final String displayName;
    private final String description;
    
    ApplicationStatus(String displayName, String description) {
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
    public boolean canTransitionTo(ApplicationStatus newStatus) {
        switch (this) {
            case SUBMITTED:
                return newStatus == SCREENING || newStatus == REJECTED || newStatus == WITHDRAWN;
            case SCREENING:
                return newStatus == INTERVIEW_SCHEDULED || newStatus == REJECTED || newStatus == WITHDRAWN;
            case INTERVIEW_SCHEDULED:
                return newStatus == INTERVIEW_COMPLETED || newStatus == REJECTED || newStatus == WITHDRAWN;
            case INTERVIEW_COMPLETED:
                return newStatus == REFERENCE_CHECK || newStatus == OFFER_PENDING || newStatus == REJECTED || newStatus == WITHDRAWN;
            case REFERENCE_CHECK:
                return newStatus == OFFER_PENDING || newStatus == REJECTED || newStatus == WITHDRAWN;
            case OFFER_PENDING:
                return newStatus == OFFERED || newStatus == REJECTED || newStatus == WITHDRAWN;
            case OFFERED:
                return newStatus == OFFER_ACCEPTED || newStatus == OFFER_DECLINED || newStatus == WITHDRAWN;
            case OFFER_ACCEPTED:
                return newStatus == HIRED;
            case OFFER_DECLINED:
            case REJECTED:
            case WITHDRAWN:
            case HIRED:
                return false; // Terminal states
            default:
                return false;
        }
    }
    
    public boolean isTerminal() {
        return this == OFFER_DECLINED || this == REJECTED || this == WITHDRAWN || this == HIRED;
    }
    
    public boolean isActive() {
        return !isTerminal();
    }
    
    // Get CSS class for status styling
    public String getCssClass() {
        switch (this) {
            case SUBMITTED:
                return "bg-blue-100 text-blue-800";
            case SCREENING:
                return "bg-yellow-100 text-yellow-800";
            case INTERVIEW_SCHEDULED:
            case INTERVIEW_COMPLETED:
                return "bg-purple-100 text-purple-800";
            case REFERENCE_CHECK:
                return "bg-indigo-100 text-indigo-800";
            case OFFER_PENDING:
            case OFFERED:
                return "bg-green-100 text-green-800";
            case OFFER_ACCEPTED:
            case HIRED:
                return "bg-emerald-100 text-emerald-800";
            case OFFER_DECLINED:
                return "bg-orange-100 text-orange-800";
            case REJECTED:
            case WITHDRAWN:
                return "bg-red-100 text-red-800";
            default:
                return "bg-gray-100 text-gray-800";
        }
    }
}