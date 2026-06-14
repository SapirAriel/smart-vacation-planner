package com.sapir.smartvacationplanner.service.itinerary;
import com.sapir.smartvacationplanner.entity.VacationDayActivity;
import java.time.LocalTime;

public record ScheduleCandidate(
    VacationDayActivity vacationDayActivity,
    double distanceFromCurrentPlace,
    int estimatedTravelMinutes,
    LocalTime possibleStartTime,
    LocalTime possibleEndTime
) {

    public VacationDayActivity getVacationDayActivity() {
        return vacationDayActivity;
    }
    public double getDistanceFromCurrentPlace() {
        return distanceFromCurrentPlace;
    }
    public int getEstimatedTravelMinutes() {
        return estimatedTravelMinutes;
    }
    public LocalTime getPossibleStartTime() {
        return possibleStartTime;
    }
    public LocalTime getPossibleEndTime() {
        return possibleEndTime;
    }
}