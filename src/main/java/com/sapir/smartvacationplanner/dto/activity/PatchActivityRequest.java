package com.sapir.smartvacationplanner.dto.activity;
import com.sapir.smartvacationplanner.entity.enums.ActivityType;
import jakarta.validation.constraints.Positive;
import java.time.LocalTime;

/**
 * PatchActivityRequest is a DTO for patching an activity.
 * It is used to validate the request body for the create activity endpoint.
 */

public class PatchActivityRequest {

    private String name;

    private ActivityType activityType;

    private String placeName;

    @Positive(message = "Duration minutes must be greater than 0")
    private Integer durationMinutes;

    private LocalTime openingTime;

    private LocalTime closingTime;

    private Integer minimumAge;

    private String notes;


    public String getName() {
        return name;
    }
    public ActivityType getActivityType() {
        return activityType;
    }
    public String getPlaceName() {
        return placeName;
    }
    public Integer getDurationMinutes() {
        return durationMinutes;
    }
    public LocalTime getOpeningTime() {
        return openingTime;
    }
    public LocalTime getClosingTime() {
        return closingTime;
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
    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }
    public void setDurationMinutes(Integer durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
    public void setOpeningTime(LocalTime openingTime) {
        this.openingTime = openingTime;
    }
    public void setClosingTime(LocalTime closingTime) {
        this.closingTime = closingTime;
    }
    public void setMinimumAge(Integer minimumAge) {
        this.minimumAge = minimumAge;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
} //PatchActivityRequest
