package com.arthmatic.shumelahire.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "leave_balances")
public class LeaveBalance extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_type_id", nullable = false)
    private LeaveType leaveType;

    @NotNull
    @Column(name = "leave_year", nullable = false)
    private Integer leaveYear;

    @Column(name = "opening_balance", nullable = false, precision = 6, scale = 1)
    private BigDecimal openingBalance = BigDecimal.ZERO;

    @Column(nullable = false, precision = 6, scale = 1)
    private BigDecimal accrued = BigDecimal.ZERO;

    @Column(nullable = false, precision = 6, scale = 1)
    private BigDecimal used = BigDecimal.ZERO;

    @Column(nullable = false, precision = 6, scale = 1)
    private BigDecimal pending = BigDecimal.ZERO;

    @Column(name = "carried_over", nullable = false, precision = 6, scale = 1)
    private BigDecimal carriedOver = BigDecimal.ZERO;

    @Column(nullable = false, precision = 6, scale = 1)
    private BigDecimal adjustment = BigDecimal.ZERO;

    @Column(name = "adjustment_reason", columnDefinition = "TEXT")
    private String adjustmentReason;

    @Column(nullable = false, precision = 6, scale = 1)
    private BigDecimal encashed = BigDecimal.ZERO;

    @Column(nullable = false, precision = 6, scale = 1)
    private BigDecimal forfeited = BigDecimal.ZERO;

    @Column(name = "last_accrual_date")
    private LocalDate lastAccrualDate;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * Computed closing balance: opening + accrued + carried_over + adjustment - used - encashed - forfeited.
     * In PostgreSQL this is a GENERATED ALWAYS AS STORED column. In JPA we compute it in Java.
     */
    public BigDecimal getClosingBalance() {
        return openingBalance.add(accrued).add(carriedOver).add(adjustment)
                .subtract(used).subtract(encashed).subtract(forfeited);
    }

    /**
     * Computed available balance: closing_balance - pending.
     */
    public BigDecimal getAvailableBalance() {
        return getClosingBalance().subtract(pending);
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

    public LeaveType getLeaveType() { return leaveType; }
    public void setLeaveType(LeaveType leaveType) { this.leaveType = leaveType; }

    public Integer getLeaveYear() { return leaveYear; }
    public void setLeaveYear(Integer leaveYear) { this.leaveYear = leaveYear; }

    public BigDecimal getOpeningBalance() { return openingBalance; }
    public void setOpeningBalance(BigDecimal openingBalance) { this.openingBalance = openingBalance; }

    public BigDecimal getAccrued() { return accrued; }
    public void setAccrued(BigDecimal accrued) { this.accrued = accrued; }

    public BigDecimal getUsed() { return used; }
    public void setUsed(BigDecimal used) { this.used = used; }

    public BigDecimal getPending() { return pending; }
    public void setPending(BigDecimal pending) { this.pending = pending; }

    public BigDecimal getCarriedOver() { return carriedOver; }
    public void setCarriedOver(BigDecimal carriedOver) { this.carriedOver = carriedOver; }

    public BigDecimal getAdjustment() { return adjustment; }
    public void setAdjustment(BigDecimal adjustment) { this.adjustment = adjustment; }

    public String getAdjustmentReason() { return adjustmentReason; }
    public void setAdjustmentReason(String adjustmentReason) { this.adjustmentReason = adjustmentReason; }

    public BigDecimal getEncashed() { return encashed; }
    public void setEncashed(BigDecimal encashed) { this.encashed = encashed; }

    public BigDecimal getForfeited() { return forfeited; }
    public void setForfeited(BigDecimal forfeited) { this.forfeited = forfeited; }

    public LocalDate getLastAccrualDate() { return lastAccrualDate; }
    public void setLastAccrualDate(LocalDate lastAccrualDate) { this.lastAccrualDate = lastAccrualDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
