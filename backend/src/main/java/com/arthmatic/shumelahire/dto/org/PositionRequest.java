package com.arthmatic.shumelahire.dto.org;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class PositionRequest {

    @NotBlank
    private String title;

    private String code;

    private String department;

    private String grade;

    private Long reportingPositionId;

    @NotNull
    private BigDecimal fte = BigDecimal.ONE;

    private String status = "ACTIVE";

    private Long currentEmployeeId;

    private Boolean isVacant = true;

    private Boolean jobSharingAllowed = false;

    private String description;

    private Long orgUnitId;

    private String location;

    // Getters and Setters
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public Long getReportingPositionId() { return reportingPositionId; }
    public void setReportingPositionId(Long reportingPositionId) { this.reportingPositionId = reportingPositionId; }

    public BigDecimal getFte() { return fte; }
    public void setFte(BigDecimal fte) { this.fte = fte; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getCurrentEmployeeId() { return currentEmployeeId; }
    public void setCurrentEmployeeId(Long currentEmployeeId) { this.currentEmployeeId = currentEmployeeId; }

    public Boolean getIsVacant() { return isVacant; }
    public void setIsVacant(Boolean isVacant) { this.isVacant = isVacant; }

    public Boolean getJobSharingAllowed() { return jobSharingAllowed; }
    public void setJobSharingAllowed(Boolean jobSharingAllowed) { this.jobSharingAllowed = jobSharingAllowed; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Long getOrgUnitId() { return orgUnitId; }
    public void setOrgUnitId(Long orgUnitId) { this.orgUnitId = orgUnitId; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }
}
