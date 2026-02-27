package com.arthmatic.shumelahire.dto.org;

import com.arthmatic.shumelahire.entity.Position;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public class PositionResponse {

    private Long id;
    private String title;
    private String code;
    private String department;
    private String grade;
    private Long reportingPositionId;
    private String reportingPositionTitle;
    private BigDecimal fte;
    private String status;
    private Long currentEmployeeId;
    private String currentEmployeeName;
    private Boolean isVacant;
    private Boolean jobSharingAllowed;
    private String description;
    private Long orgUnitId;
    private String orgUnitName;
    private String location;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static PositionResponse fromEntity(Position position) {
        PositionResponse response = new PositionResponse();
        response.setId(position.getId());
        response.setTitle(position.getTitle());
        response.setCode(position.getCode());
        response.setDepartment(position.getDepartment());
        response.setGrade(position.getGrade());
        response.setFte(position.getFte());
        response.setStatus(position.getStatus().name());
        response.setIsVacant(position.getIsVacant());
        response.setJobSharingAllowed(position.getJobSharingAllowed());
        response.setDescription(position.getDescription());
        response.setLocation(position.getLocation());
        response.setCreatedAt(position.getCreatedAt());
        response.setUpdatedAt(position.getUpdatedAt());

        if (position.getReportingPosition() != null) {
            response.setReportingPositionId(position.getReportingPosition().getId());
            response.setReportingPositionTitle(position.getReportingPosition().getTitle());
        }

        if (position.getCurrentEmployee() != null) {
            response.setCurrentEmployeeId(position.getCurrentEmployee().getId());
            response.setCurrentEmployeeName(position.getCurrentEmployee().getFullName());
        }

        if (position.getOrgUnit() != null) {
            response.setOrgUnitId(position.getOrgUnit().getId());
            response.setOrgUnitName(position.getOrgUnit().getName());
        }

        return response;
    }

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

    public Long getReportingPositionId() { return reportingPositionId; }
    public void setReportingPositionId(Long reportingPositionId) { this.reportingPositionId = reportingPositionId; }

    public String getReportingPositionTitle() { return reportingPositionTitle; }
    public void setReportingPositionTitle(String reportingPositionTitle) { this.reportingPositionTitle = reportingPositionTitle; }

    public BigDecimal getFte() { return fte; }
    public void setFte(BigDecimal fte) { this.fte = fte; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public Long getCurrentEmployeeId() { return currentEmployeeId; }
    public void setCurrentEmployeeId(Long currentEmployeeId) { this.currentEmployeeId = currentEmployeeId; }

    public String getCurrentEmployeeName() { return currentEmployeeName; }
    public void setCurrentEmployeeName(String currentEmployeeName) { this.currentEmployeeName = currentEmployeeName; }

    public Boolean getIsVacant() { return isVacant; }
    public void setIsVacant(Boolean isVacant) { this.isVacant = isVacant; }

    public Boolean getJobSharingAllowed() { return jobSharingAllowed; }
    public void setJobSharingAllowed(Boolean jobSharingAllowed) { this.jobSharingAllowed = jobSharingAllowed; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Long getOrgUnitId() { return orgUnitId; }
    public void setOrgUnitId(Long orgUnitId) { this.orgUnitId = orgUnitId; }

    public String getOrgUnitName() { return orgUnitName; }
    public void setOrgUnitName(String orgUnitName) { this.orgUnitName = orgUnitName; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
