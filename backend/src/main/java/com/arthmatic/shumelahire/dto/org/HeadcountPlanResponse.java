package com.arthmatic.shumelahire.dto.org;

import com.arthmatic.shumelahire.entity.HeadcountPlan;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class HeadcountPlanResponse {

    private Long id;
    private String department;
    private Integer fiscalYear;
    private Integer plannedHeadcount;
    private Integer currentHeadcount;
    private BigDecimal budget;
    private String notes;
    private Integer forecastVacancies;
    private Integer newPositionRequests;
    private Long orgUnitId;
    private String orgUnitName;
    private Integer variance;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static HeadcountPlanResponse fromEntity(HeadcountPlan plan) {
        HeadcountPlanResponse response = new HeadcountPlanResponse();
        response.setId(plan.getId());
        response.setDepartment(plan.getDepartment());
        response.setFiscalYear(plan.getFiscalYear());
        response.setPlannedHeadcount(plan.getPlannedHeadcount());
        response.setCurrentHeadcount(plan.getCurrentHeadcount());
        response.setBudget(plan.getBudget());
        response.setNotes(plan.getNotes());
        response.setForecastVacancies(plan.getForecastVacancies());
        response.setNewPositionRequests(plan.getNewPositionRequests());
        response.setVariance(plan.getPlannedHeadcount() - plan.getCurrentHeadcount());
        response.setCreatedAt(plan.getCreatedAt());
        response.setUpdatedAt(plan.getUpdatedAt());

        if (plan.getOrgUnit() != null) {
            response.setOrgUnitId(plan.getOrgUnit().getId());
            response.setOrgUnitName(plan.getOrgUnit().getName());
        }

        return response;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public String getOrgUnitName() { return orgUnitName; }
    public void setOrgUnitName(String orgUnitName) { this.orgUnitName = orgUnitName; }

    public Integer getVariance() { return variance; }
    public void setVariance(Integer variance) { this.variance = variance; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
