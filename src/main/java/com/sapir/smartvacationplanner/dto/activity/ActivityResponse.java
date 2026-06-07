package com.sapir.smartvacationplanner.dto.activity;
import com.sapir.smartvacationplanner.entity.enums.ActivityType;
import java.time.LocalTime;
/**
 * ActivityResponse is a DTO for returning an activity.
 * It is used to return the activity details to the client.
 */

public class ActivityResponse {

    private int id;
    private int vacationDayId;
    private String name;
    private ActivityType activityType;
    private String placeName;
    private int durationMinutes;
    private LocalTime openingTime;
    private LocalTime closingTime;
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
    public String getPlaceName() {
        return placeName;
    }
    public int getDurationMinutes() {
        return durationMinutes;
    }
    public LocalTime getOpeningTime() {
        return openingTime;
    }
    public LocalTime getClosingTime() {
        return closingTime;
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
    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }
    public void setDurationMinutes(int durationMinutes) {
        this.durationMinutes = durationMinutes;
    }
    public void setOpeningTime(LocalTime openingTime) {
        this.openingTime = openingTime;
    }
    public void setClosingTime(LocalTime closingTime) {
        this.closingTime = closingTime;
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
            ", placeName=" + placeName +
            ", durationMinutes=" + durationMinutes +
            ", openingTime=" + openingTime +
            ", closingTime=" + closingTime +
            ", minimumAge=" + minimumAge +
            ", notes=" + notes +
            '}';
    }

} //ActivityResponse
