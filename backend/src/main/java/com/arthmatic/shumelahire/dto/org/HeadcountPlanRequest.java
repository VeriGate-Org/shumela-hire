package com.arthmatic.shumelahire.dto.org;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class HeadcountPlanRequest {

    @NotBlank
    private String department;

    @NotNull
    private Integer fiscalYear;

    private Integer plannedHeadcount = 0;

    private Integer currentHeadcount = 0;

    private BigDecimal budget;

    private String notes;

    private Integer forecastVacancies = 0;

    private Integer newPositionRequests = 0;

    private Long orgUnitId;

    // Getters and Setters
    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public Integer getFiscalYear() { return fiscalYear; }
    public void setFiscalYear(Integer fiscalYear) { this.fiscalYear = fiscalYear; }

    public Integer getPlannedHeadcount() { return plannedHeadcount; }
    public void setPlannedHeadcount(Integer plannedHeadcount) { this.plannedHeadcount = plannedHeadcount; }

    public Integer getCurrentHeadcount() { return currentHeadcount; }
    public void setCurrentHeadcount(Integer currentHeadcount) { this.currentHeadcount = currentHeadcount; }

    public BigDecimal getBudget() { return budget; }
    public void setBudget(BigDecimal budget) { this.budget = budget; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Integer getForecastVacancies() { return forecastVacancies; }
    public void setForecastVacancies(Integer forecastVacancies) { this.forecastVacancies = forecastVacancies; }

    public Integer getNewPositionRequests() { return newPositionRequests; }
    public void setNewPositionRequests(Integer newPositionRequests) { this.newPositionRequests = newPositionRequests; }

    public Long getOrgUnitId() { return orgUnitId; }
    public void setOrgUnitId(Long orgUnitId) { this.orgUnitId = orgUnitId; }
}
