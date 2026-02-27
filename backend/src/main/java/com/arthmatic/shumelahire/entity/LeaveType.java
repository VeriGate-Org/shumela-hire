package com.arthmatic.shumelahire.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "leave_types")
public class LeaveType extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String name;

    @NotBlank
    @Column(nullable = false, length = 30)
    private String code;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull
    @Column(name = "default_days_per_year", nullable = false, precision = 5, scale = 1)
    private BigDecimal defaultDaysPerYear = BigDecimal.ZERO;

    @Column(name = "max_carry_over_days", precision = 5, scale = 1)
    private BigDecimal maxCarryOverDays = BigDecimal.ZERO;

    @Column(name = "carry_over_allowed", nullable = false)
    private boolean carryOverAllowed = false;

    @Column(name = "carry_over_expiry_months")
    private Integer carryOverExpiryMonths;

    @Column(name = "requires_approval", nullable = false)
    private boolean requiresApproval = true;

    @Column(name = "requires_documentation", nullable = false)
    private boolean requiresDocumentation = false;

    @Column(name = "min_days_notice")
    private Integer minDaysNotice = 0;

    @Column(name = "max_consecutive_days")
    private Integer maxConsecutiveDays;

    @Column(name = "is_paid", nullable = false)
    private boolean paid = true;

    @Column(name = "is_active", nullable = false)
    private boolean active = true;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender_restriction", length = 20)
    private GenderRestriction genderRestriction;

    @Column(name = "applies_to_employment_types", columnDefinition = "TEXT")
    private String appliesToEmploymentTypes;

    @Column(name = "color_code", length = 7)
    private String colorCode;

    @Column(name = "sort_order")
    private Integer sortOrder = 0;

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

    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }

    public GenderRestriction getGenderRestriction() { return genderRestriction; }
    public void setGenderRestriction(GenderRestriction genderRestriction) { this.genderRestriction = genderRestriction; }

    public String getAppliesToEmploymentTypes() { return appliesToEmploymentTypes; }
    public void setAppliesToEmploymentTypes(String appliesToEmploymentTypes) { this.appliesToEmploymentTypes = appliesToEmploymentTypes; }

    public String getColorCode() { return colorCode; }
    public void setColorCode(String colorCode) { this.colorCode = colorCode; }

    public Integer getSortOrder() { return sortOrder; }
    public void setSortOrder(Integer sortOrder) { this.sortOrder = sortOrder; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
