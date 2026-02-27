package com.arthmatic.shumelahire.entity.compensation;

import com.arthmatic.shumelahire.entity.Employee;
import com.arthmatic.shumelahire.entity.TenantAwareEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "total_rewards_statements")
public class TotalRewardsStatement extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    @NotNull(message = "Employee is required")
    private Employee employee;

    @NotNull
    @Column(name = "statement_date", nullable = false)
    private LocalDate statementDate;

    @Column(name = "period_start", nullable = false)
    private LocalDate periodStart;

    @Column(name = "period_end", nullable = false)
    private LocalDate periodEnd;

    // Base compensation
    @Column(name = "base_salary", precision = 15, scale = 2)
    private BigDecimal baseSalary;

    // Variable pay
    @Column(name = "bonus", precision = 15, scale = 2)
    private BigDecimal bonus;

    @Column(name = "commission", precision = 15, scale = 2)
    private BigDecimal commission;

    @Column(name = "incentives", precision = 15, scale = 2)
    private BigDecimal incentives;

    // Benefits (employer cost)
    @Column(name = "medical_aid_contribution", precision = 15, scale = 2)
    private BigDecimal medicalAidContribution;

    @Column(name = "retirement_fund_contribution", precision = 15, scale = 2)
    private BigDecimal retirementFundContribution;

    @Column(name = "life_insurance_contribution", precision = 15, scale = 2)
    private BigDecimal lifeInsuranceContribution;

    @Column(name = "other_benefits", precision = 15, scale = 2)
    private BigDecimal otherBenefits;

    // Allowances
    @Column(name = "travel_allowance", precision = 15, scale = 2)
    private BigDecimal travelAllowance;

    @Column(name = "housing_allowance", precision = 15, scale = 2)
    private BigDecimal housingAllowance;

    @Column(name = "other_allowances", precision = 15, scale = 2)
    private BigDecimal otherAllowances;

    // Totals
    @Column(name = "total_remuneration", precision = 15, scale = 2)
    private BigDecimal totalRemuneration;

    @Column(name = "currency", length = 10)
    private String currency = "ZAR";

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @Column(name = "generated_by", length = 255)
    private String generatedBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public TotalRewardsStatement() {}

    public void calculateTotalRemuneration() {
        BigDecimal total = BigDecimal.ZERO;
        if (baseSalary != null) total = total.add(baseSalary);
        if (bonus != null) total = total.add(bonus);
        if (commission != null) total = total.add(commission);
        if (incentives != null) total = total.add(incentives);
        if (medicalAidContribution != null) total = total.add(medicalAidContribution);
        if (retirementFundContribution != null) total = total.add(retirementFundContribution);
        if (lifeInsuranceContribution != null) total = total.add(lifeInsuranceContribution);
        if (otherBenefits != null) total = total.add(otherBenefits);
        if (travelAllowance != null) total = total.add(travelAllowance);
        if (housingAllowance != null) total = total.add(housingAllowance);
        if (otherAllowances != null) total = total.add(otherAllowances);
        this.totalRemuneration = total;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

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
