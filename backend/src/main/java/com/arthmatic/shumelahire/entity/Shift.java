package com.arthmatic.shumelahire.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Entity
@Table(name = "shifts")
public class Shift extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 20)
    private String code;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @NotNull
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;

    @Column(name = "break_duration_minutes", nullable = false)
    private Integer breakDurationMinutes = 60;

    @Column(name = "total_hours", precision = 4, scale = 2)
    private BigDecimal totalHours;

    @Column(name = "grace_period_minutes", nullable = false)
    private Integer gracePeriodMinutes = 15;

    @Column(name = "is_overnight", nullable = false)
    private Boolean isOvernight = false;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(length = 50)
    private String color;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "geofence_id")
    private Geofence geofence;

    @Column(name = "min_hours_for_overtime", precision = 4, scale = 2)
    private BigDecimal minHoursForOvertime;

    @Column(name = "department", length = 200)
    private String department;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

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

    public BigDecimal getTotalHours() { return totalHours; }
    public void setTotalHours(BigDecimal totalHours) { this.totalHours = totalHours; }

    public Integer getGracePeriodMinutes() { return gracePeriodMinutes; }
    public void setGracePeriodMinutes(Integer gracePeriodMinutes) { this.gracePeriodMinutes = gracePeriodMinutes; }

    public Boolean getIsOvernight() { return isOvernight; }
    public void setIsOvernight(Boolean isOvernight) { this.isOvernight = isOvernight; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }

    public Geofence getGeofence() { return geofence; }
    public void setGeofence(Geofence geofence) { this.geofence = geofence; }

    public BigDecimal getMinHoursForOvertime() { return minHoursForOvertime; }
    public void setMinHoursForOvertime(BigDecimal minHoursForOvertime) { this.minHoursForOvertime = minHoursForOvertime; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
