package com.arthmatic.shumelahire.dto.compensation;

import com.arthmatic.shumelahire.entity.compensation.BenefitType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class BenefitRequest {

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    @NotNull(message = "Benefit type is required")
    private BenefitType benefitType;

    @NotBlank(message = "Benefit name is required")
    private String benefitName;

    private String provider;
    private String policyNumber;
    private BigDecimal employeeContribution;
    private BigDecimal employerContribution;
    private String currency = "ZAR";
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isActive = true;
    private String notes;

    // Getters and Setters
    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public BenefitType getBenefitType() { return benefitType; }
    public void setBenefitType(BenefitType benefitType) { this.benefitType = benefitType; }

    public String getBenefitName() { return benefitName; }
    public void setBenefitName(String benefitName) { this.benefitName = benefitName; }

    public String getProvider() { return provider; }
    public void setProvider(String provider) { this.provider = provider; }

    public String getPolicyNumber() { return policyNumber; }
    public void setPolicyNumber(String policyNumber) { this.policyNumber = policyNumber; }

    public BigDecimal getEmployeeContribution() { return employeeContribution; }
    public void setEmployeeContribution(BigDecimal employeeContribution) { this.employeeContribution = employeeContribution; }

    public BigDecimal getEmployerContribution() { return employerContribution; }
    public void setEmployerContribution(BigDecimal employerContribution) { this.employerContribution = employerContribution; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }
}
