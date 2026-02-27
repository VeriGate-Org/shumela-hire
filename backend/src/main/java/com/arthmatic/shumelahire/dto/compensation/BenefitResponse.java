package com.arthmatic.shumelahire.dto.compensation;

import com.arthmatic.shumelahire.entity.compensation.Benefit;
import com.arthmatic.shumelahire.entity.compensation.BenefitType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class BenefitResponse {

    private Long id;
    private Long employeeId;
    private String employeeName;
    private BenefitType benefitType;
    private String benefitName;
    private String provider;
    private String policyNumber;
    private BigDecimal employeeContribution;
    private BigDecimal employerContribution;
    private BigDecimal totalContribution;
    private String currency;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isActive;
    private String notes;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static BenefitResponse fromEntity(Benefit benefit) {
        BenefitResponse response = new BenefitResponse();
        response.setId(benefit.getId());
        if (benefit.getEmployee() != null) {
            response.setEmployeeId(benefit.getEmployee().getId());
            response.setEmployeeName(benefit.getEmployee().getFullName());
        }
        response.setBenefitType(benefit.getBenefitType());
        response.setBenefitName(benefit.getBenefitName());
        response.setProvider(benefit.getProvider());
        response.setPolicyNumber(benefit.getPolicyNumber());
        response.setEmployeeContribution(benefit.getEmployeeContribution());
        response.setEmployerContribution(benefit.getEmployerContribution());
        response.setTotalContribution(benefit.getTotalContribution());
        response.setCurrency(benefit.getCurrency());
        response.setStartDate(benefit.getStartDate());
        response.setEndDate(benefit.getEndDate());
        response.setIsActive(benefit.getIsActive());
        response.setNotes(benefit.getNotes());
        response.setCreatedAt(benefit.getCreatedAt());
        response.setUpdatedAt(benefit.getUpdatedAt());
        return response;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

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

    public BigDecimal getTotalContribution() { return totalContribution; }
    public void setTotalContribution(BigDecimal totalContribution) { this.totalContribution = totalContribution; }

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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
