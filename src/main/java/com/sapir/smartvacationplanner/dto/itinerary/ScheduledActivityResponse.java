package com.sapir.smartvacationplanner.dto.itinerary;
import java.time.LocalTime;
import com.sapir.smartvacationplanner.entity.enums.ActivityType;

public class ScheduledActivityResponse {

    private Integer activityId;
    private String activityName;
    private ActivityType activityType;
    private LocalTime plannedStartTime;
    private LocalTime plannedEndTime;
    private String location;

    public ScheduledActivityResponse() {
    }

    public ScheduledActivityResponse(Integer activityId, String activityName, ActivityType activityType, LocalTime plannedStartTime, LocalTime plannedEndTime, String location) {
        this.activityId = activityId;
        this.activityName = activityName;
        this.activityType = activityType;
        this.plannedStartTime = plannedStartTime;
        this.plannedEndTime = plannedEndTime;
        this.location = location;
    } 
    
    public Integer getActivityId() {
        return activityId;
    }
    public String getActivityName() {
        return activityName;
    }
    public ActivityType getActivityType() {
        return activityType;
    }
    public LocalTime getPlannedStartTime() {
        return plannedStartTime;
    }
    public LocalTime getPlannedEndTime() {
        return plannedEndTime;
    }
    public String getLocation() {
        return location;
    }
    
    public void setActivityId(Integer activityId) {
        this.activityId = activityId;
    }
    public void setActivityName(String activityName) {
        this.activityName = activityName;
    }
    public void setActivityType(ActivityType activityType) {
        this.activityType = activityType;
    }
    public void setPlannedStartTime(LocalTime plannedStartTime) {
        this.plannedStartTime = plannedStartTime;
    }
    public void setPlannedEndTime(LocalTime plannedEndTime) {
        this.plannedEndTime = plannedEndTime;
    }
    public void setLocation(String location) {
        this.location = location;
    }
    
    public String toString() {
        return "ScheduledActivityResponse{" +
            "activityId=" + activityId +
            ", activityName=" + activityName +
            ", activityType=" + activityType +
            ", plannedStartTime=" + plannedStartTime +
            ", plannedEndTime=" + plannedEndTime +
            ", location=" + location +
            '}';
    }
}