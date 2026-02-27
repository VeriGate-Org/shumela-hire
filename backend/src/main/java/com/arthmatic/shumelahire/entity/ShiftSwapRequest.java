package com.arthmatic.shumelahire.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "shift_swap_requests")
public class ShiftSwapRequest extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_id", nullable = false)
    private Employee requester;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "requester_schedule_id", nullable = false)
    private ShiftSchedule requesterSchedule;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_employee_id", nullable = false)
    private Employee targetEmployee;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "target_schedule_id")
    private ShiftSchedule targetSchedule;

    @NotNull
    @Column(name = "swap_date", nullable = false)
    private LocalDate swapDate;

    @Column(name = "target_date")
    private LocalDate targetDate;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private SwapStatus status = SwapStatus.PENDING_TARGET;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(name = "target_response_at")
    private LocalDateTime targetResponseAt;

    @Column(name = "target_response_notes", columnDefinition = "TEXT")
    private String targetResponseNotes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "manager_approved_by")
    private Employee managerApprovedBy;

    @Column(name = "manager_approved_at")
    private LocalDateTime managerApprovedAt;

    @Column(name = "manager_notes", columnDefinition = "TEXT")
    private String managerNotes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public enum SwapStatus {
        PENDING_TARGET, TARGET_ACCEPTED, TARGET_REJECTED, PENDING_MANAGER, APPROVED, REJECTED, CANCELLED
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Employee getRequester() { return requester; }
    public void setRequester(Employee requester) { this.requester = requester; }

    public ShiftSchedule getRequesterSchedule() { return requesterSchedule; }
    public void setRequesterSchedule(ShiftSchedule requesterSchedule) { this.requesterSchedule = requesterSchedule; }

    public Employee getTargetEmployee() { return targetEmployee; }
    public void setTargetEmployee(Employee targetEmployee) { this.targetEmployee = targetEmployee; }

    public ShiftSchedule getTargetSchedule() { return targetSchedule; }
    public void setTargetSchedule(ShiftSchedule targetSchedule) { this.targetSchedule = targetSchedule; }

    public LocalDate getSwapDate() { return swapDate; }
    public void setSwapDate(LocalDate swapDate) { this.swapDate = swapDate; }

    public LocalDate getTargetDate() { return targetDate; }
    public void setTargetDate(LocalDate targetDate) { this.targetDate = targetDate; }

    public SwapStatus getStatus() { return status; }
    public void setStatus(SwapStatus status) { this.status = status; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public LocalDateTime getTargetResponseAt() { return targetResponseAt; }
    public void setTargetResponseAt(LocalDateTime targetResponseAt) { this.targetResponseAt = targetResponseAt; }

    public String getTargetResponseNotes() { return targetResponseNotes; }
    public void setTargetResponseNotes(String targetResponseNotes) { this.targetResponseNotes = targetResponseNotes; }

    public Employee getManagerApprovedBy() { return managerApprovedBy; }
    public void setManagerApprovedBy(Employee managerApprovedBy) { this.managerApprovedBy = managerApprovedBy; }

    public LocalDateTime getManagerApprovedAt() { return managerApprovedAt; }
    public void setManagerApprovedAt(LocalDateTime managerApprovedAt) { this.managerApprovedAt = managerApprovedAt; }

    public String getManagerNotes() { return managerNotes; }
    public void setManagerNotes(String managerNotes) { this.managerNotes = managerNotes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
