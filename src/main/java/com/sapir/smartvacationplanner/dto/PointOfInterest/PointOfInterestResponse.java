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

    private String placeId;
    private String formattedAddress;
    private Double latitude;
    private Double longitude;
    private String city;
    private String country;
    
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

    public String getPlaceId() {
        return placeId;
    }
    public String getFormattedAddress() {
        return formattedAddress;
    }
    public Double getLatitude() {
        return latitude;
    }
    public Double getLongitude() {
        return longitude;
    }
    public String getCity() {
        return city;
    }
    public String getCountry() {
        return country;
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
    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }
    public void setFormattedAddress(String formattedAddress) {
        this.formattedAddress = formattedAddress;
    }
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public void setCountry(String country) {
        this.country = country;
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
            ", placeId=" + placeId +
            ", formattedAddress=" + formattedAddress +
            ", latitude=" + latitude +
            ", longitude=" + longitude +
            ", city=" + city +
            ", country=" + country +
            ", durationMinutes=" + durationMinutes +
            ", openingTime=" + openingTime +
            ", closingTime=" + closingTime +
            ", minimumAge=" + minimumAge +
            ", notes=" + notes +
            '}';
    }

} //ActivityResponse
