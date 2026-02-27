package com.arthmatic.shumelahire.dto;

import com.arthmatic.shumelahire.entity.PublicHoliday;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class PublicHolidayResponse {

    private Long id;
    private String name;
    private LocalDate holidayDate;
    private String description;
    private String country;
    private String region;
    private boolean recurring;
    private boolean active;
    private LocalDateTime createdAt;

    public PublicHolidayResponse(PublicHoliday holiday) {
        this.id = holiday.getId();
        this.name = holiday.getName();
        this.holidayDate = holiday.getHolidayDate();
        this.description = holiday.getDescription();
        this.country = holiday.getCountry();
        this.region = holiday.getRegion();
        this.recurring = holiday.isRecurring();
        this.active = holiday.isActive();
        this.createdAt = holiday.getCreatedAt();
    }

    public static PublicHolidayResponse fromEntity(PublicHoliday holiday) {
        return new PublicHolidayResponse(holiday);
    }

    // Getters
    public Long getId() { return id; }
    public String getName() { return name; }
    public LocalDate getHolidayDate() { return holidayDate; }
    public String getDescription() { return description; }
    public String getCountry() { return country; }
    public String getRegion() { return region; }
    public boolean isRecurring() { return recurring; }
    public boolean isActive() { return active; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
