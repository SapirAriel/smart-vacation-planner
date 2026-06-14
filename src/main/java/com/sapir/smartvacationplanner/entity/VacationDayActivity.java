package com.sapir.smartvacationplanner.entity;
import jakarta.persistence.*;
import java.time.LocalTime;

/**
 * VacationDayActivity entity represents an activity on a vacation day.
 * It is a nested resource of VacationDay.
 * It is used to store the activity details such as point of interest, planned start time, planned end time, travel minutes from previous, and distance km from previous.
 */

@Entity
@Table(name = "vacation_day_activities")
public class VacationDayActivity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vacation_day_id", nullable = false)
    private VacationDay vacationDay;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "point_of_interest_id", nullable = false)
    private PointOfInterest pointOfInterest;

    @Column(name = "planned_start_time")
    private LocalTime plannedStartTime;
    @Column(name = "planned_end_time")
    private LocalTime plannedEndTime;
    @Column(name = "travel_minutes_from_previous")
    private Integer travelMinutesFromPrevious;
    @Column(name = "distance_km_from_previous")
    private Double distanceKmFromPrevious;

    public VacationDayActivity() {
    }

    public VacationDayActivity(VacationDay vacationDay, PointOfInterest pointOfInterest, LocalTime plannedStartTime, LocalTime plannedEndTime, Integer travelMinutesFromPrevious, Double distanceKmFromPrevious) {
        this.vacationDay = vacationDay;
        this.pointOfInterest = pointOfInterest;
        this.plannedStartTime = plannedStartTime;
        this.plannedEndTime = plannedEndTime;
        this.travelMinutesFromPrevious = travelMinutesFromPrevious;
        this.distanceKmFromPrevious = distanceKmFromPrevious;
    }

    public VacationDayActivity(VacationDay vacationDay, PointOfInterest pointOfInterest) {
        this.vacationDay = vacationDay;
        this.pointOfInterest = pointOfInterest;
    }
    
    public Integer getId() {
        return id;
    }
    public VacationDay getVacationDay() {
        return vacationDay;
    }
    public PointOfInterest getPointOfInterest() {
        return pointOfInterest;
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
    public void setId(Integer id) {
        this.id = id;
    }
    public void setVacationDay(VacationDay vacationDay) {
        this.vacationDay = vacationDay;
    }
    public void setPointOfInterest(PointOfInterest pointOfInterest) {
        this.pointOfInterest = pointOfInterest;
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
        return "VacationDayActivity{" +
            "id=" + id +
            ", vacationDay=" + vacationDay +
            ", pointOfInterest=" + pointOfInterest +
            ", plannedStartTime=" + plannedStartTime +
            ", plannedEndTime=" + plannedEndTime +
            ", travelMinutesFromPrevious=" + travelMinutesFromPrevious +
            ", distanceKmFromPrevious=" + distanceKmFromPrevious +
            '}';
    }
}
    

