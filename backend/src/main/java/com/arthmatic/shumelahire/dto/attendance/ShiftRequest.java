package com.arthmatic.shumelahire.dto.attendance;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalTime;

public class ShiftRequest {

    @NotBlank
    private String name;

    private String code;
    private String description;

    @NotNull
    private LocalTime startTime;

    @NotNull
    private LocalTime endTime;

    private Integer breakDurationMinutes;
    private Integer gracePeriodMinutes;
    private Boolean isOvernight;
    private String color;
    private Long geofenceId;
    private BigDecimal minHoursForOvertime;
    private String department;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public Integer getBreakDurationMinutes() { return breakDurationMinutes; }
    public void setBreakDurationMinutes(Integer breakDurationMinutes) { this.breakDurationMinutes = breakDurationMinutes; }

    public Integer getGracePeriodMinutes() { return gracePeriodMinutes; }
    public void setGracePeriodMinutes(Integer gracePeriodMinutes) { this.gracePeriodMinutes = gracePeriodMinutes; }

    public Boolean getIsOvernight() { return isOvernight; }
    public void setIsOvernight(Boolean isOvernight) { this.isOvernight = isOvernight; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public Long getGeofenceId() { return geofenceId; }
    public void setGeofenceId(Long geofenceId) { this.geofenceId = geofenceId; }

    public BigDecimal getMinHoursForOvertime() { return minHoursForOvertime; }
    public void setMinHoursForOvertime(BigDecimal minHoursForOvertime) { this.minHoursForOvertime = minHoursForOvertime; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }
}
