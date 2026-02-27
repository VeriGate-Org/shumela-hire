package com.arthmatic.shumelahire.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "shifts")
public class Shift extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 200)
    private String name;

    @NotNull
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @NotNull
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "break_duration_mins", nullable = false)
    private Integer breakDurationMins = 0;

    @Column(name = "grace_period_mins", nullable = false)
    private Integer gracePeriodMins = 0;

    @Column(name = "night_shift", nullable = false)
    private Boolean nightShift = false;

    @Column(length = 20)
    private String color;

    @Column(nullable = false)
    private Boolean active = true;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt;

    public long getScheduledMinutes() {
        long totalMins;
        if (nightShift != null && nightShift && endTime.isBefore(startTime)) {
            totalMins = java.time.Duration.between(startTime, endTime).toMinutes() + 1440;
        } else {
            totalMins = java.time.Duration.between(startTime, endTime).toMinutes();
        }
        return totalMins - (breakDurationMins != null ? breakDurationMins : 0);
    }

    public double getScheduledHours() {
        return getScheduledMinutes() / 60.0;
    }

    // Getters and Setters
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

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
