package com.sapir.smartvacationplanner.dto.activity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.sapir.smartvacationplanner.entity.enums.ActivityType;
import jakarta.validation.constraints.Positive;
import java.time.LocalTime;

/**
 * UpdateActivityRequest is a DTO for updating an activity.
 * It is used to validate the request body for the update activity endpoint.
 */

public class UpdateActivityRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Activity type is required")
    private ActivityType activityType;

    @NotBlank(message = "Place name is required")
    private String placeName;

    @Positive(message = "Duration minutes must be greater than 0")
    @NotNull(message = "Duration minutes is required")
    private Integer durationMinutes;

    @NotNull(message = "Opening time is required")
    private LocalTime openingTime;

    @NotNull(message = "Closing time is required")
    private LocalTime closingTime;

    @NotNull(message = "Minimum age is required")
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
} //UpdateActivityRequest
