package com.arthmatic.shumelahire.dto.attendance;

import com.arthmatic.shumelahire.entity.Shift;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

public class ShiftResponse {

    private Long id;
    private String name;
    private String code;
    private String description;
    private LocalTime startTime;
    private LocalTime endTime;
    private Integer breakDurationMinutes;
    private BigDecimal totalHours;
    private Integer gracePeriodMinutes;
    private Boolean isOvernight;
    private Boolean isActive;
    private String color;
    private Long geofenceId;
    private String geofenceName;
    private BigDecimal minHoursForOvertime;
    private String department;
    private LocalDateTime createdAt;

    public static ShiftResponse fromEntity(Shift s) {
        ShiftResponse r = new ShiftResponse();
        r.id = s.getId();
        r.name = s.getName();
        r.code = s.getCode();
        r.description = s.getDescription();
        r.startTime = s.getStartTime();
        r.endTime = s.getEndTime();
        r.breakDurationMinutes = s.getBreakDurationMinutes();
        r.totalHours = s.getTotalHours();
        r.gracePeriodMinutes = s.getGracePeriodMinutes();
        r.isOvernight = s.getIsOvernight();
        r.isActive = s.getIsActive();
        r.color = s.getColor();
        r.geofenceId = s.getGeofence() != null ? s.getGeofence().getId() : null;
        r.geofenceName = s.getGeofence() != null ? s.getGeofence().getName() : null;
        r.minHoursForOvertime = s.getMinHoursForOvertime();
        r.department = s.getDepartment();
        r.createdAt = s.getCreatedAt();
        return r;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public String getCode() { return code; }
    public String getDescription() { return description; }
    public LocalTime getStartTime() { return startTime; }
    public LocalTime getEndTime() { return endTime; }
    public Integer getBreakDurationMinutes() { return breakDurationMinutes; }
    public BigDecimal getTotalHours() { return totalHours; }
    public Integer getGracePeriodMinutes() { return gracePeriodMinutes; }
    public Boolean getIsOvernight() { return isOvernight; }
    public Boolean getIsActive() { return isActive; }
    public String getColor() { return color; }
    public Long getGeofenceId() { return geofenceId; }
    public String getGeofenceName() { return geofenceName; }
    public BigDecimal getMinHoursForOvertime() { return minHoursForOvertime; }
    public String getDepartment() { return department; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
