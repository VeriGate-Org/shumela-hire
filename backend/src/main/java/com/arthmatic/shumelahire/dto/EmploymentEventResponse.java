package com.arthmatic.shumelahire.dto;

import com.arthmatic.shumelahire.entity.EmploymentEvent;
import com.arthmatic.shumelahire.entity.EmploymentEventType;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class EmploymentEventResponse {

    private Long id;
    private Long employeeId;
    private String employeeName;
    private EmploymentEventType eventType;
    private LocalDate eventDate;
    private LocalDate effectiveDate;
    private String description;
    private String previousValue;
    private String newValue;
    private String reason;
    private Long approvedBy;
    private LocalDateTime approvedAt;
    private String referenceNumber;
    private String attachments;
    private LocalDateTime createdAt;

    public EmploymentEventResponse() {}

    public EmploymentEventResponse(EmploymentEvent event) {
        this.id = event.getId();
        this.employeeId = event.getEmployeeId();
        if (event.getEmployee() != null) {
            this.employeeName = event.getEmployee().getFullName();
        }
        this.eventType = event.getEventType();
        this.eventDate = event.getEventDate();
        this.effectiveDate = event.getEffectiveDate();
        this.description = event.getDescription();
        this.previousValue = event.getPreviousValue();
        this.newValue = event.getNewValue();
        this.reason = event.getReason();
        this.approvedBy = event.getApprovedBy();
        this.approvedAt = event.getApprovedAt();
        this.referenceNumber = event.getReferenceNumber();
        this.attachments = event.getAttachments();
        this.createdAt = event.getCreatedAt();
    }

    public static EmploymentEventResponse fromEntity(EmploymentEvent event) {
        return new EmploymentEventResponse(event);
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public EmploymentEventType getEventType() { return eventType; }
    public void setEventType(EmploymentEventType eventType) { this.eventType = eventType; }

    public LocalDate getEventDate() { return eventDate; }
    public void setEventDate(LocalDate eventDate) { this.eventDate = eventDate; }

    public LocalDate getEffectiveDate() { return effectiveDate; }
    public void setEffectiveDate(LocalDate effectiveDate) { this.effectiveDate = effectiveDate; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getPreviousValue() { return previousValue; }
    public void setPreviousValue(String previousValue) { this.previousValue = previousValue; }

    public String getNewValue() { return newValue; }
    public void setNewValue(String newValue) { this.newValue = newValue; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public Long getApprovedBy() { return approvedBy; }
    public void setApprovedBy(Long approvedBy) { this.approvedBy = approvedBy; }

    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }

    public String getReferenceNumber() { return referenceNumber; }
    public void setReferenceNumber(String referenceNumber) { this.referenceNumber = referenceNumber; }

    public String getAttachments() { return attachments; }
    public void setAttachments(String attachments) { this.attachments = attachments; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
