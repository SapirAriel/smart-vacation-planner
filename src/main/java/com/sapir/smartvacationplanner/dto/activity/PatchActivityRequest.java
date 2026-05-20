package com.sapir.smartvacationplanner.dto.activity;
import com.sapir.smartvacationplanner.entity.enums.ActivityType;
import jakarta.validation.constraints.Positive;

/**
 * PatchActivityRequest is a DTO for patching an activity.
 * It is used to validate the request body for the create activity endpoint.
 */

public class PatchActivityRequest {

    private String name;

    private ActivityType activityType;

    private String location;

    @Positive(message = "Duration minutes must be greater than 0")
    private Integer durationMinutes;

    private String openingHours;

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
} //PatchActivityRequest
