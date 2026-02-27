package com.arthmatic.shumelahire.dto.engagement;

import com.arthmatic.shumelahire.entity.engagement.MoodRating;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public class WellnessCheckInRequest {

    @NotNull(message = "Employee ID is required")
    private Long employeeId;

    @NotNull(message = "Mood rating is required")
    private MoodRating moodRating;

    @Min(1) @Max(10)
    private Integer energyLevel;

    @Min(1) @Max(10)
    private Integer stressLevel;

    private String notes;
    private Long wellnessProgramId;
    private LocalDate checkInDate;

    // Getters and Setters
    public Long getEmployeeId() { return employeeId; }
    public void setEmployeeId(Long employeeId) { this.employeeId = employeeId; }

    public MoodRating getMoodRating() { return moodRating; }
    public void setMoodRating(MoodRating moodRating) { this.moodRating = moodRating; }

    public Integer getEnergyLevel() { return energyLevel; }
    public void setEnergyLevel(Integer energyLevel) { this.energyLevel = energyLevel; }

    public Integer getStressLevel() { return stressLevel; }
    public void setStressLevel(Integer stressLevel) { this.stressLevel = stressLevel; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public Long getWellnessProgramId() { return wellnessProgramId; }
    public void setWellnessProgramId(Long wellnessProgramId) { this.wellnessProgramId = wellnessProgramId; }

    public LocalDate getCheckInDate() { return checkInDate; }
    public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }
}
