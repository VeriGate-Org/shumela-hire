package com.arthmatic.shumelahire.dto.attendance;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ShiftPatternRequest {

    @NotBlank
    private String name;

    private String description;

    @NotNull
    private Integer daysOn;

    @NotNull
    private Integer daysOff;

    @NotNull
    private Integer cycleLengthDays;

    private String patternDefinition;
    private Long defaultShiftId;
    private String department;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Integer getDaysOn() { return daysOn; }
    public void setDaysOn(Integer daysOn) { this.daysOn = daysOn; }

    public Integer getDaysOff() { return daysOff; }
    public void setDaysOff(Integer daysOff) { this.daysOff = daysOff; }

    public Integer getCycleLengthDays() { return cycleLengthDays; }
    public void setCycleLengthDays(Integer cycleLengthDays) { this.cycleLengthDays = cycleLengthDays; }

    public String getPatternDefinition() { return patternDefinition; }
    public void setPatternDefinition(String patternDefinition) { this.patternDefinition = patternDefinition; }

    public Long getDefaultShiftId() { return defaultShiftId; }
    public void setDefaultShiftId(Long defaultShiftId) { this.defaultShiftId = defaultShiftId; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
}
