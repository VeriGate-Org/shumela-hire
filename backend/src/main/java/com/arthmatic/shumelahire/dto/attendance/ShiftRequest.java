package com.arthmatic.shumelahire.dto.attendance;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.LocalTime;

public class ShiftRequest {

    @NotBlank
    private String name;
    @NotNull
    private LocalTime startTime;
    @NotNull
    private LocalTime endTime;
    private Integer breakDurationMins = 0;
    private Integer gracePeriodMins = 0;
    private Boolean nightShift = false;
    private String color;

    public ShiftRequest() {}

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public LocalTime getStartTime() { return startTime; }
    public void setStartTime(LocalTime startTime) { this.startTime = startTime; }

    public LocalTime getEndTime() { return endTime; }
    public void setEndTime(LocalTime endTime) { this.endTime = endTime; }

    public Integer getBreakDurationMins() { return breakDurationMins; }
    public void setBreakDurationMins(Integer breakDurationMins) { this.breakDurationMins = breakDurationMins; }

    public Integer getGracePeriodMins() { return gracePeriodMins; }
    public void setGracePeriodMins(Integer gracePeriodMins) { this.gracePeriodMins = gracePeriodMins; }

    public Boolean getNightShift() { return nightShift; }
    public void setNightShift(Boolean nightShift) { this.nightShift = nightShift; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
}
