package com.arthmatic.shumelahire.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "leave_delegations")
public class LeaveDelegation extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delegator_id", nullable = false)
    private Employee delegator;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "delegate_id", nullable = false)
    private Employee delegate;

    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Column(columnDefinition = "TEXT")
    private String reason;

    @Column(name = "can_approve_leave", nullable = false)
    private boolean canApproveLeave = true;

    @Column(name = "can_approve_encashment", nullable = false)
    private boolean canApproveEncashment = false;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Employee getDelegator() { return delegator; }
    public void setDelegator(Employee delegator) { this.delegator = delegator; }

    public Employee getDelegate() { return delegate; }
    public void setDelegate(Employee delegate) { this.delegate = delegate; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public boolean isCanApproveLeave() { return canApproveLeave; }
    public void setCanApproveLeave(boolean canApproveLeave) { this.canApproveLeave = canApproveLeave; }

    public boolean isCanApproveEncashment() { return canApproveEncashment; }
    public void setCanApproveEncashment(boolean canApproveEncashment) { this.canApproveEncashment = canApproveEncashment; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
