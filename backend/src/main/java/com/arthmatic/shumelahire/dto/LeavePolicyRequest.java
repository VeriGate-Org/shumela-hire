package com.arthmatic.shumelahire.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class LeavePolicyRequest {

    @NotBlank(message = "Policy name is required")
    private String name;

    private String description;

    @NotNull(message = "Leave type ID is required")
    private Long leaveTypeId;

    private String employmentType;
    private String department;
    private String jobGrade;
    private Integer minServiceMonths;

    @NotNull(message = "Annual entitlement is required")
    private BigDecimal annualEntitlement;

    private String accrualFrequency = "MONTHLY";
    private boolean proRataOnJoin = true;
    private boolean proRataOnLeave = true;
    private BigDecimal maxNegativeBalance;
    private boolean requireManagerApproval = true;
    private boolean requireHrApproval;
    private Integer autoApproveDaysThreshold;

    @NotNull(message = "Effective from date is required")
    private LocalDate effectiveFrom;

    private LocalDate effectiveTo;

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Long getLeaveTypeId() { return leaveTypeId; }
    public void setLeaveTypeId(Long leaveTypeId) { this.leaveTypeId = leaveTypeId; }

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

    public String getAccrualFrequency() { return accrualFrequency; }
    public void setAccrualFrequency(String accrualFrequency) { this.accrualFrequency = accrualFrequency; }

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
}
