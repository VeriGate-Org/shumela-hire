package com.arthmatic.shumelahire.entity.engagement;

import com.arthmatic.shumelahire.entity.Employee;
import com.arthmatic.shumelahire.entity.TenantAwareEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "wellness_check_ins")
public class WellnessCheckIn extends TenantAwareEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    @NotNull
    private Employee employee;

    @Enumerated(EnumType.STRING)
    @Column(name = "mood_rating", nullable = false, length = 20)
    @NotNull
    private MoodRating moodRating;

    @Min(1) @Max(10)
    @Column(name = "energy_level")
    private Integer energyLevel;

    @Min(1) @Max(10)
    @Column(name = "stress_level")
    private Integer stressLevel;

    @Column(name = "notes", columnDefinition = "TEXT")
    private String notes;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "wellness_program_id")
    private WellnessProgram wellnessProgram;

    @Column(name = "check_in_date", nullable = false)
    private LocalDate checkInDate = LocalDate.now();

    @CreationTimestamp
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    public WellnessCheckIn() {}

    public int getMoodScore() {
        return switch (moodRating) {
            case GREAT -> 5;
            case GOOD -> 4;
            case OKAY -> 3;
            case LOW -> 2;
            case STRUGGLING -> 1;
        };
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Employee getEmployee() { return employee; }
    public void setEmployee(Employee employee) { this.employee = employee; }

    public MoodRating getMoodRating() { return moodRating; }
    public void setMoodRating(MoodRating moodRating) { this.moodRating = moodRating; }

    public Integer getEnergyLevel() { return energyLevel; }
    public void setEnergyLevel(Integer energyLevel) { this.energyLevel = energyLevel; }

    public Integer getStressLevel() { return stressLevel; }
    public void setStressLevel(Integer stressLevel) { this.stressLevel = stressLevel; }

    public String getNotes() { return notes; }
    public void setNotes(String notes) { this.notes = notes; }

    public WellnessProgram getWellnessProgram() { return wellnessProgram; }
    public void setWellnessProgram(WellnessProgram wellnessProgram) { this.wellnessProgram = wellnessProgram; }

    public LocalDate getCheckInDate() { return checkInDate; }
    public void setCheckInDate(LocalDate checkInDate) { this.checkInDate = checkInDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
