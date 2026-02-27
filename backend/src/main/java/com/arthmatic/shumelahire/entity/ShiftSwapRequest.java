package com.arthmatic.shumelahire.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "shift_swap_requests")
public class ShiftSwapRequest extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_employee_id", nullable = false)
    private Employee requesterEmployee;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_employee_id", nullable = false)
    private Employee targetEmployee;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_schedule_id", nullable = false)
    private ShiftSchedule requesterSchedule;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_schedule_id", nullable = false)
    private ShiftSchedule targetSchedule;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SwapStatus status = SwapStatus.PENDING;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(name = "rejection_reason", columnDefinition = "TEXT")
    private String rejectionReason;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "approved_by_id")
    private Employee approvedBy;

    @Column(name = "approved_at")
    private LocalDateTime approvedAt;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public enum SwapStatus {
        PENDING, TARGET_ACCEPTED, APPROVED, REJECTED, CANCELLED
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Employee getRequesterEmployee() { return requesterEmployee; }
    public void setRequesterEmployee(Employee requesterEmployee) { this.requesterEmployee = requesterEmployee; }

    public Employee getTargetEmployee() { return targetEmployee; }
    public void setTargetEmployee(Employee targetEmployee) { this.targetEmployee = targetEmployee; }

    public ShiftSchedule getRequesterSchedule() { return requesterSchedule; }
    public void setRequesterSchedule(ShiftSchedule requesterSchedule) { this.requesterSchedule = requesterSchedule; }

    public ShiftSchedule getTargetSchedule() { return targetSchedule; }
    public void setTargetSchedule(ShiftSchedule targetSchedule) { this.targetSchedule = targetSchedule; }

    public SwapStatus getStatus() { return status; }
    public void setStatus(SwapStatus status) { this.status = status; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getRejectionReason() { return rejectionReason; }
    public void setRejectionReason(String rejectionReason) { this.rejectionReason = rejectionReason; }

    public Employee getApprovedBy() { return approvedBy; }
    public void setApprovedBy(Employee approvedBy) { this.approvedBy = approvedBy; }

    public LocalDateTime getApprovedAt() { return approvedAt; }
    public void setApprovedAt(LocalDateTime approvedAt) { this.approvedAt = approvedAt; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
