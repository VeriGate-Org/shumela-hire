package com.arthmatic.shumelahire.dto.performance;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;
import java.util.List;

public class PIPRequest {

    @NotNull(message = "Contract ID is required")
    private Long contractId;

    @NotBlank(message = "Employee ID is required")
    private String employeeId;

    private String employeeName;

    @NotBlank(message = "Manager ID is required")
    private String managerId;

    private String managerName;

    @NotBlank(message = "Reason is required")
    private String reason;

    private String performanceGaps;
    private String expectedImprovements;
    private String supportProvided;

    @NotNull(message = "Start date is required")
    private LocalDate startDate;

    @NotNull(message = "End date is required")
    private LocalDate endDate;

    private List<PIPMilestoneRequest> milestones;

    // Getters and Setters
    public Long getContractId() { return contractId; }
    public void setContractId(Long contractId) { this.contractId = contractId; }

    public String getEmployeeId() { return employeeId; }
    public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public String getManagerId() { return managerId; }
    public void setManagerId(String managerId) { this.managerId = managerId; }

    public String getManagerName() { return managerName; }
    public void setManagerName(String managerName) { this.managerName = managerName; }

    public String getReason() { return reason; }
    public void setReason(String reason) { this.reason = reason; }

    public String getPerformanceGaps() { return performanceGaps; }
    public void setPerformanceGaps(String performanceGaps) { this.performanceGaps = performanceGaps; }

    public String getExpectedImprovements() { return expectedImprovements; }
    public void setExpectedImprovements(String expectedImprovements) { this.expectedImprovements = expectedImprovements; }

    public String getSupportProvided() { return supportProvided; }
    public void setSupportProvided(String supportProvided) { this.supportProvided = supportProvided; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public List<PIPMilestoneRequest> getMilestones() { return milestones; }
    public void setMilestones(List<PIPMilestoneRequest> milestones) { this.milestones = milestones; }
}
