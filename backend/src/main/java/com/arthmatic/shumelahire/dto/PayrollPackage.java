package com.arthmatic.shumelahire.dto;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public class PayrollPackage {

    private Long offerId;
    private String offerNumber;
    private String employeeFullName;
    private String email;
    private String idNumber;
    private String jobTitle;
    private String department;
    private BigDecimal baseSalary;
    private String currency;
    private String frequency;
    private BigDecimal bonusTarget;
    private String employmentType;
    private LocalDate startDate;
    private Integer probationDays;
    private Integer noticeDays;
    private Integer vacationDays;
    private Boolean healthInsurance;
    private Boolean retirement;
    private String taxNumber;
    private LocalDateTime exportedAt;
    private String exportedBy;

    public PayrollPackage() {}

    // Getters and Setters
    public Long getOfferId() { return offerId; }
    public void setOfferId(Long offerId) { this.offerId = offerId; }

    public String getOfferNumber() { return offerNumber; }
    public void setOfferNumber(String offerNumber) { this.offerNumber = offerNumber; }

    public String getEmployeeFullName() { return employeeFullName; }
    public void setEmployeeFullName(String employeeFullName) { this.employeeFullName = employeeFullName; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getIdNumber() { return idNumber; }
    public void setIdNumber(String idNumber) { this.idNumber = idNumber; }

    public String getJobTitle() { return jobTitle; }
    public void setJobTitle(String jobTitle) { this.jobTitle = jobTitle; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public BigDecimal getBaseSalary() { return baseSalary; }
    public void setBaseSalary(BigDecimal baseSalary) { this.baseSalary = baseSalary; }

    public String getCurrency() { return currency; }
    public void setCurrency(String currency) { this.currency = currency; }

    public String getFrequency() { return frequency; }
    public void setFrequency(String frequency) { this.frequency = frequency; }

    public BigDecimal getBonusTarget() { return bonusTarget; }
    public void setBonusTarget(BigDecimal bonusTarget) { this.bonusTarget = bonusTarget; }

    public String getEmploymentType() { return employmentType; }
    public void setEmploymentType(String employmentType) { this.employmentType = employmentType; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public Integer getProbationDays() { return probationDays; }
    public void setProbationDays(Integer probationDays) { this.probationDays = probationDays; }

    public Integer getNoticeDays() { return noticeDays; }
    public void setNoticeDays(Integer noticeDays) { this.noticeDays = noticeDays; }

    public Integer getVacationDays() { return vacationDays; }
    public void setVacationDays(Integer vacationDays) { this.vacationDays = vacationDays; }

    public Boolean getHealthInsurance() { return healthInsurance; }
    public void setHealthInsurance(Boolean healthInsurance) { this.healthInsurance = healthInsurance; }

    public Boolean getRetirement() { return retirement; }
    public void setRetirement(Boolean retirement) { this.retirement = retirement; }

    public String getTaxNumber() { return taxNumber; }
    public void setTaxNumber(String taxNumber) { this.taxNumber = taxNumber; }

    public LocalDateTime getExportedAt() { return exportedAt; }
    public void setExportedAt(LocalDateTime exportedAt) { this.exportedAt = exportedAt; }

    public String getExportedBy() { return exportedBy; }
    public void setExportedBy(String exportedBy) { this.exportedBy = exportedBy; }
}
