package com.example.recruitment.entity;

public enum NotificationType {
    // Application notifications
    APPLICATION_SUBMITTED("Application Submitted", "New application received", "📝", "APPLICATION", "bg-blue-100 text-blue-800"),
    APPLICATION_VIEWED("Application Viewed", "Your application has been viewed", "👀", "APPLICATION", "bg-purple-100 text-purple-800"),
    APPLICATION_APPROVED("Application Approved", "Application has been approved", "✅", "APPLICATION", "bg-green-100 text-green-800"),
    APPLICATION_REJECTED("Application Rejected", "Application has been rejected", "❌", "APPLICATION", "bg-red-100 text-red-800"),
    APPLICATION_WITHDRAWN("Application Withdrawn", "Application has been withdrawn", "↩️", "APPLICATION", "bg-gray-100 text-gray-800"),
    
    // Interview notifications
    INTERVIEW_SCHEDULED("Interview Scheduled", "Interview has been scheduled", "📅", "INTERVIEW", "bg-blue-100 text-blue-800"),
    INTERVIEW_RESCHEDULED("Interview Rescheduled", "Interview has been rescheduled", "🔄", "INTERVIEW", "bg-yellow-100 text-yellow-800"),
    INTERVIEW_CANCELLED("Interview Cancelled", "Interview has been cancelled", "❌", "INTERVIEW", "bg-red-100 text-red-800"),
    INTERVIEW_REMINDER("Interview Reminder", "Upcoming interview reminder", "⏰", "INTERVIEW", "bg-orange-100 text-orange-800"),
    INTERVIEW_COMPLETED("Interview Completed", "Interview has been completed", "✅", "INTERVIEW", "bg-green-100 text-green-800"),
    INTERVIEW_FEEDBACK_REQUESTED("Feedback Requested", "Interview feedback requested", "💭", "INTERVIEW", "bg-purple-100 text-purple-800"),
    
    // Offer notifications
    OFFER_EXTENDED("Offer Extended", "Job offer has been extended", "💰", "OFFER", "bg-green-100 text-green-800"),
    OFFER_ACCEPTED("Offer Accepted", "Offer has been accepted", "🎉", "OFFER", "bg-emerald-100 text-emerald-800"),
    OFFER_DECLINED("Offer Declined", "Offer has been declined", "❌", "OFFER", "bg-red-100 text-red-800"),
    OFFER_NEGOTIATION("Offer Negotiation", "Offer is under negotiation", "🤝", "OFFER", "bg-blue-100 text-blue-800"),
    OFFER_EXPIRED("Offer Expired", "Offer has expired", "⏰", "OFFER", "bg-gray-100 text-gray-800"),
    OFFER_WITHDRAWN("Offer Withdrawn", "Offer has been withdrawn", "↩️", "OFFER", "bg-gray-100 text-gray-800"),
    
    // Job posting notifications
    JOB_PUBLISHED("Job Published", "Job posting has been published", "📢", "JOB_POSTING", "bg-green-100 text-green-800"),
    JOB_UPDATED("Job Updated", "Job posting has been updated", "✏️", "JOB_POSTING", "bg-blue-100 text-blue-800"),
    JOB_CLOSED("Job Closed", "Job posting has been closed", "🚫", "JOB_POSTING", "bg-red-100 text-red-800"),
    JOB_EXPIRED("Job Expired", "Job posting has expired", "⏰", "JOB_POSTING", "bg-gray-100 text-gray-800"),
    
    // System notifications
    SYSTEM_MAINTENANCE("System Maintenance", "Scheduled system maintenance", "🔧", "SYSTEM", "bg-yellow-100 text-yellow-800"),
    SYSTEM_UPDATE("System Update", "System has been updated", "🆙", "SYSTEM", "bg-blue-100 text-blue-800"),
    SYSTEM_ALERT("System Alert", "System alert notification", "⚠️", "SYSTEM", "bg-red-100 text-red-800"),
    
    // Pipeline notifications
    PIPELINE_STAGE_CHANGED("Stage Changed", "Application stage has changed", "🔄", "PIPELINE", "bg-purple-100 text-purple-800"),
    PIPELINE_STALLED("Pipeline Stalled", "Application pipeline has stalled", "⏸️", "PIPELINE", "bg-yellow-100 text-yellow-800"),
    
    // Communication notifications
    MESSAGE_RECEIVED("Message Received", "New message received", "💬", "MESSAGE", "bg-blue-100 text-blue-800"),
    DOCUMENT_SHARED("Document Shared", "Document has been shared", "📎", "DOCUMENT", "bg-green-100 text-green-800"),
    
    // Task and reminder notifications
    TASK_ASSIGNED("Task Assigned", "New task has been assigned", "📋", "TASK", "bg-blue-100 text-blue-800"),
    TASK_DUE("Task Due", "Task is due soon", "⏰", "TASK", "bg-orange-100 text-orange-800"),
    REMINDER("Reminder", "General reminder notification", "🔔", "REMINDER", "bg-yellow-100 text-yellow-800"),
    
    // Analytics and reporting
    REPORT_READY("Report Ready", "Report has been generated", "📊", "REPORT", "bg-green-100 text-green-800"),
    ANALYTICS_ALERT("Analytics Alert", "Analytics threshold alert", "📈", "ANALYTICS", "bg-red-100 text-red-800"),
    
    // Approval workflow
    APPROVAL_REQUIRED("Approval Required", "Approval is required", "✋", "APPROVAL", "bg-yellow-100 text-yellow-800"),
    APPROVAL_GRANTED("Approval Granted", "Approval has been granted", "✅", "APPROVAL", "bg-green-100 text-green-800"),
    APPROVAL_DENIED("Approval Denied", "Approval has been denied", "❌", "APPROVAL", "bg-red-100 text-red-800");

    private final String displayName;
    private final String description;
    private final String icon;
    private final String category;
    private final String cssClass;

    NotificationType(String displayName, String description, String icon, String category, String cssClass) {
        this.displayName = displayName;
        this.description = description;
        this.icon = icon;
        this.category = category;
        this.cssClass = cssClass;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    public String getIcon() {
        return icon;
    }

    public String getCategory() {
        return category;
    }

    public String getCssClass() {
        return cssClass;
    }

    public boolean isApplicationRelated() {
        return category.equals("APPLICATION");
    }

    public boolean isInterviewRelated() {
        return category.equals("INTERVIEW");
    }

    public boolean isOfferRelated() {
        return category.equals("OFFER");
    }

    public boolean isJobPostingRelated() {
        return category.equals("JOB_POSTING");
    }

    public boolean isSystemRelated() {
        return category.equals("SYSTEM");
    }

    public boolean isUrgent() {
        return this == INTERVIEW_REMINDER || this == OFFER_EXPIRED || 
               this == SYSTEM_ALERT || this == TASK_DUE;
    }

    public boolean requiresAction() {
        return this == APPROVAL_REQUIRED || this == INTERVIEW_FEEDBACK_REQUESTED ||
               this == TASK_ASSIGNED || this == OFFER_NEGOTIATION;
    }

    public static NotificationType[] getByCategory(String category) {
        return java.util.Arrays.stream(values())
                .filter(type -> type.getCategory().equals(category))
                .toArray(NotificationType[]::new);
    }

    public static NotificationType[] getUrgentTypes() {
        return java.util.Arrays.stream(values())
                .filter(NotificationType::isUrgent)
                .toArray(NotificationType[]::new);
    }

    public static NotificationType[] getActionRequiredTypes() {
        return java.util.Arrays.stream(values())
                .filter(NotificationType::requiresAction)
                .toArray(NotificationType[]::new);
    }
}