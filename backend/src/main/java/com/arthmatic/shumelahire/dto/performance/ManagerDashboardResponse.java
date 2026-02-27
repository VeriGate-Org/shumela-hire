package com.arthmatic.shumelahire.dto.performance;

import java.util.List;
import java.util.Map;

public class ManagerDashboardResponse {

    private int totalDirectReports;
    private int contractsCompleted;
    private int contractsPending;
    private int reviewsCompleted;
    private int reviewsPending;
    private int reviewsOverdue;
    private int activePIPs;
    private double averageTeamRating;
    private List<TeamMemberSummary> teamMembers;
    private Map<String, Integer> ratingDistribution;

    public static class TeamMemberSummary {
        private String employeeId;
        private String employeeName;
        private String department;
        private String contractStatus;
        private String reviewStatus;
        private Double selfRating;
        private Double managerRating;
        private Double finalRating;
        private boolean hasPIP;

        // Getters and Setters
        public String getEmployeeId() { return employeeId; }
        public void setEmployeeId(String employeeId) { this.employeeId = employeeId; }
        public String getEmployeeName() { return employeeName; }
        public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }
        public String getDepartment() { return department; }
        public void setDepartment(String department) { this.department = department; }
        public String getContractStatus() { return contractStatus; }
        public void setContractStatus(String contractStatus) { this.contractStatus = contractStatus; }
        public String getReviewStatus() { return reviewStatus; }
        public void setReviewStatus(String reviewStatus) { this.reviewStatus = reviewStatus; }
        public Double getSelfRating() { return selfRating; }
        public void setSelfRating(Double selfRating) { this.selfRating = selfRating; }
        public Double getManagerRating() { return managerRating; }
        public void setManagerRating(Double managerRating) { this.managerRating = managerRating; }
        public Double getFinalRating() { return finalRating; }
        public void setFinalRating(Double finalRating) { this.finalRating = finalRating; }
        public boolean isHasPIP() { return hasPIP; }
        public void setHasPIP(boolean hasPIP) { this.hasPIP = hasPIP; }
    }

    // Getters and Setters
    public int getTotalDirectReports() { return totalDirectReports; }
    public void setTotalDirectReports(int totalDirectReports) { this.totalDirectReports = totalDirectReports; }
    public int getContractsCompleted() { return contractsCompleted; }
    public void setContractsCompleted(int contractsCompleted) { this.contractsCompleted = contractsCompleted; }
    public int getContractsPending() { return contractsPending; }
    public void setContractsPending(int contractsPending) { this.contractsPending = contractsPending; }
    public int getReviewsCompleted() { return reviewsCompleted; }
    public void setReviewsCompleted(int reviewsCompleted) { this.reviewsCompleted = reviewsCompleted; }
    public int getReviewsPending() { return reviewsPending; }
    public void setReviewsPending(int reviewsPending) { this.reviewsPending = reviewsPending; }
    public int getReviewsOverdue() { return reviewsOverdue; }
    public void setReviewsOverdue(int reviewsOverdue) { this.reviewsOverdue = reviewsOverdue; }
    public int getActivePIPs() { return activePIPs; }
    public void setActivePIPs(int activePIPs) { this.activePIPs = activePIPs; }
    public double getAverageTeamRating() { return averageTeamRating; }
    public void setAverageTeamRating(double averageTeamRating) { this.averageTeamRating = averageTeamRating; }
    public List<TeamMemberSummary> getTeamMembers() { return teamMembers; }
    public void setTeamMembers(List<TeamMemberSummary> teamMembers) { this.teamMembers = teamMembers; }
    public Map<String, Integer> getRatingDistribution() { return ratingDistribution; }
    public void setRatingDistribution(Map<String, Integer> ratingDistribution) { this.ratingDistribution = ratingDistribution; }
}
