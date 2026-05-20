package com.sapir.smartvacationplanner.dto.activity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.sapir.smartvacationplanner.entity.enums.ActivityType;
import jakarta.validation.constraints.Positive;

/**
 * UpdateActivityRequest is a DTO for updating an activity.
 * It is used to validate the request body for the update activity endpoint.
 */

public class UpdateActivityRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Activity type is required")
    private ActivityType activityType;

    @NotBlank(message = "Location is required")
    private String location;

    @Positive(message = "Duration minutes must be greater than 0")
    @NotNull(message = "Duration minutes is required")
    private Integer durationMinutes;

    @NotBlank(message = "Opening hours is required")
    private String openingHours;

    @NotNull(message = "Minimum age is required")
    private Integer minimumAge;

    private String notes;


    public String getName() {
        return name;
    }
    public ActivityType getActivityType() {
        return activityType;
    }
    public String getLocation() {
        return location;
    }
    public Integer getDurationMinutes() {
        return durationMinutes;
    }
    public String getOpeningHours() {
        return openingHours;
    }
    public Integer getMinimumAge() {
        return minimumAge;
    }
    public String getNotes() {
        return notes;
    }

    public void setName(String name) {
        this.name = name;
    }
    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }
    public void setMinimumAge(Integer minimumAge) {
        this.minimumAge = minimumAge;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
} //UpdateActivityRequest
