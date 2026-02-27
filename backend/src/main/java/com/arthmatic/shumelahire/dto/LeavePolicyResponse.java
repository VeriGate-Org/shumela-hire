package com.arthmatic.shumelahire.dto;

import com.arthmatic.shumelahire.entity.LeavePolicy;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class LeavePolicyResponse {

    private Long id;
    private String name;
    private String description;
    private Long leaveTypeId;
    private String leaveTypeName;
    private String employmentType;
    private String department;
    private String jobGrade;
    private Integer minServiceMonths;
    private BigDecimal annualEntitlement;
    private String accrualFrequency;
    private boolean proRataOnJoin;
    private boolean proRataOnLeave;
    private BigDecimal maxNegativeBalance;
    private boolean requireManagerApproval;
    private boolean requireHrApproval;
    private Integer autoApproveDaysThreshold;
    private LocalDate effectiveFrom;
    private LocalDate effectiveTo;
    private boolean active;
    private LocalDateTime createdAt;

    public LeavePolicyResponse(LeavePolicy policy) {
        this.id = policy.getId();
        this.name = policy.getName();
        this.description = policy.getDescription();
        this.leaveTypeId = policy.getLeaveType().getId();
        this.leaveTypeName = policy.getLeaveType().getName();
        this.employmentType = policy.getEmploymentType();
        this.department = policy.getDepartment();
        this.jobGrade = policy.getJobGrade();
        this.minServiceMonths = policy.getMinServiceMonths();
        this.annualEntitlement = policy.getAnnualEntitlement();
        this.accrualFrequency = policy.getAccrualFrequency().name();
        this.proRataOnJoin = policy.isProRataOnJoin();
        this.proRataOnLeave = policy.isProRataOnLeave();
        this.maxNegativeBalance = policy.getMaxNegativeBalance();
        this.requireManagerApproval = policy.isRequireManagerApproval();
        this.requireHrApproval = policy.isRequireHrApproval();
        this.autoApproveDaysThreshold = policy.getAutoApproveDaysThreshold();
        this.effectiveFrom = policy.getEffectiveFrom();
        this.effectiveTo = policy.getEffectiveTo();
        this.active = policy.isActive();
        this.createdAt = policy.getCreatedAt();
    }

    public static LeavePolicyResponse fromEntity(LeavePolicy policy) {
        return new LeavePolicyResponse(policy);
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Long getLeaveTypeId() { return leaveTypeId; }
    public String getLeaveTypeName() { return leaveTypeName; }
    public String getEmploymentType() { return employmentType; }
    public String getDepartment() { return department; }
    public String getJobGrade() { return jobGrade; }
    public Integer getMinServiceMonths() { return minServiceMonths; }
    public BigDecimal getAnnualEntitlement() { return annualEntitlement; }
    public String getAccrualFrequency() { return accrualFrequency; }
    public boolean isProRataOnJoin() { return proRataOnJoin; }
    public boolean isProRataOnLeave() { return proRataOnLeave; }
    public BigDecimal getMaxNegativeBalance() { return maxNegativeBalance; }
    public boolean isRequireManagerApproval() { return requireManagerApproval; }
    public boolean isRequireHrApproval() { return requireHrApproval; }
    public Integer getAutoApproveDaysThreshold() { return autoApproveDaysThreshold; }
    public LocalDate getEffectiveFrom() { return effectiveFrom; }
    public LocalDate getEffectiveTo() { return effectiveTo; }
    public boolean isActive() { return active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
