package com.sapir.smartvacationplanner.dto.activity;
import com.sapir.smartvacationplanner.entity.enums.ActivityType;

/**
 * ActivityResponse is a DTO for returning an activity.
 * It is used to return the activity details to the client.
 */

public class ActivityResponse {

    private int id;
    private int vacationDayId;
    private String name;
    private ActivityType activityType;
    private String location;
    private int durationMinutes;
    private String openingHours;
    private int minimumAge;
    private String notes;

    public int getId() {
        return id;
    }
    public int getVacationDayId() {
        return vacationDayId;
    }
    public String getName() {
        return name;
    }
    public ActivityType getActivityType() {
        return activityType;
    }
    public String getLocation() {
        return location;
    }
    public int getDurationMinutes() {
        return durationMinutes;
    }
    public String getOpeningHours() {
        return openingHours;
    }
    public int getMinimumAge() {
        return minimumAge;
    }
    public String getNotes() {
        return notes;
    }

    public void setId(int id) {
        this.id = id;
    }
    public void setVacationDayId(int vacationDayId) {
        this.vacationDayId = vacationDayId;
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
    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }
    public void setMinimumAge(int minimumAge) {
        this.minimumAge = minimumAge;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String toString() {
        return "ActivityResponse{" +
            "id=" + id +
            ", vacationDayId=" + vacationDayId +
            ", name=" + name +
            ", activityType=" + activityType +
            ", location=" + location +
            ", durationMinutes=" + durationMinutes +
            ", openingHours=" + openingHours +
            ", minimumAge=" + minimumAge +
            ", notes=" + notes +
            '}';
    }

} //ActivityResponse
