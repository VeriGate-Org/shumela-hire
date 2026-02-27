package com.arthmatic.shumelahire.dto.attendance;

import com.arthmatic.shumelahire.entity.Shift;

import java.time.LocalTime;

public class ShiftResponse {

    private Long id;
    private String name;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer breakDurationMins;
    private Integer gracePeriodMins;
    private Boolean nightShift;
    private String color;
    private Boolean active;
    private double scheduledHours;

    public ShiftResponse() {}

    public static ShiftResponse fromEntity(Shift s) {
        ShiftResponse r = new ShiftResponse();
        r.setId(s.getId());
        r.setName(s.getName());
        r.setStartTime(s.getStartTime());
        r.setEndTime(s.getEndTime());
        r.setBreakDurationMins(s.getBreakDurationMins());
        r.setGracePeriodMins(s.getGracePeriodMins());
        r.setNightShift(s.getNightShift());
        r.setColor(s.getColor());
        r.setActive(s.getActive());
        r.setScheduledHours(s.getScheduledHours());
        return r;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }

    public double getScheduledHours() { return scheduledHours; }
    public void setScheduledHours(double scheduledHours) { this.scheduledHours = scheduledHours; }
}
