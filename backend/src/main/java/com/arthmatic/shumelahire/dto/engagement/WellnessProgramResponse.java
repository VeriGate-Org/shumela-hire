package com.arthmatic.shumelahire.dto.engagement;

import com.arthmatic.shumelahire.entity.engagement.WellnessCategory;
import com.arthmatic.shumelahire.entity.engagement.WellnessProgram;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class WellnessProgramResponse {

    private Long id;
    private String name;
    private String description;
    private WellnessCategory category;
    private LocalDate startDate;
    private LocalDate endDate;
    private Boolean isActive;
    private Integer maxParticipants;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static WellnessProgramResponse fromEntity(WellnessProgram program) {
        WellnessProgramResponse dto = new WellnessProgramResponse();
        dto.setId(program.getId());
        dto.setName(program.getName());
        dto.setDescription(program.getDescription());
        dto.setCategory(program.getCategory());
        dto.setStartDate(program.getStartDate());
        dto.setEndDate(program.getEndDate());
        dto.setIsActive(program.getIsActive());
        dto.setMaxParticipants(program.getMaxParticipants());
        dto.setCreatedAt(program.getCreatedAt());
        dto.setUpdatedAt(program.getUpdatedAt());
        return dto;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public WellnessCategory getCategory() { return category; }
    public void setCategory(WellnessCategory category) { this.category = category; }

    public LocalDate getStartDate() { return startDate; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }

    public LocalDate getEndDate() { return endDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public Integer getMaxParticipants() { return maxParticipants; }
    public void setMaxParticipants(Integer maxParticipants) { this.maxParticipants = maxParticipants; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
