package com.arthmatic.shumelahire.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "positions")
public class Position extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 200)
    private String title;

    @Column(length = 50)
    private String code;

    @Column(length = 200)
    private String department;

    @Column(length = 50)
    private String grade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "reporting_position_id")
    private Position reportingPosition;

    @NotNull
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal fte = BigDecimal.ONE;

    @NotNull
    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 30)
    private PositionStatus status = PositionStatus.ACTIVE;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "current_employee_id")
    private Employee currentEmployee;

    @Column(name = "is_vacant", nullable = false)
    private Boolean isVacant = true;

    @Column(name = "job_sharing_allowed", nullable = false)
    private Boolean jobSharingAllowed = false;

    @Column(columnDefinition = "TEXT")
    private String description;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "org_unit_id")
    private OrgUnit orgUnit;

    @Column(length = 200)
    private String location;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public Position getReportingPosition() { return reportingPosition; }
    public void setReportingPosition(Position reportingPosition) { this.reportingPosition = reportingPosition; }

    public BigDecimal getFte() { return fte; }
    public void setFte(BigDecimal fte) { this.fte = fte; }

    public PositionStatus getStatus() { return status; }
    public void setStatus(PositionStatus status) { this.status = status; }

    public Employee getCurrentEmployee() { return currentEmployee; }
    public void setCurrentEmployee(Employee currentEmployee) { this.currentEmployee = currentEmployee; }

    public Boolean getIsVacant() { return isVacant; }
    public void setIsVacant(Boolean isVacant) { this.isVacant = isVacant; }

    public Boolean getJobSharingAllowed() { return jobSharingAllowed; }
    public void setJobSharingAllowed(Boolean jobSharingAllowed) { this.jobSharingAllowed = jobSharingAllowed; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public OrgUnit getOrgUnit() { return orgUnit; }
    public void setOrgUnit(OrgUnit orgUnit) { this.orgUnit = orgUnit; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
