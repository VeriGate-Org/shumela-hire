package com.arthmatic.shumelahire.dto.performance;

import java.util.Map;

public class PerformanceAnalyticsResponse {

    private CycleAnalytics cycleAnalytics;
    private Map<String, Integer> ratingDistribution;
    private Map<String, Double> departmentAverages;
    private CompletionMetrics completionMetrics;
    private PIPAnalytics pipAnalytics;

    public static class CycleAnalytics {
        private Long cycleId;
        private String cycleName;
        private int totalContracts;
        private int approvedContracts;
        private int pendingContracts;
        private int totalReviews;
        private int completedReviews;
        private double contractCompletionRate;
        private double reviewCompletionRate;

        // Getters and Setters
        public Long getCycleId() { return cycleId; }
        public void setCycleId(Long cycleId) { this.cycleId = cycleId; }
        public String getCycleName() { return cycleName; }
        public void setCycleName(String cycleName) { this.cycleName = cycleName; }
        public int getTotalContracts() { return totalContracts; }
        public void setTotalContracts(int totalContracts) { this.totalContracts = totalContracts; }
        public int getApprovedContracts() { return approvedContracts; }
        public void setApprovedContracts(int approvedContracts) { this.approvedContracts = approvedContracts; }
        public int getPendingContracts() { return pendingContracts; }
        public void setPendingContracts(int pendingContracts) { this.pendingContracts = pendingContracts; }
        public int getTotalReviews() { return totalReviews; }
        public void setTotalReviews(int totalReviews) { this.totalReviews = totalReviews; }
        public int getCompletedReviews() { return completedReviews; }
        public void setCompletedReviews(int completedReviews) { this.completedReviews = completedReviews; }
        public double getContractCompletionRate() { return contractCompletionRate; }
        public void setContractCompletionRate(double contractCompletionRate) { this.contractCompletionRate = contractCompletionRate; }
        public double getReviewCompletionRate() { return reviewCompletionRate; }
        public void setReviewCompletionRate(double reviewCompletionRate) { this.reviewCompletionRate = reviewCompletionRate; }
    }

    public static class CompletionMetrics {
        private int totalEmployees;
        private int selfAssessmentsSubmitted;
        private int managerAssessmentsSubmitted;
        private int calibrationsCompleted;
        private double selfAssessmentRate;
        private double managerAssessmentRate;

        // Getters and Setters
        public int getTotalEmployees() { return totalEmployees; }
        public void setTotalEmployees(int totalEmployees) { this.totalEmployees = totalEmployees; }
        public int getSelfAssessmentsSubmitted() { return selfAssessmentsSubmitted; }
        public void setSelfAssessmentsSubmitted(int selfAssessmentsSubmitted) { this.selfAssessmentsSubmitted = selfAssessmentsSubmitted; }
        public int getManagerAssessmentsSubmitted() { return managerAssessmentsSubmitted; }
        public void setManagerAssessmentsSubmitted(int managerAssessmentsSubmitted) { this.managerAssessmentsSubmitted = managerAssessmentsSubmitted; }
        public int getCalibrationsCompleted() { return calibrationsCompleted; }
        public void setCalibrationsCompleted(int calibrationsCompleted) { this.calibrationsCompleted = calibrationsCompleted; }
        public double getSelfAssessmentRate() { return selfAssessmentRate; }
        public void setSelfAssessmentRate(double selfAssessmentRate) { this.selfAssessmentRate = selfAssessmentRate; }
        public double getManagerAssessmentRate() { return managerAssessmentRate; }
        public void setManagerAssessmentRate(double managerAssessmentRate) { this.managerAssessmentRate = managerAssessmentRate; }
    }

    public static class PIPAnalytics {
        private int totalActive;
        private int completedSuccessfully;
        private int completedUnsuccessfully;
        private int terminated;
        private int overdue;
        private double successRate;

        // Getters and Setters
        public int getTotalActive() { return totalActive; }
        public void setTotalActive(int totalActive) { this.totalActive = totalActive; }
        public int getCompletedSuccessfully() { return completedSuccessfully; }
        public void setCompletedSuccessfully(int completedSuccessfully) { this.completedSuccessfully = completedSuccessfully; }
        public int getCompletedUnsuccessfully() { return completedUnsuccessfully; }
        public void setCompletedUnsuccessfully(int completedUnsuccessfully) { this.completedUnsuccessfully = completedUnsuccessfully; }
        public int getTerminated() { return terminated; }
        public void setTerminated(int terminated) { this.terminated = terminated; }
        public int getOverdue() { return overdue; }
        public void setOverdue(int overdue) { this.overdue = overdue; }
        public double getSuccessRate() { return successRate; }
        public void setSuccessRate(double successRate) { this.successRate = successRate; }
    }

    // Getters and Setters
    public CycleAnalytics getCycleAnalytics() { return cycleAnalytics; }
    public void setCycleAnalytics(CycleAnalytics cycleAnalytics) { this.cycleAnalytics = cycleAnalytics; }
    public Map<String, Integer> getRatingDistribution() { return ratingDistribution; }
    public void setRatingDistribution(Map<String, Integer> ratingDistribution) { this.ratingDistribution = ratingDistribution; }
    public Map<String, Double> getDepartmentAverages() { return departmentAverages; }
    public void setDepartmentAverages(Map<String, Double> departmentAverages) { this.departmentAverages = departmentAverages; }
    public CompletionMetrics getCompletionMetrics() { return completionMetrics; }
    public void setCompletionMetrics(CompletionMetrics completionMetrics) { this.completionMetrics = completionMetrics; }
    public PIPAnalytics getPipAnalytics() { return pipAnalytics; }
    public void setPipAnalytics(PIPAnalytics pipAnalytics) { this.pipAnalytics = pipAnalytics; }
}
