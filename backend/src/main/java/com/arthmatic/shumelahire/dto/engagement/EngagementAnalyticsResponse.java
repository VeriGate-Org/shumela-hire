package com.arthmatic.shumelahire.dto.engagement;

import java.util.Map;

public class EngagementAnalyticsResponse {

    private Long activeSurveys;
    private Long totalSurveyResponses;
    private Double averageSurveyRating;
    private Long totalRecognitions;
    private Long totalRecognitionPoints;
    private Long wellnessCheckIns;
    private Double averageEnergyLevel;
    private Double averageStressLevel;
    private Map<String, Long> moodDistribution;
    private Map<String, Long> badgeDistribution;
    private Long totalSocialPosts;

    // Getters and Setters
    public Long getActiveSurveys() { return activeSurveys; }
    public void setActiveSurveys(Long activeSurveys) { this.activeSurveys = activeSurveys; }

    public Long getTotalSurveyResponses() { return totalSurveyResponses; }
    public void setTotalSurveyResponses(Long totalSurveyResponses) { this.totalSurveyResponses = totalSurveyResponses; }

    public Double getAverageSurveyRating() { return averageSurveyRating; }
    public void setAverageSurveyRating(Double averageSurveyRating) { this.averageSurveyRating = averageSurveyRating; }

    public Long getTotalRecognitions() { return totalRecognitions; }
    public void setTotalRecognitions(Long totalRecognitions) { this.totalRecognitions = totalRecognitions; }

    public Long getTotalRecognitionPoints() { return totalRecognitionPoints; }
    public void setTotalRecognitionPoints(Long totalRecognitionPoints) { this.totalRecognitionPoints = totalRecognitionPoints; }

    public Long getWellnessCheckIns() { return wellnessCheckIns; }
    public void setWellnessCheckIns(Long wellnessCheckIns) { this.wellnessCheckIns = wellnessCheckIns; }

    public Double getAverageEnergyLevel() { return averageEnergyLevel; }
    public void setAverageEnergyLevel(Double averageEnergyLevel) { this.averageEnergyLevel = averageEnergyLevel; }

    public Double getAverageStressLevel() { return averageStressLevel; }
    public void setAverageStressLevel(Double averageStressLevel) { this.averageStressLevel = averageStressLevel; }

    public Map<String, Long> getMoodDistribution() { return moodDistribution; }
    public void setMoodDistribution(Map<String, Long> moodDistribution) { this.moodDistribution = moodDistribution; }

    public Map<String, Long> getBadgeDistribution() { return badgeDistribution; }
    public void setBadgeDistribution(Map<String, Long> badgeDistribution) { this.badgeDistribution = badgeDistribution; }

    public Long getTotalSocialPosts() { return totalSocialPosts; }
    public void setTotalSocialPosts(Long totalSocialPosts) { this.totalSocialPosts = totalSocialPosts; }
}
