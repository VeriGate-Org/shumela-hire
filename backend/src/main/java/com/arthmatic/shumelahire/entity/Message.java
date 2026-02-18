package com.arthmatic.shumelahire.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "messages")
public class Message extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "sender_id", nullable = false)
    @NotNull(message = "Sender is required")
    private Long senderId;

    @Column(name = "sender_name")
    private String senderName;

    @Column(name = "sender_role")
    private String senderRole;

    @Column(name = "recipient_ids", columnDefinition = "TEXT")
    private String recipientIds; // JSON array of recipient IDs

    @Column(name = "recipient_type")
    @Enumerated(EnumType.STRING)
    private MessageRecipientType recipientType = MessageRecipientType.DIRECT;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_type", nullable = false)
    private MessageType messageType = MessageType.DIRECT_MESSAGE;

    @Column(name = "subject")
    private String subject;

    @Column(name = "content", columnDefinition = "TEXT", nullable = false)
    private String content;

    @Column(name = "message_format")
    @Enumerated(EnumType.STRING)
    private MessageFormat messageFormat = MessageFormat.TEXT;

    @Enumerated(EnumType.STRING)
    @Column(name = "priority", nullable = false)
    private MessagePriority priority = MessagePriority.NORMAL;

    // Thread and conversation management
    @Column(name = "thread_id")
    private String threadId;

    @Column(name = "parent_message_id")
    private Long parentMessageId;

    @Column(name = "conversation_id")
    private String conversationId;

    @Column(name = "is_thread_starter")
    private Boolean isThreadStarter = false;

    // Related entities
    @Column(name = "application_id")
    private Long applicationId;

    @Column(name = "interview_id")
    private Long interviewId;

    @Column(name = "job_posting_id")
    private Long jobPostingId;

    @Column(name = "offer_id")
    private Long offerId;

    // Status and tracking
    @Column(name = "is_read")
    private Boolean isRead = false;

    @Column(name = "read_by", columnDefinition = "TEXT")
    private String readBy; // JSON object with read status per recipient

    @Column(name = "is_delivered")
    private Boolean isDelivered = false;

    @Column(name = "delivered_at")
    private LocalDateTime deliveredAt;

    @Column(name = "is_archived")
    private Boolean isArchived = false;

    @Column(name = "archived_at")
    private LocalDateTime archivedAt;

    @Column(name = "is_deleted")
    private Boolean isDeleted = false;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by")
    private Long deletedBy;

    // Message features
    @Column(name = "has_attachments")
    private Boolean hasAttachments = false;

    @Column(name = "attachment_urls", columnDefinition = "TEXT")
    private String attachmentUrls; // JSON array of attachment URLs

    @Column(name = "is_urgent")
    private Boolean isUrgent = false;

    @Column(name = "requires_response")
    private Boolean requiresResponse = false;

    @Column(name = "response_deadline")
    private LocalDateTime responseDeadline;

    @Column(name = "is_confidential")
    private Boolean isConfidential = false;

    @Column(name = "auto_delete_at")
    private LocalDateTime autoDeleteAt;

    // Scheduling
    @Column(name = "scheduled_for")
    private LocalDateTime scheduledFor;

    @Column(name = "is_scheduled")
    private Boolean isScheduled = false;

    // Tags and categories
    @Column(name = "tags", columnDefinition = "TEXT")
    private String tags; // JSON array of tags

    @Column(name = "category")
    private String category;

    // Tracking metadata
    @Column(name = "metadata", columnDefinition = "TEXT")
    private String metadata; // JSON for additional data

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "sent_at")
    private LocalDateTime sentAt;

    // Constructors
    public Message() {
        this.createdAt = LocalDateTime.now();
        this.threadId = generateThreadId();
    }

    public Message(Long senderId, String content, MessageType messageType) {
        this();
        this.senderId = senderId;
        this.content = content;
        this.messageType = messageType;
    }

    public Message(Long senderId, String recipientIds, String subject, String content) {
        this(senderId, content, MessageType.DIRECT_MESSAGE);
        this.recipientIds = recipientIds;
        this.subject = subject;
    }

    // Lifecycle callbacks
    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Business methods
    public boolean canBeEdited() {
        return !isDelivered && !isDeleted && 
               (scheduledFor == null || LocalDateTime.now().isBefore(scheduledFor));
    }

    public boolean canBeDeleted() {
        return !isDeleted;
    }

    public boolean canBeScheduled() {
        return !isDelivered && !isDeleted;
    }

    public boolean isOverdue() {
        return requiresResponse && responseDeadline != null && 
               LocalDateTime.now().isAfter(responseDeadline);
    }

    public boolean shouldAutoDelete() {
        return autoDeleteAt != null && LocalDateTime.now().isAfter(autoDeleteAt);
    }

    public boolean canBeSent() {
        return !isDelivered && !isDeleted && 
               (scheduledFor == null || LocalDateTime.now().isAfter(scheduledFor));
    }

    public void markAsRead(Long userId) {
        this.isRead = true;
        // In a real implementation, update the readBy JSON with user-specific read status
    }

    public void markAsDelivered() {
        this.isDelivered = true;
        this.deliveredAt = LocalDateTime.now();
        if (sentAt == null) {
            this.sentAt = LocalDateTime.now();
        }
    }

    public void archive() {
        this.isArchived = true;
        this.archivedAt = LocalDateTime.now();
    }

    public void delete(Long userId) {
        this.isDeleted = true;
        this.deletedAt = LocalDateTime.now();
        this.deletedBy = userId;
    }

    public long getMinutesSinceCreated() {
        return java.time.Duration.between(createdAt, LocalDateTime.now()).toMinutes();
    }

    public long getDaysSinceCreated() {
        return java.time.temporal.ChronoUnit.DAYS.between(createdAt.toLocalDate(), LocalDateTime.now().toLocalDate());
    }

    public String getDisplayTime() {
        long minutes = getMinutesSinceCreated();
        if (minutes < 1) return "Just now";
        if (minutes < 60) return minutes + "m ago";
        
        long hours = minutes / 60;
        if (hours < 24) return hours + "h ago";
        
        long days = getDaysSinceCreated();
        if (days < 7) return days + "d ago";
        
        return createdAt.toLocalDate().toString();
    }

    public String getTypeDisplayName() {
        return messageType.getDisplayName();
    }

    public String getTypeIcon() {
        return messageType.getIcon();
    }

    public String getTypeCssClass() {
        return messageType.getCssClass();
    }

    public String getPriorityDisplayName() {
        return priority.getDisplayName();
    }

    public String getPriorityIcon() {
        return priority.getIcon();
    }

    public String getPriorityCssClass() {
        return priority.getCssClass();
    }

    public String getPreview(int length) {
        if (content == null) return "";
        if (content.length() <= length) return content;
        return content.substring(0, length) + "...";
    }

    public boolean hasRelatedEntity() {
        return applicationId != null || interviewId != null || 
               jobPostingId != null || offerId != null;
    }

    public String getRelatedEntityType() {
        if (applicationId != null) return "APPLICATION";
        if (interviewId != null) return "INTERVIEW";
        if (jobPostingId != null) return "JOB_POSTING";
        if (offerId != null) return "OFFER";
        return null;
    }

    public Long getRelatedEntityId() {
        if (applicationId != null) return applicationId;
        if (interviewId != null) return interviewId;
        if (jobPostingId != null) return jobPostingId;
        if (offerId != null) return offerId;
        return null;
    }

    private String generateThreadId() {
        return "thread_" + System.currentTimeMillis() + "_" + System.nanoTime();
    }

    public String generateConversationId(Long... participantIds) {
        StringBuilder sb = new StringBuilder("conv_");
        java.util.Arrays.sort(participantIds);
        for (Long id : participantIds) {
            sb.append(id).append("_");
        }
        return sb.toString();
    }

    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getSenderId() {
        return senderId;
    }

    public void setSenderId(Long senderId) {
        this.senderId = senderId;
    }

    public String getSenderName() {
        return senderName;
    }

    public void setSenderName(String senderName) {
        this.senderName = senderName;
    }

    public String getSenderRole() {
        return senderRole;
    }

    public void setSenderRole(String senderRole) {
        this.senderRole = senderRole;
    }

    public String getRecipientIds() {
        return recipientIds;
    }

    public void setRecipientIds(String recipientIds) {
        this.recipientIds = recipientIds;
    }

    public MessageRecipientType getRecipientType() {
        return recipientType;
    }

    public void setRecipientType(MessageRecipientType recipientType) {
        this.recipientType = recipientType;
    }

    public MessageType getMessageType() {
        return messageType;
    }

    public void setMessageType(MessageType messageType) {
        this.messageType = messageType;
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public MessageFormat getMessageFormat() {
        return messageFormat;
    }

    public void setMessageFormat(MessageFormat messageFormat) {
        this.messageFormat = messageFormat;
    }

    public MessagePriority getPriority() {
        return priority;
    }

    public void setPriority(MessagePriority priority) {
        this.priority = priority;
    }

    public String getThreadId() {
        return threadId;
    }

    public void setThreadId(String threadId) {
        this.threadId = threadId;
    }

    public Long getParentMessageId() {
        return parentMessageId;
    }

    public void setParentMessageId(Long parentMessageId) {
        this.parentMessageId = parentMessageId;
    }

    public String getConversationId() {
        return conversationId;
    }

    public void setConversationId(String conversationId) {
        this.conversationId = conversationId;
    }

    public Boolean getIsThreadStarter() {
        return isThreadStarter;
    }

    public void setIsThreadStarter(Boolean isThreadStarter) {
        this.isThreadStarter = isThreadStarter;
    }

    public Long getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(Long applicationId) {
        this.applicationId = applicationId;
    }

    public Long getInterviewId() {
        return interviewId;
    }

    public void setInterviewId(Long interviewId) {
        this.interviewId = interviewId;
    }

    public Long getJobPostingId() {
        return jobPostingId;
    }

    public void setJobPostingId(Long jobPostingId) {
        this.jobPostingId = jobPostingId;
    }

    public Long getOfferId() {
        return offerId;
    }

    public void setOfferId(Long offerId) {
        this.offerId = offerId;
    }

    public Boolean getIsRead() {
        return isRead;
    }

    public void setIsRead(Boolean isRead) {
        this.isRead = isRead;
    }

    public String getReadBy() {
        return readBy;
    }

    public void setReadBy(String readBy) {
        this.readBy = readBy;
    }

    public Boolean getIsDelivered() {
        return isDelivered;
    }

    public void setIsDelivered(Boolean isDelivered) {
        this.isDelivered = isDelivered;
    }

    public LocalDateTime getDeliveredAt() {
        return deliveredAt;
    }

    public void setDeliveredAt(LocalDateTime deliveredAt) {
        this.deliveredAt = deliveredAt;
    }

    public Boolean getIsArchived() {
        return isArchived;
    }

    public void setIsArchived(Boolean isArchived) {
        this.isArchived = isArchived;
    }

    public LocalDateTime getArchivedAt() {
        return archivedAt;
    }

    public void setArchivedAt(LocalDateTime archivedAt) {
        this.archivedAt = archivedAt;
    }

    public Boolean getIsDeleted() {
        return isDeleted;
    }

    public void setIsDeleted(Boolean isDeleted) {
        this.isDeleted = isDeleted;
    }

    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }

    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }

    public Long getDeletedBy() {
        return deletedBy;
    }

    public void setDeletedBy(Long deletedBy) {
        this.deletedBy = deletedBy;
    }

    public Boolean getHasAttachments() {
        return hasAttachments;
    }

    public void setHasAttachments(Boolean hasAttachments) {
        this.hasAttachments = hasAttachments;
    }

    public String getAttachmentUrls() {
        return attachmentUrls;
    }

    public void setAttachmentUrls(String attachmentUrls) {
        this.attachmentUrls = attachmentUrls;
    }

    public Boolean getIsUrgent() {
        return isUrgent;
    }

    public void setIsUrgent(Boolean isUrgent) {
        this.isUrgent = isUrgent;
    }

    public Boolean getRequiresResponse() {
        return requiresResponse;
    }

    public void setRequiresResponse(Boolean requiresResponse) {
        this.requiresResponse = requiresResponse;
    }

    public LocalDateTime getResponseDeadline() {
        return responseDeadline;
    }

    public void setResponseDeadline(LocalDateTime responseDeadline) {
        this.responseDeadline = responseDeadline;
    }

    public Boolean getIsConfidential() {
        return isConfidential;
    }

    public void setIsConfidential(Boolean isConfidential) {
        this.isConfidential = isConfidential;
    }

    public LocalDateTime getAutoDeleteAt() {
        return autoDeleteAt;
    }

    public void setAutoDeleteAt(LocalDateTime autoDeleteAt) {
        this.autoDeleteAt = autoDeleteAt;
    }

    public LocalDateTime getScheduledFor() {
        return scheduledFor;
    }

    public void setScheduledFor(LocalDateTime scheduledFor) {
        this.scheduledFor = scheduledFor;
    }

    public Boolean getIsScheduled() {
        return isScheduled;
    }

    public void setIsScheduled(Boolean isScheduled) {
        this.isScheduled = isScheduled;
    }

    public String getTags() {
        return tags;
    }

    public void setTags(String tags) {
        this.tags = tags;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getMetadata() {
        return metadata;
    }

    public void setMetadata(String metadata) {
        this.metadata = metadata;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public LocalDateTime getSentAt() {
        return sentAt;
    }

    public void setSentAt(LocalDateTime sentAt) {
        this.sentAt = sentAt;
    }
}