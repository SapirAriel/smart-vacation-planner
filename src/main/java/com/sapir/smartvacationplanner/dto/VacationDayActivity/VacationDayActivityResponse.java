package com.sapir.smartvacationplanner.dto.VacationDayActivity;
import java.time.LocalTime;

public class VacationDayActivityResponse {

    private int id;
    private int vacationDayId;
    private int pointOfInterestId;
    private String pointOfInterestName;
    private LocalTime plannedStartTime;
    private LocalTime plannedEndTime;
    private Integer travelMinutesFromPrevious;
    private Double distanceKmFromPrevious;

    public int getId() {
        return id;
    }
    public int getVacationDayId() {
        return vacationDayId;
    }
    public int getPointOfInterestId() {
        return pointOfInterestId;
    }
    public String getPointOfInterestName() {
        return pointOfInterestName;
    }
    public LocalTime getPlannedStartTime() {
        return plannedStartTime;
    }
    public LocalTime getPlannedEndTime() {
        return plannedEndTime;
    }
    public Integer getTravelMinutesFromPrevious() {
        return travelMinutesFromPrevious;
    }
    public Double getDistanceKmFromPrevious() {
        return distanceKmFromPrevious;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setVacationDayId(int vacationDayId) {
        this.vacationDayId = vacationDayId;
    }
    public void setPointOfInterestId(int pointOfInterestId) {
        this.pointOfInterestId = pointOfInterestId;
    }
    public void setPointOfInterestName(String pointOfInterestName) {
        this.pointOfInterestName = pointOfInterestName;
    }
    public void setPlannedStartTime(LocalTime plannedStartTime) {
        this.plannedStartTime = plannedStartTime;
    }
    public void setPlannedEndTime(LocalTime plannedEndTime) {
        this.plannedEndTime = plannedEndTime;
    }
    public void setTravelMinutesFromPrevious(Integer travelMinutesFromPrevious) {
        this.travelMinutesFromPrevious = travelMinutesFromPrevious;
    }
    public void setDistanceKmFromPrevious(Double distanceKmFromPrevious) {
        this.distanceKmFromPrevious = distanceKmFromPrevious;
    }
    public String toString() {
        return "VacationDayActivityResponse{" +
            "id=" + id +
            ", vacationDayId=" + vacationDayId +
            ", pointOfInterestId=" + pointOfInterestId +
            ", pointOfInterestName=" + pointOfInterestName +
            ", plannedStartTime=" + plannedStartTime +
            ", plannedEndTime=" + plannedEndTime +
            ", travelMinutesFromPrevious=" + travelMinutesFromPrevious +
            ", distanceKmFromPrevious=" + distanceKmFromPrevious +
            '}';
    }
}
