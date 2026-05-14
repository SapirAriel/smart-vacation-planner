package com.sapir.smartvacationplanner.entity;
import jakarta.persistence.*;
import com.sapir.smartvacationplanner.entity.enums.ActivityType;

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
    private int id;

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

    @Column(name = "duration_minutes", nullable = false)
    private int durationMinutes;

    @Column(name = "opening_hours", nullable = false)
    private String openingHours;

    @Column(name = "minimum_age", nullable = false)
    private int minimumAge;
    
    @Column(name = "notes", nullable = false)
    private String notes;

    public Activity() {
    }

    public Activity(VacationDay vacationDay, String name, ActivityType activityType, String location, 
        int durationMinutes, String openingHours, int minimumAge, String notes) {

        this.vacationDay = vacationDay;
        this.name = name;
        this.activityType = activityType;
        this.location = location;
        this.durationMinutes = durationMinutes;
        this.openingHours = openingHours;
        this.minimumAge = minimumAge;
        this.notes = notes;
    }

    public int getId() {
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
    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }
    public void setMinimumAge(int minimumAge) {
        this.minimumAge = minimumAge;
    }
    public void setNotes(String notes) {
        this.notes = notes;
    }
}
