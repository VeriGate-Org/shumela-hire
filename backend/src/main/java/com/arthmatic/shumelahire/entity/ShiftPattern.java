package com.arthmatic.shumelahire.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "shift_patterns")
public class ShiftPattern extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Column(nullable = false, length = 100)
    private String name;

    @Column(columnDefinition = "TEXT")
    private String description;

    @NotNull
    @Column(name = "days_on", nullable = false)
    private Integer daysOn;

    @NotNull
    @Column(name = "days_off", nullable = false)
    private Integer daysOff;

    @NotNull
    @Column(name = "cycle_length_days", nullable = false)
    private Integer cycleLengthDays;

    @Column(name = "pattern_definition", columnDefinition = "TEXT")
    private String patternDefinition;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "default_shift_id")
    private Shift defaultShift;

    @Column(name = "is_active", nullable = false)
    private Boolean isActive = true;

    @Column(length = 200)
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

    public Shift getDefaultShift() { return defaultShift; }
    public void setDefaultShift(Shift defaultShift) { this.defaultShift = defaultShift; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public String getDepartment() { return department; }
    public void setDepartment(String department) { this.department = department; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}
