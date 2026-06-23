package com.sapir.smartvacationplanner.dto.itinerary;
import java.time.LocalTime;
import com.sapir.smartvacationplanner.entity.enums.PointOfInterestCategory;

public class ScheduledActivityResponse {

    private Integer vacationDayActivityId;
    private String pointOfInterestName;
    private PointOfInterestCategory pointOfInterestCategory;
    private LocalTime plannedStartTime;
    private LocalTime plannedEndTime;
    private String placeName;
    private String notes;

    public ScheduledActivityResponse() {
    }

    public ScheduledActivityResponse(Integer vacationDayActivityId, String pointOfInterestName, PointOfInterestCategory pointOfInterestCategory, 
        LocalTime plannedStartTime, LocalTime plannedEndTime, String placeName, String notes) {
        this.vacationDayActivityId = vacationDayActivityId;
        this.pointOfInterestName = pointOfInterestName;
        this.pointOfInterestCategory = pointOfInterestCategory;
        this.plannedStartTime = plannedStartTime;
        this.plannedEndTime = plannedEndTime;
        this.placeName = placeName;
        this.notes = notes;
    } 
    
    public Integer getVacationDayActivityId() {
        return vacationDayActivityId;
    }
    public String getPointOfInterestName() {
        return pointOfInterestName;
    }
    public PointOfInterestCategory getPointOfInterestCategory() {
        return pointOfInterestCategory;
    }
    public LocalTime getPlannedStartTime() {
        return plannedStartTime;
    }
    public LocalTime getPlannedEndTime() {
        return plannedEndTime;
    }
    public String getPlaceName() {
        return placeName;
    }
    public String getNotes() {
        return notes;
    }
    public void setVacationDayActivityId(Integer vacationDayActivityId) {
        this.vacationDayActivityId = vacationDayActivityId;
    }
    public void setPointOfInterestName(String pointOfInterestName) {
        this.pointOfInterestName = pointOfInterestName;
    }
    public void setPointOfInterestCategory(PointOfInterestCategory pointOfInterestCategory) {
        this.pointOfInterestCategory = pointOfInterestCategory;
    }
    public void setPlannedStartTime(LocalTime plannedStartTime) {
        this.plannedStartTime = plannedStartTime;
    }
    public void setPlannedEndTime(LocalTime plannedEndTime) {
        this.plannedEndTime = plannedEndTime;
    }
    public void setPlaceName(String placeName) {
        this.placeName = placeName;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
    public String toString() {
        return "ScheduledActivityResponse{" +
            "vacationDayActivityId=" + vacationDayActivityId +
            ", pointOfInterestName=" + pointOfInterestName +
            ", pointOfInterestCategory=" + pointOfInterestCategory +
            ", plannedStartTime=" + plannedStartTime +
            ", plannedEndTime=" + plannedEndTime +
            ", placeName=" + placeName +
            ", notes=" + notes +
            '}';
    }
}