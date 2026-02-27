package com.arthmatic.shumelahire.dto.engagement;

import com.arthmatic.shumelahire.entity.engagement.MoodRating;
import com.arthmatic.shumelahire.entity.engagement.WellnessCheckIn;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class WellnessCheckInResponse {

    private Long id;
    private Long employeeId;
    private String employeeName;
    private MoodRating moodRating;
    private Integer moodScore;
    private Integer energyLevel;
    private Integer stressLevel;
    private String notes;
    private Long wellnessProgramId;
    private String wellnessProgramName;
    private LocalDate checkInDate;
    private LocalDateTime createdAt;

    public static WellnessCheckInResponse fromEntity(WellnessCheckIn checkIn) {
        WellnessCheckInResponse dto = new WellnessCheckInResponse();
        dto.setId(checkIn.getId());
        if (checkIn.getEmployee() != null) {
            dto.setEmployeeId(checkIn.getEmployee().getId());
            dto.setEmployeeName(checkIn.getEmployee().getFullName());
        }
        dto.setMoodRating(checkIn.getMoodRating());
        dto.setMoodScore(checkIn.getMoodScore());
        dto.setEnergyLevel(checkIn.getEnergyLevel());
        dto.setStressLevel(checkIn.getStressLevel());
        dto.setNotes(checkIn.getNotes());
        if (checkIn.getWellnessProgram() != null) {
            dto.setWellnessProgramId(checkIn.getWellnessProgram().getId());
            dto.setWellnessProgramName(checkIn.getWellnessProgram().getName());
        }
        dto.setCheckInDate(checkIn.getCheckInDate());
        dto.setCreatedAt(checkIn.getCreatedAt());
        return dto;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public String getEmployeeName() { return employeeName; }
    public void setEmployeeName(String employeeName) { this.employeeName = employeeName; }

    public MoodRating getMoodRating() { return moodRating; }
    public void setMoodRating(MoodRating moodRating) { this.moodRating = moodRating; }

    public Integer getMoodScore() { return moodScore; }
    public void setMoodScore(Integer moodScore) { this.moodScore = moodScore; }

    public Integer getEnergyLevel() { return energyLevel; }
    public void setEnergyLevel(Integer energyLevel) { this.energyLevel = energyLevel; }

    public Integer getStressLevel() { return stressLevel; }
    public void setStressLevel(Integer stressLevel) { this.stressLevel = stressLevel; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Long getWellnessProgramId() { return wellnessProgramId; }
    public void setWellnessProgramId(Long wellnessProgramId) { this.wellnessProgramId = wellnessProgramId; }

    public String getWellnessProgramName() { return wellnessProgramName; }
    public void setWellnessProgramName(String wellnessProgramName) { this.wellnessProgramName = wellnessProgramName; }

    public LocalDate getCheckInDate() { return checkInDate; }
    public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
