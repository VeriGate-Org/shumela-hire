package com.arthmatic.shumelahire.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class LeaveTypeRequest {

    @NotBlank(message = "Leave type name is required")
    private String name;

    @NotBlank(message = "Leave type code is required")
    private String code;

    private String description;

    @NotNull(message = "Default days per year is required")
    private BigDecimal defaultDaysPerYear;

    private BigDecimal maxCarryOverDays;
    private boolean carryOverAllowed;
    private Integer carryOverExpiryMonths;
    private boolean requiresApproval = true;
    private boolean requiresDocumentation;
    private Integer minDaysNotice;
    private Integer maxConsecutiveDays;
    private boolean paid = true;
    private String genderRestriction;
    private String appliesToEmploymentTypes;
    private String colorCode;
    private Integer sortOrder;

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public BigDecimal getDefaultDaysPerYear() { return defaultDaysPerYear; }
    public void setDefaultDaysPerYear(BigDecimal defaultDaysPerYear) { this.defaultDaysPerYear = defaultDaysPerYear; }

    public BigDecimal getMaxCarryOverDays() { return maxCarryOverDays; }
    public void setMaxCarryOverDays(BigDecimal maxCarryOverDays) { this.maxCarryOverDays = maxCarryOverDays; }

    public boolean isCarryOverAllowed() { return carryOverAllowed; }
    public void setCarryOverAllowed(boolean carryOverAllowed) { this.carryOverAllowed = carryOverAllowed; }

    public Integer getCarryOverExpiryMonths() { return carryOverExpiryMonths; }
    public void setCarryOverExpiryMonths(Integer carryOverExpiryMonths) { this.carryOverExpiryMonths = carryOverExpiryMonths; }

    public boolean isRequiresApproval() { return requiresApproval; }
    public void setRequiresApproval(boolean requiresApproval) { this.requiresApproval = requiresApproval; }

    public boolean isRequiresDocumentation() { return requiresDocumentation; }
    public void setRequiresDocumentation(boolean requiresDocumentation) { this.requiresDocumentation = requiresDocumentation; }

    public Integer getMinDaysNotice() { return minDaysNotice; }
    public void setMinDaysNotice(Integer minDaysNotice) { this.minDaysNotice = minDaysNotice; }

    public Integer getMaxConsecutiveDays() { return maxConsecutiveDays; }
    public void setMaxConsecutiveDays(Integer maxConsecutiveDays) { this.maxConsecutiveDays = maxConsecutiveDays; }

    public boolean isPaid() { return paid; }
    public void setPaid(boolean paid) { this.paid = paid; }

    public String getGenderRestriction() { return genderRestriction; }
    public void setGenderRestriction(String genderRestriction) { this.genderRestriction = genderRestriction; }

    public String getAppliesToEmploymentTypes() { return appliesToEmploymentTypes; }
    public void setAppliesToEmploymentTypes(String appliesToEmploymentTypes) { this.appliesToEmploymentTypes = appliesToEmploymentTypes; }

    public String getColorCode() { return colorCode; }
    public void setColorCode(String colorCode) { this.colorCode = colorCode; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }
}
