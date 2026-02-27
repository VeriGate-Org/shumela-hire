package com.arthmatic.shumelahire.dto.attendance;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class ShiftPatternRequest {

    @NotBlank(message = "Name is required")
    private String name;

    private String description;

    @NotNull(message = "Rotation days is required")
    @Positive
    private Integer rotationDays;

    @NotBlank(message = "Pattern definition is required")
    private String patternDefinition;

    private Boolean isActive = true;

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getRotationDays() { return rotationDays; }
    public void setRotationDays(Integer rotationDays) { this.rotationDays = rotationDays; }

    public String getPatternDefinition() { return patternDefinition; }
    public void setPatternDefinition(String patternDefinition) { this.patternDefinition = patternDefinition; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }
}
