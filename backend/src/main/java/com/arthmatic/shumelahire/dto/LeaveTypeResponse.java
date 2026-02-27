package com.arthmatic.shumelahire.dto;

import com.arthmatic.shumelahire.entity.LeaveType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class LeaveTypeResponse {

    private Long id;
    private String name;
    private String code;
    private String description;
    private BigDecimal defaultDaysPerYear;
    private BigDecimal maxCarryOverDays;
    private boolean carryOverAllowed;
    private Integer carryOverExpiryMonths;
    private boolean requiresApproval;
    private boolean requiresDocumentation;
    private Integer minDaysNotice;
    private Integer maxConsecutiveDays;
    private boolean paid;
    private boolean active;
    private String genderRestriction;
    private String appliesToEmploymentTypes;
    private String colorCode;
    private Integer sortOrder;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public LeaveTypeResponse(LeaveType leaveType) {
        this.id = leaveType.getId();
        this.name = leaveType.getName();
        this.code = leaveType.getCode();
        this.description = leaveType.getDescription();
        this.defaultDaysPerYear = leaveType.getDefaultDaysPerYear();
        this.maxCarryOverDays = leaveType.getMaxCarryOverDays();
        this.carryOverAllowed = leaveType.isCarryOverAllowed();
        this.carryOverExpiryMonths = leaveType.getCarryOverExpiryMonths();
        this.requiresApproval = leaveType.isRequiresApproval();
        this.requiresDocumentation = leaveType.isRequiresDocumentation();
        this.minDaysNotice = leaveType.getMinDaysNotice();
        this.maxConsecutiveDays = leaveType.getMaxConsecutiveDays();
        this.paid = leaveType.isPaid();
        this.active = leaveType.isActive();
        this.genderRestriction = leaveType.getGenderRestriction() != null
                ? leaveType.getGenderRestriction().name() : null;
        this.appliesToEmploymentTypes = leaveType.getAppliesToEmploymentTypes();
        this.colorCode = leaveType.getColorCode();
        this.sortOrder = leaveType.getSortOrder();
        this.createdAt = leaveType.getCreatedAt();
        this.updatedAt = leaveType.getUpdatedAt();
    }

    public static LeaveTypeResponse fromEntity(LeaveType leaveType) {
        return new LeaveTypeResponse(leaveType);
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public String getCode() { return code; }
    public String getDescription() { return description; }
    public BigDecimal getDefaultDaysPerYear() { return defaultDaysPerYear; }
    public BigDecimal getMaxCarryOverDays() { return maxCarryOverDays; }
    public boolean isCarryOverAllowed() { return carryOverAllowed; }
    public Integer getCarryOverExpiryMonths() { return carryOverExpiryMonths; }
    public boolean isRequiresApproval() { return requiresApproval; }
    public boolean isRequiresDocumentation() { return requiresDocumentation; }
    public Integer getMinDaysNotice() { return minDaysNotice; }
    public Integer getMaxConsecutiveDays() { return maxConsecutiveDays; }
    public boolean isPaid() { return paid; }
    public boolean isActive() { return active; }
    public String getGenderRestriction() { return genderRestriction; }
    public String getAppliesToEmploymentTypes() { return appliesToEmploymentTypes; }
    public String getColorCode() { return colorCode; }
    public Integer getSortOrder() { return sortOrder; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}
