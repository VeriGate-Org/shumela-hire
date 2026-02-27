package com.arthmatic.shumelahire.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "leave_accruals")
public class LeaveAccrual extends TenantAwareEntity {

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
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_balance_id", nullable = false)
    private LeaveBalance leaveBalance;

    @NotNull
    @Column(name = "accrual_date", nullable = false)
    private LocalDate accrualDate;

    @NotNull
    @Column(name = "days_accrued", nullable = false, precision = 5, scale = 2)
    private BigDecimal daysAccrued;

    @NotNull
    @Column(name = "accrual_period_start", nullable = false)
    private LocalDate accrualPeriodStart;

    @NotNull
    @Column(name = "accrual_period_end", nullable = false)
    private LocalDate accrualPeriodEnd;

    @Column(name = "is_pro_rated", nullable = false)
    private boolean proRated = false;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

    public LeaveType getLeaveType() { return leaveType; }
    public void setLeaveType(LeaveType leaveType) { this.leaveType = leaveType; }

    public LeaveBalance getLeaveBalance() { return leaveBalance; }
    public void setLeaveBalance(LeaveBalance leaveBalance) { this.leaveBalance = leaveBalance; }

    public LocalDate getAccrualDate() { return accrualDate; }
    public void setAccrualDate(LocalDate accrualDate) { this.accrualDate = accrualDate; }

    public BigDecimal getDaysAccrued() { return daysAccrued; }
    public void setDaysAccrued(BigDecimal daysAccrued) { this.daysAccrued = daysAccrued; }

    public LocalDate getAccrualPeriodStart() { return accrualPeriodStart; }
    public void setAccrualPeriodStart(LocalDate accrualPeriodStart) { this.accrualPeriodStart = accrualPeriodStart; }

    public LocalDate getAccrualPeriodEnd() { return accrualPeriodEnd; }
    public void setAccrualPeriodEnd(LocalDate accrualPeriodEnd) { this.accrualPeriodEnd = accrualPeriodEnd; }

    public boolean isProRated() { return proRated; }
    public void setProRated(boolean proRated) { this.proRated = proRated; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
