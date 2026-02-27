package com.arthmatic.shumelahire.dto.compensation;

import com.arthmatic.shumelahire.entity.compensation.TotalRewardsStatement;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class TotalRewardsResponse {

    private Long id;
    private Long employeeId;
    private String employeeName;
    private LocalDate statementDate;
    private LocalDate periodStart;
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
    private BigDecimal totalRemuneration;
    private String currency;
    private String notes;
    private String generatedBy;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static TotalRewardsResponse fromEntity(TotalRewardsStatement statement) {
        TotalRewardsResponse response = new TotalRewardsResponse();
        response.setId(statement.getId());
        if (statement.getEmployee() != null) {
            response.setEmployeeId(statement.getEmployee().getId());
            response.setEmployeeName(statement.getEmployee().getFullName());
        }
        response.setStatementDate(statement.getStatementDate());
        response.setPeriodStart(statement.getPeriodStart());
        response.setPeriodEnd(statement.getPeriodEnd());
        response.setBaseSalary(statement.getBaseSalary());
        response.setBonus(statement.getBonus());
        response.setCommission(statement.getCommission());
        response.setIncentives(statement.getIncentives());
        response.setMedicalAidContribution(statement.getMedicalAidContribution());
        response.setRetirementFundContribution(statement.getRetirementFundContribution());
        response.setLifeInsuranceContribution(statement.getLifeInsuranceContribution());
        response.setOtherBenefits(statement.getOtherBenefits());
        response.setTravelAllowance(statement.getTravelAllowance());
        response.setHousingAllowance(statement.getHousingAllowance());
        response.setOtherAllowances(statement.getOtherAllowances());
        response.setTotalRemuneration(statement.getTotalRemuneration());
        response.setCurrency(statement.getCurrency());
        response.setNotes(statement.getNotes());
        response.setGeneratedBy(statement.getGeneratedBy());
        response.setCreatedAt(statement.getCreatedAt());
        response.setUpdatedAt(statement.getUpdatedAt());
        return response;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

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

    public BigDecimal getTotalRemuneration() { return totalRemuneration; }
    public void setTotalRemuneration(BigDecimal totalRemuneration) { this.totalRemuneration = totalRemuneration; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public String getGeneratedBy() { return generatedBy; }
    public void setGeneratedBy(String generatedBy) { this.generatedBy = generatedBy; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
