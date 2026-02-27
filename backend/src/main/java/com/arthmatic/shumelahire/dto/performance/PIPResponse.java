package com.arthmatic.shumelahire.dto.performance;

import com.arthmatic.shumelahire.entity.performance.PerformanceImprovementPlan;
import com.arthmatic.shumelahire.entity.performance.PIPMilestone;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class PIPResponse {

    private Long id;
    private Long contractId;
    private String employeeId;
    private String employeeName;
    private String managerId;
    private String managerName;
    private String reason;
    private String performanceGaps;
    private String expectedImprovements;
    private String supportProvided;
    private LocalDate startDate;
    private LocalDate endDate;
    private LocalDate originalEndDate;
    private String extensionReason;
    private String status;
    private String outcomeNotes;
    private LocalDateTime completedAt;
    private String completedBy;
    private LocalDateTime createdAt;
    private String createdBy;
    private boolean overdue;
    private List<MilestoneResponse> milestones;

    public static PIPResponse fromEntity(PerformanceImprovementPlan pip) {
        PIPResponse response = new PIPResponse();
        response.setId(pip.getId());
        response.setContractId(pip.getContract() != null ? pip.getContract().getId() : null);
        response.setEmployeeId(pip.getEmployeeId());
        response.setEmployeeName(pip.getEmployeeName());
        response.setManagerId(pip.getManagerId());
        response.setManagerName(pip.getManagerName());
        response.setReason(pip.getReason());
        response.setPerformanceGaps(pip.getPerformanceGaps());
        response.setExpectedImprovements(pip.getExpectedImprovements());
        response.setSupportProvided(pip.getSupportProvided());
        response.setStartDate(pip.getStartDate());
        response.setEndDate(pip.getEndDate());
        response.setOriginalEndDate(pip.getOriginalEndDate());
        response.setExtensionReason(pip.getExtensionReason());
        response.setStatus(pip.getStatus().name());
        response.setOutcomeNotes(pip.getOutcomeNotes());
        response.setCompletedAt(pip.getCompletedAt());
        response.setCompletedBy(pip.getCompletedBy());
        response.setCreatedAt(pip.getCreatedAt());
        response.setCreatedBy(pip.getCreatedBy());
        response.setOverdue(pip.isOverdue());

        if (pip.getMilestones() != null) {
            response.setMilestones(pip.getMilestones().stream()
                    .map(MilestoneResponse::fromEntity)
                    .collect(Collectors.toList()));
        } else {
            response.setMilestones(Collections.emptyList());
        }

        return response;
    }

    public static class MilestoneResponse {
        private Long id;
        private String title;
        private String description;
        private String successCriteria;
        private LocalDate targetDate;
        private LocalDate completedDate;
        private String status;
        private String managerNotes;
        private String employeeNotes;
        private boolean overdue;

        public static MilestoneResponse fromEntity(PIPMilestone milestone) {
            MilestoneResponse resp = new MilestoneResponse();
            resp.setId(milestone.getId());
            resp.setTitle(milestone.getTitle());
            resp.setDescription(milestone.getDescription());
            resp.setSuccessCriteria(milestone.getSuccessCriteria());
            resp.setTargetDate(milestone.getTargetDate());
            resp.setCompletedDate(milestone.getCompletedDate());
            resp.setStatus(milestone.getStatus().name());
            resp.setManagerNotes(milestone.getManagerNotes());
            resp.setEmployeeNotes(milestone.getEmployeeNotes());
            resp.setOverdue(milestone.isOverdue());
            return resp;
        }

        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getTitle() { return title; }
        public void setTitle(String title) { this.title = title; }
        public String getDescription() { return description; }
        public void setDescription(String description) { this.description = description; }
        public String getSuccessCriteria() { return successCriteria; }
        public void setSuccessCriteria(String successCriteria) { this.successCriteria = successCriteria; }
        public LocalDate getTargetDate() { return targetDate; }
        public void setTargetDate(LocalDate targetDate) { this.targetDate = targetDate; }
        public LocalDate getCompletedDate() { return completedDate; }
        public void setCompletedDate(LocalDate completedDate) { this.completedDate = completedDate; }
        public String getStatus() { return status; }
        public void setStatus(String status) { this.status = status; }
        public String getManagerNotes() { return managerNotes; }
        public void setManagerNotes(String managerNotes) { this.managerNotes = managerNotes; }
        public String getEmployeeNotes() { return employeeNotes; }
        public void setEmployeeNotes(String employeeNotes) { this.employeeNotes = employeeNotes; }
        public boolean isOverdue() { return overdue; }
        public void setOverdue(boolean overdue) { this.overdue = overdue; }
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
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
    public LocalDate getOriginalEndDate() { return originalEndDate; }
    public void setOriginalEndDate(LocalDate originalEndDate) { this.originalEndDate = originalEndDate; }
    public String getExtensionReason() { return extensionReason; }
    public void setExtensionReason(String extensionReason) { this.extensionReason = extensionReason; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getOutcomeNotes() { return outcomeNotes; }
    public void setOutcomeNotes(String outcomeNotes) { this.outcomeNotes = outcomeNotes; }
    public LocalDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(LocalDateTime completedAt) { this.completedAt = completedAt; }
    public String getCompletedBy() { return completedBy; }
    public void setCompletedBy(String completedBy) { this.completedBy = completedBy; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
    public String getCreatedBy() { return createdBy; }
    public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    public boolean isOverdue() { return overdue; }
    public void setOverdue(boolean overdue) { this.overdue = overdue; }
    public List<MilestoneResponse> getMilestones() { return milestones; }
    public void setMilestones(List<MilestoneResponse> milestones) { this.milestones = milestones; }
}
