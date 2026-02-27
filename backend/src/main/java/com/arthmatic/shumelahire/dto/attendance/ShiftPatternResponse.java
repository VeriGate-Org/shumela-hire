package com.arthmatic.shumelahire.dto.attendance;

import com.arthmatic.shumelahire.entity.ShiftPattern;

import java.time.LocalDateTime;

public class ShiftPatternResponse {

    private Long id;
    private String name;
    private String description;
    private Integer daysOn;
    private Integer daysOff;
    private Integer cycleLengthDays;
    private String patternDefinition;
    private Long defaultShiftId;
    private String defaultShiftName;
    private Boolean isActive;
    private String department;
    private LocalDateTime createdAt;

    public static ShiftPatternResponse fromEntity(ShiftPattern p) {
        ShiftPatternResponse r = new ShiftPatternResponse();
        r.id = p.getId();
        r.name = p.getName();
        r.description = p.getDescription();
        r.daysOn = p.getDaysOn();
        r.daysOff = p.getDaysOff();
        r.cycleLengthDays = p.getCycleLengthDays();
        r.patternDefinition = p.getPatternDefinition();
        r.defaultShiftId = p.getDefaultShift() != null ? p.getDefaultShift().getId() : null;
        r.defaultShiftName = p.getDefaultShift() != null ? p.getDefaultShift().getName() : null;
        r.isActive = p.getIsActive();
        r.department = p.getDepartment();
        r.createdAt = p.getCreatedAt();
        return r;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public Integer getDaysOn() { return daysOn; }
    public Integer getDaysOff() { return daysOff; }
    public Integer getCycleLengthDays() { return cycleLengthDays; }
    public String getPatternDefinition() { return patternDefinition; }
    public Long getDefaultShiftId() { return defaultShiftId; }
    public String getDefaultShiftName() { return defaultShiftName; }
    public Boolean getIsActive() { return isActive; }
    public String getDepartment() { return department; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
