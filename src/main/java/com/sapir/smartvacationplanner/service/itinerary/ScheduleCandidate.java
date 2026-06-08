package com.sapir.smartvacationplanner.service.itinerary;
import com.sapir.smartvacationplanner.entity.Activity;
import java.time.LocalTime;

public record ScheduleCandidate(
    Activity activity,
    double distanceFromCurrentPlace,
    int estimatedTravelMinutes,
    LocalTime possibleStartTime,
    LocalTime possibleEndTime
) {

    public Activity getActivity() {
        return activity;
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