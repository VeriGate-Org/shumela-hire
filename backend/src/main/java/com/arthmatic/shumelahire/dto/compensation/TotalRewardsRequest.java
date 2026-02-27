package com.arthmatic.shumelahire.dto.compensation;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDate;

public class TotalRewardsRequest {

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    @NotNull(message = "Statement date is required")
    private LocalDate statementDate;

    @NotNull(message = "Period start is required")
    private LocalDate periodStart;

    @NotNull(message = "Period end is required")
    private LocalDate periodEnd;

    private BigDecimal baseSalary;
    private BigDecimal bonus;
    private BigDecimal commission;
    private BigDecimal incentives;
    private BigDecimal medicalAidContribution;
    private BigDecimal retirementFundContribution;
    private BigDecimal lifeInsuranceContribution;
    private BigDecimal otherBenefits;
    private BigDecimal travelAllowance;
    private BigDecimal housingAllowance;
    private BigDecimal otherAllowances;
    private String currency = "ZAR";
    private String notes;
    private String generatedBy;

    // Getters and Setters
    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public LocalDate getStatementDate() { return statementDate; }
    public void setStatementDate(LocalDate statementDate) { this.statementDate = statementDate; }

    public LocalDate getPeriodStart() { return periodStart; }
    public void setPeriodStart(LocalDate periodStart) { this.periodStart = periodStart; }

    public LocalDate getPeriodEnd() { return periodEnd; }
    public void setPeriodEnd(LocalDate periodEnd) { this.periodEnd = periodEnd; }

    public BigDecimal getBaseSalary() { return baseSalary; }
    public void setBaseSalary(BigDecimal baseSalary) { this.baseSalary = baseSalary; }

    public BigDecimal getBonus() { return bonus; }
    public void setBonus(BigDecimal bonus) { this.bonus = bonus; }

    public BigDecimal getCommission() { return commission; }
    public void setCommission(BigDecimal commission) { this.commission = commission; }

    public BigDecimal getIncentives() { return incentives; }
    public void setIncentives(BigDecimal incentives) { this.incentives = incentives; }

    public BigDecimal getMedicalAidContribution() { return medicalAidContribution; }
    public void setMedicalAidContribution(BigDecimal medicalAidContribution) { this.medicalAidContribution = medicalAidContribution; }

    public BigDecimal getRetirementFundContribution() { return retirementFundContribution; }
    public void setRetirementFundContribution(BigDecimal retirementFundContribution) { this.retirementFundContribution = retirementFundContribution; }

    public BigDecimal getLifeInsuranceContribution() { return lifeInsuranceContribution; }
    public void setLifeInsuranceContribution(BigDecimal lifeInsuranceContribution) { this.lifeInsuranceContribution = lifeInsuranceContribution; }

    public BigDecimal getOtherBenefits() { return otherBenefits; }
    public void setOtherBenefits(BigDecimal otherBenefits) { this.otherBenefits = otherBenefits; }

    public BigDecimal getTravelAllowance() { return travelAllowance; }
    public void setTravelAllowance(BigDecimal travelAllowance) { this.travelAllowance = travelAllowance; }

    public BigDecimal getHousingAllowance() { return housingAllowance; }
    public void setHousingAllowance(BigDecimal housingAllowance) { this.housingAllowance = housingAllowance; }

    public BigDecimal getOtherAllowances() { return otherAllowances; }
    public void setOtherAllowances(BigDecimal otherAllowances) { this.otherAllowances = otherAllowances; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getGeneratedBy() { return generatedBy; }
    public void setGeneratedBy(String generatedBy) { this.generatedBy = generatedBy; }
}
