package com.arthmatic.shumelahire.dto;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * A single event in the recruitment lifecycle timeline.
 * Polymorphic — can represent events from any entity type.
 */
public class LifecycleEvent implements Comparable<LifecycleEvent> {

    private String eventId;
    private String entityType;      // REQUISITION, JOB_AD, APPLICATION, INTERVIEW, OFFER, SALARY_REC, PIPELINE, AUDIT, BACKGROUND_CHECK
    private String entityId;
    private String eventType;       // e.g. "CREATED", "STATUS_CHANGE", "SUBMITTED", "COMPLETED"
    private String title;
    private String description;
    private LocalDateTime timestamp;
    private String performedBy;
    private String status;
    private String previousStatus;
    private Map<String, Object> metadata;
    private String icon;
    private String colorClass;

    public LifecycleEvent() {}

    public LifecycleEvent(String entityType, String entityId, String eventType,
                          String title, String description, LocalDateTime timestamp) {
        this.eventId = entityType + "-" + entityId + "-" + eventType;
        this.entityType = entityType;
        this.entityId = entityId;
        this.eventType = eventType;
        this.title = title;
        this.description = description;
        this.timestamp = timestamp;
    }

    @Override
    public int compareTo(LifecycleEvent other) {
        if (this.timestamp == null && other.timestamp == null) return 0;
        if (this.timestamp == null) return 1;
        if (other.timestamp == null) return -1;
        return this.timestamp.compareTo(other.timestamp);
    }

    // Getters and Setters
    public String getEventId() { return eventId; }
    public void setEventId(String eventId) { this.eventId = eventId; }

    public String getEntityType() { return entityType; }
    public void setEntityType(String entityType) { this.entityType = entityType; }

    public String getEntityId() { return entityId; }
    public void setEntityId(String entityId) { this.entityId = entityId; }

    public String getEventType() { return eventType; }
    public void setEventType(String eventType) { this.eventType = eventType; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalDateTime getTimestamp() { return timestamp; }
    public void setTimestamp(LocalDateTime timestamp) { this.timestamp = timestamp; }

    public String getPerformedBy() { return performedBy; }
    public void setPerformedBy(String performedBy) { this.performedBy = performedBy; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPreviousStatus() { return previousStatus; }
    public void setPreviousStatus(String previousStatus) { this.previousStatus = previousStatus; }

    public Map<String, Object> getMetadata() { return metadata; }
    public void setMetadata(Map<String, Object> metadata) { this.metadata = metadata; }

    public String getIcon() { return icon; }
    public void setIcon(String icon) { this.icon = icon; }

    public String getColorClass() { return colorClass; }
    public void setColorClass(String colorClass) { this.colorClass = colorClass; }
}
