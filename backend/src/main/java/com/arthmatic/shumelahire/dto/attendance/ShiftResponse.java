package com.arthmatic.shumelahire.dto.attendance;

import com.arthmatic.shumelahire.entity.attendance.Shift;

import java.time.LocalDateTime;

public class ShiftResponse {

    private Long id;
    private String name;
    private String code;
    private String startTime;
    private String endTime;
    private Integer breakDurationMinutes;
    private Integer gracePeriodMinutes;
    private Boolean isNightShift;
    private Boolean isActive;
    private String color;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static ShiftResponse fromEntity(Shift shift) {
        ShiftResponse response = new ShiftResponse();
        response.setId(shift.getId());
        response.setName(shift.getName());
        response.setCode(shift.getCode());
        response.setStartTime(shift.getStartTime() != null ? shift.getStartTime().toString() : null);
        response.setEndTime(shift.getEndTime() != null ? shift.getEndTime().toString() : null);
        response.setBreakDurationMinutes(shift.getBreakDurationMinutes());
        response.setGracePeriodMinutes(shift.getGracePeriodMinutes());
        response.setIsNightShift(shift.getIsNightShift());
        response.setIsActive(shift.getIsActive());
        response.setColor(shift.getColor());
        response.setCreatedAt(shift.getCreatedAt());
        response.setUpdatedAt(shift.getUpdatedAt());
        return response;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getStartTime() { return startTime; }
    public void setStartTime(String startTime) { this.startTime = startTime; }

    public String getEndTime() { return endTime; }
    public void setEndTime(String endTime) { this.endTime = endTime; }

    public Integer getBreakDurationMinutes() { return breakDurationMinutes; }
    public void setBreakDurationMinutes(Integer breakDurationMinutes) { this.breakDurationMinutes = breakDurationMinutes; }

    public Integer getGracePeriodMinutes() { return gracePeriodMinutes; }
    public void setGracePeriodMinutes(Integer gracePeriodMinutes) { this.gracePeriodMinutes = gracePeriodMinutes; }

    public Boolean getIsNightShift() { return isNightShift; }
    public void setIsNightShift(Boolean isNightShift) { this.isNightShift = isNightShift; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
