package com.arthmatic.shumelahire.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "leave_policies")
public class LeavePolicy extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 200)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "leave_type_id", nullable = false)
    private LeaveType leaveType;

    @Column(name = "employment_type", length = 50)
    private String employmentType;

    @Column(length = 200)
    private String department;

    @Column(name = "job_grade", length = 50)
    private String jobGrade;

    @Column(name = "min_service_months")
    private Integer minServiceMonths = 0;

    @NotNull
    @Column(name = "annual_entitlement", nullable = false, precision = 5, scale = 1)
    private BigDecimal annualEntitlement;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(name = "accrual_frequency", nullable = false, length = 30)
    private LeaveAccrualFrequency accrualFrequency = LeaveAccrualFrequency.MONTHLY;

    @Column(name = "pro_rata_on_join", nullable = false)
    private boolean proRataOnJoin = true;

    @Column(name = "pro_rata_on_leave", nullable = false)
    private boolean proRataOnLeave = true;

    @Column(name = "max_negative_balance", precision = 5, scale = 1)
    private BigDecimal maxNegativeBalance = BigDecimal.ZERO;

    @Column(name = "require_manager_approval", nullable = false)
    private boolean requireManagerApproval = true;

    @Column(name = "require_hr_approval", nullable = false)
    private boolean requireHrApproval = false;

    @Column(name = "auto_approve_days_threshold")
    private Integer autoApproveDaysThreshold;

    @NotNull
    @Column(name = "effective_from", nullable = false)
    private LocalDate effectiveFrom;

    @Column(name = "effective_to")
    private LocalDate effectiveTo;

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

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LeaveType getLeaveType() { return leaveType; }
    public void setLeaveType(LeaveType leaveType) { this.leaveType = leaveType; }

    public String getEmploymentType() { return employmentType; }
    public void setEmploymentType(String employmentType) { this.employmentType = employmentType; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getJobGrade() { return jobGrade; }
    public void setJobGrade(String jobGrade) { this.jobGrade = jobGrade; }

    public Integer getMinServiceMonths() { return minServiceMonths; }
    public void setMinServiceMonths(Integer minServiceMonths) { this.minServiceMonths = minServiceMonths; }

    public BigDecimal getAnnualEntitlement() { return annualEntitlement; }
    public void setAnnualEntitlement(BigDecimal annualEntitlement) { this.annualEntitlement = annualEntitlement; }

    public LeaveAccrualFrequency getAccrualFrequency() { return accrualFrequency; }
    public void setAccrualFrequency(LeaveAccrualFrequency accrualFrequency) { this.accrualFrequency = accrualFrequency; }

    public boolean isProRataOnJoin() { return proRataOnJoin; }
    public void setProRataOnJoin(boolean proRataOnJoin) { this.proRataOnJoin = proRataOnJoin; }

    public boolean isProRataOnLeave() { return proRataOnLeave; }
    public void setProRataOnLeave(boolean proRataOnLeave) { this.proRataOnLeave = proRataOnLeave; }

    public BigDecimal getMaxNegativeBalance() { return maxNegativeBalance; }
    public void setMaxNegativeBalance(BigDecimal maxNegativeBalance) { this.maxNegativeBalance = maxNegativeBalance; }

    public boolean isRequireManagerApproval() { return requireManagerApproval; }
    public void setRequireManagerApproval(boolean requireManagerApproval) { this.requireManagerApproval = requireManagerApproval; }

    public boolean isRequireHrApproval() { return requireHrApproval; }
    public void setRequireHrApproval(boolean requireHrApproval) { this.requireHrApproval = requireHrApproval; }

    public Integer getAutoApproveDaysThreshold() { return autoApproveDaysThreshold; }
    public void setAutoApproveDaysThreshold(Integer autoApproveDaysThreshold) { this.autoApproveDaysThreshold = autoApproveDaysThreshold; }

    public LocalDate getEffectiveFrom() { return effectiveFrom; }
    public void setEffectiveFrom(LocalDate effectiveFrom) { this.effectiveFrom = effectiveFrom; }

    public LocalDate getEffectiveTo() { return effectiveTo; }
    public void setEffectiveTo(LocalDate effectiveTo) { this.effectiveTo = effectiveTo; }

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
