package com.sapir.smartvacationplanner.dto.PointOfInterest;
import com.sapir.smartvacationplanner.entity.enums.PointOfInterestCategory;
import java.time.LocalTime;
/**
 * PointOfInterestResponse is a DTO for returning a point of interest.
 * It is used to return the point of interest details to the client.
 */

public class PointOfInterestResponse {

    private int id;
    private String name;
    private PointOfInterestCategory pointOfInterestCategory;
    private String placeName;
    private int durationMinutes;
    private LocalTime openingTime;
    private LocalTime closingTime;
    private int minimumAge;
    private String notes;

    public int getId() {
        return id;
    }
    public String getName() {
        return name;
    }
    public PointOfInterestCategory getPointOfInterestCategory() {
        return pointOfInterestCategory;
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
    public void setName(String name) {
        this.name = name;
    }
    public void setPointOfInterestCategory(PointOfInterestCategory pointOfInterestCategory) {
        this.pointOfInterestCategory = pointOfInterestCategory;
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
        return "PointOfInterestResponse{" +
            "id=" + id +
            ", name=" + name +
            ", pointOfInterestCategory=" + pointOfInterestCategory +
            ", placeName=" + placeName +
            ", durationMinutes=" + durationMinutes +
            ", openingTime=" + openingTime +
            ", closingTime=" + closingTime +
            ", minimumAge=" + minimumAge +
            ", notes=" + notes +
            '}';
    }

} //ActivityResponse
