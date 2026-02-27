package com.arthmatic.shumelahire.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sap_payroll_transmissions")
public class SapPayrollTransmission extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "offer_id", nullable = false)
    private Offer offer;

    @Column(name = "transmission_id", unique = true, nullable = false, length = 50)
    private String transmissionId;

    @Column(name = "sap_employee_number", length = 20)
    private String sapEmployeeNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 30)
    private TransmissionStatus status = TransmissionStatus.PENDING;

    @Column(name = "payload_json", length = 10000)
    private String payloadJson;

    @Column(name = "response_json", length = 10000)
    private String responseJson;

    @Column(name = "error_message", length = 10000)
    private String errorMessage;

    @Column(name = "retry_count", nullable = false)
    private Integer retryCount = 0;

    @Column(name = "max_retries", nullable = false)
    private Integer maxRetries = 3;

    @Column(name = "next_retry_at")
    private LocalDateTime nextRetryAt;

    @Column(name = "initiated_by")
    private Long initiatedBy;

    @Column(name = "sap_company_code", length = 10)
    private String sapCompanyCode;

    @Column(name = "sap_payroll_area", length = 10)
    private String sapPayrollArea;

    @Column(name = "validation_errors", length = 10000)
    private String validationErrors;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "transmitted_at")
    private LocalDateTime transmittedAt;

    @Column(name = "confirmed_at")
    private LocalDateTime confirmedAt;

    @Column(name = "cancelled_at")
    private LocalDateTime cancelledAt;

    @Column(name = "cancelled_by")
    private Long cancelledBy;

    @Column(name = "cancellation_reason", length = 10000)
    private String cancellationReason;

    // Constructors
    public SapPayrollTransmission() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    // Business methods
    public boolean canBeRetried() {
        return status.isRetryable() && retryCount < maxRetries;
    }

    public boolean canBeCancelled() {
        return status.canBeCancelled();
    }

    public boolean isComplete() {
        return status == TransmissionStatus.CONFIRMED;
    }

    public boolean hasFailed() {
        return status == TransmissionStatus.FAILED;
    }

    public void incrementRetryCount() {
        this.retryCount++;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Offer getOffer() { return offer; }
    public void setOffer(Offer offer) { this.offer = offer; }

    public String getTransmissionId() { return transmissionId; }
    public void setTransmissionId(String transmissionId) { this.transmissionId = transmissionId; }

    public String getSapEmployeeNumber() { return sapEmployeeNumber; }
    public void setSapEmployeeNumber(String sapEmployeeNumber) { this.sapEmployeeNumber = sapEmployeeNumber; }

    public TransmissionStatus getStatus() { return status; }
    public void setStatus(TransmissionStatus status) { this.status = status; }

    public String getPayloadJson() { return payloadJson; }
    public void setPayloadJson(String payloadJson) { this.payloadJson = payloadJson; }

    public String getResponseJson() { return responseJson; }
    public void setResponseJson(String responseJson) { this.responseJson = responseJson; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public Integer getRetryCount() { return retryCount; }
    public void setRetryCount(Integer retryCount) { this.retryCount = retryCount; }

    public Integer getMaxRetries() { return maxRetries; }
    public void setMaxRetries(Integer maxRetries) { this.maxRetries = maxRetries; }

    public LocalDateTime getNextRetryAt() { return nextRetryAt; }
    public void setNextRetryAt(LocalDateTime nextRetryAt) { this.nextRetryAt = nextRetryAt; }

    public Long getInitiatedBy() { return initiatedBy; }
    public void setInitiatedBy(Long initiatedBy) { this.initiatedBy = initiatedBy; }

    public String getSapCompanyCode() { return sapCompanyCode; }
    public void setSapCompanyCode(String sapCompanyCode) { this.sapCompanyCode = sapCompanyCode; }

    public String getSapPayrollArea() { return sapPayrollArea; }
    public void setSapPayrollArea(String sapPayrollArea) { this.sapPayrollArea = sapPayrollArea; }

    public String getValidationErrors() { return validationErrors; }
    public void setValidationErrors(String validationErrors) { this.validationErrors = validationErrors; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }

    public LocalDateTime getTransmittedAt() { return transmittedAt; }
    public void setTransmittedAt(LocalDateTime transmittedAt) { this.transmittedAt = transmittedAt; }

    public LocalDateTime getConfirmedAt() { return confirmedAt; }
    public void setConfirmedAt(LocalDateTime confirmedAt) { this.confirmedAt = confirmedAt; }

    public LocalDateTime getCancelledAt() { return cancelledAt; }
    public void setCancelledAt(LocalDateTime cancelledAt) { this.cancelledAt = cancelledAt; }

    public Long getCancelledBy() { return cancelledBy; }
    public void setCancelledBy(Long cancelledBy) { this.cancelledBy = cancelledBy; }

    public String getCancellationReason() { return cancellationReason; }
    public void setCancellationReason(String cancellationReason) { this.cancellationReason = cancellationReason; }
}
