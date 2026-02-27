package com.arthmatic.shumelahire.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "headcount_plans")
public class HeadcountPlan extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 200)
    private String department;

    @NotNull
    @Column(name = "fiscal_year", nullable = false)
    private Integer fiscalYear;

    @Column(name = "planned_headcount", nullable = false)
    private Integer plannedHeadcount = 0;

    @Column(name = "current_headcount", nullable = false)
    private Integer currentHeadcount = 0;

    @Column(precision = 15, scale = 2)
    private BigDecimal budget;

    @Column(columnDefinition = "TEXT")
    private String notes;

    @Column(name = "forecast_vacancies", nullable = false)
    private Integer forecastVacancies = 0;

    @Column(name = "new_position_requests", nullable = false)
    private Integer newPositionRequests = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_unit_id")
    private OrgUnit orgUnit;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

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

    public OrgUnit getOrgUnit() { return orgUnit; }
    public void setOrgUnit(OrgUnit orgUnit) { this.orgUnit = orgUnit; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
