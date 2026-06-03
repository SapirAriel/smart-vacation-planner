package com.sapir.smartvacationplanner.entity;
import jakarta.persistence.*;
import com.sapir.smartvacationplanner.entity.enums.ActivityType;
import java.time.LocalTime;

/**
 * Activity entity represents an activity that a traveler can do on a vacation day.
 * It is a nested resource of VacationDay.
 * It is used to store the activity details such as name, type, location, duration, opening hours, minimum age, and notes.
 */

@Entity
@Table(name = "activities")
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "vacation_day_id", nullable = false)
    private VacationDay vacationDay;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "activity_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ActivityType activityType;

    @Column(name = "location", nullable = false)
    private String location;

    // Google Maps API place details

    @Column(name = "place_id")
    private String placeId;

    @Column(name = "formatted_address")
    private String formattedAddress;

    @Column(name = "latitude")
    private Double latitude;

    @Column(name = "longitude")
    private Double longitude;


    @Column(name = "duration_minutes", nullable = false)
    private int durationMinutes;

    @Column(name = "opening_time", nullable = false)
    private LocalTime openingTime;

    @Column(name = "closing_time", nullable = false)
    private LocalTime closingTime;

    @Column(name = "minimum_age", nullable = false)
    private int minimumAge;
    
    @Column(name = "notes", nullable = false)
    private String notes;

    public Activity() {
    }

    public Activity(VacationDay vacationDay, String name, ActivityType activityType, String location, 
        int durationMinutes, LocalTime openingTime, LocalTime closingTime, int minimumAge, String notes, String placeId, String formattedAddress, Double latitude, Double longitude) {

        this.vacationDay = vacationDay;
        this.name = name;
        this.activityType = activityType;
        this.location = location;
        this.durationMinutes = durationMinutes;
        this.openingTime = openingTime;
        this.closingTime = closingTime;
        this.minimumAge = minimumAge;
        this.notes = notes;
    }

    public Integer getId() {
        return id;
    }
    public VacationDay getVacationDay() {
        return vacationDay;
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

    public void setId(Integer id) {
        this.id = id;
    }
    public void setVacationDay(VacationDay vacationDay) {
        this.vacationDay = vacationDay;
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

    @Override
    public String toString() {
        return "Activity{" +
            "id=" + id +
            ", vacationDay=" + vacationDay +
            ", name=" + name +
            ", activityType=" + activityType +
            ", location=" + location +
            ", durationMinutes=" + durationMinutes +
            ", openingTime=" + openingTime +
            ", closingTime=" + closingTime +
            ", minimumAge=" + minimumAge +
            ", notes=" + notes +
            ", placeId=" + placeId +
            ", formattedAddress=" + formattedAddress +
            ", latitude=" + latitude +
            ", longitude=" + longitude +
            '}';
    }
}
