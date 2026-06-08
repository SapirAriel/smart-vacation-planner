package com.sapir.smartvacationplanner.service.itinerary;
import org.springframework.stereotype.Service;
import com.sapir.smartvacationplanner.entity.Vacation;
import com.sapir.smartvacationplanner.entity.VacationDay;
import com.sapir.smartvacationplanner.service.AuthorizationService;
import com.sapir.smartvacationplanner.entity.Activity;
import com.sapir.smartvacationplanner.dto.itinerary.ItineraryResponse;
import java.util.List;
import org.springframework.data.domain.Sort;
import java.util.ArrayList;
import com.sapir.smartvacationplanner.dto.itinerary.ScheduledActivityResponse;
import java.time.LocalTime;
import com.sapir.smartvacationplanner.dto.itinerary.DayScheduleResponse;
import java.time.LocalDate;
import com.sapir.smartvacationplanner.common.place.Place;
import java.util.Comparator;


@Service
public class ItineraryServiceImpl implements ItineraryService {

    private final AuthorizationService authorizationService;

    public ItineraryServiceImpl(AuthorizationService authorizationService) {
        this.authorizationService = authorizationService;
    }

    @Override
    public Vacation getVacationById(Integer vacationId) {
        return authorizationService.getVacationForCurrentUser(vacationId);
    }

    @Override
    public List<VacationDay> getVacationDays(Integer vacationId) {
        return authorizationService.getVacationDaysForCurrentUser(vacationId);
    }

    @Override
    public VacationDay getVacationDayByDate(Integer vacationId, LocalDate date) {
        return authorizationService.getVacationDayForCurrentUser(vacationId, date);
    }

    @Override
    public List<Activity> getActivities(Integer vacationId, Integer vacationDayId) {
        return authorizationService.getActivitiesForCurrentUser(vacationId, vacationDayId, Sort.by(
                Sort.Order.asc("openingTime"),
                Sort.Order.desc("closingTime"),
                Sort.Order.asc("durationMinutes")));
    }

    @Override
    public ItineraryResponse generateItinerary(Integer vacationId) {

        Vacation vacation = getVacationById(vacationId);

        ItineraryResponse itineraryResponse = new ItineraryResponse();
        itineraryResponse.setVacationId(vacation.getId());
        itineraryResponse.setVacationName(vacation.getName());

        List<VacationDay> vacationDays = getVacationDays(vacationId);

        List<DayScheduleResponse> days = new ArrayList<>();
        itineraryResponse.setDays(days);

        for (VacationDay vacationDay : vacationDays) {
            DayScheduleResponse dayScheduleResponse = buildDaySchedule(vacationDay);
            days.add(dayScheduleResponse);
        }

        return itineraryResponse;
    }

    private double calculateDistanceKm(Place fromPlace, Place toPlace) {
        // One degree of latitude is approximately 111 kilometers.
        final double kmPerLatitudeDegree = 111.0;
    
        // Calculate the difference between the two latitudes in degrees.
        double latitudeDifference = fromPlace.getLatitude() - toPlace.getLatitude();
    
        // Calculate the difference between the two longitudes in degrees.
        double longitudeDifference = fromPlace.getLongitude() - toPlace.getLongitude();
    
        // Longitude degrees become "shorter" as we move away from the equator.
        // We use the average latitude of the two places to estimate that correction.
        double averageLatitude = (fromPlace.getLatitude() + toPlace.getLatitude()) / 2.0;
    
        // Convert latitude difference from degrees to kilometers.
        double latitudeDistanceKm = latitudeDifference * kmPerLatitudeDegree;
    
        // Convert longitude difference from degrees to kilometers.
        // Math.cos expects radians, so we convert the average latitude to radians.
        double longitudeDistanceKm =
                longitudeDifference * kmPerLatitudeDegree * Math.cos(Math.toRadians(averageLatitude));
    
        // Use Pythagoras after converting both directions to kilometers.
        return Math.sqrt(Math.pow(latitudeDistanceKm, 2) + Math.pow(longitudeDistanceKm, 2));
    }

    private int calculateTravelMinutes(double distanceKm) {
        int walkingMinutes = calculateWalkingMinutes(distanceKm);
    
        if (walkingMinutes <= 30) {
            return walkingMinutes;
        }
    
        return calculateDrivingMinutes(distanceKm);
    }

    private int calculateWalkingMinutes(double distanceKm) {
        final double walkingSpeedKmPerHour = 5.0;
        final int minimumWalkingMinutes = 5;
    
        int estimatedMinutes = (int) Math.ceil((distanceKm / walkingSpeedKmPerHour) * 60);
    
        return Math.max(estimatedMinutes, minimumWalkingMinutes);
    }

    private int calculateDrivingMinutes(double distanceKm) {
        final double drivingSpeedKmPerHour = 30.0;
        final int minimumDrivingMinutes = 10;
    
        int estimatedMinutes = (int) Math.ceil((distanceKm / drivingSpeedKmPerHour) * 60);
    
        return Math.max(estimatedMinutes, minimumDrivingMinutes);
    }

    private boolean canScheduleActivity(ScheduleCandidate scheduleCandidate, LocalTime dayEndTime,
        LocalTime openingTime, LocalTime closingTime) {
        return !scheduleCandidate.possibleStartTime().isBefore(openingTime) && 
            !scheduleCandidate.possibleEndTime().isAfter(closingTime) &&
            !scheduleCandidate.possibleEndTime().isAfter(dayEndTime);
    }


    private List<ScheduleCandidate> buildSchedulableCandidates(Place currentPlace, LocalTime currentTime, 
        List<Activity> unscheduledActivities,LocalTime dayEndTime) {

            List<ScheduleCandidate> schedulableCandidates = new ArrayList<>();

            for (Activity activity : unscheduledActivities) {
                
                double distanceKm = calculateDistanceKm(currentPlace, activity.getPlace());
                int travelMinutes = calculateTravelMinutes(distanceKm);
                LocalTime possibleStartTime = currentTime.plusMinutes(travelMinutes);
                LocalTime possibleEndTime = possibleStartTime.plusMinutes(activity.getDurationMinutes());

                ScheduleCandidate scheduleCandidate = new ScheduleCandidate(activity, distanceKm, travelMinutes, possibleStartTime, possibleEndTime);
            
                if (canScheduleActivity(scheduleCandidate, dayEndTime, activity.getOpeningTime(), activity.getClosingTime())) {
                    schedulableCandidates.add(scheduleCandidate);
                }
            }
            return schedulableCandidates;
        }

    private DayScheduleResponse buildDaySchedule(VacationDay vacationDay) {
        DayScheduleResponse dayScheduleResponse = new DayScheduleResponse();
        dayScheduleResponse.setVacationDayId(vacationDay.getId());
        dayScheduleResponse.setDayNumber(vacationDay.getDayNumber());
        dayScheduleResponse.setDate(vacationDay.getDate());
        Place currentPlace = vacationDay.getHotelPlace();
        
        LocalTime currentTime = LocalTime.of(9, 0);
        LocalTime dayEndTime = LocalTime.of(18, 0);

        List<Activity> unscheduledActivities = getActivities(vacationDay.getVacation().getId(), vacationDay.getId());
        
        List<ScheduledActivityResponse> scheduledActivities = buildScheduledActivities
        (currentPlace, currentTime, unscheduledActivities, dayEndTime);
        
        dayScheduleResponse.setActivities(scheduledActivities);
        return dayScheduleResponse;

    }


    private List<ScheduledActivityResponse> buildScheduledActivities(Place currentPlace, LocalTime currentTime,
        List<Activity> unscheduledActivities, LocalTime dayEndTime) {

        List<ScheduledActivityResponse> scheduledActivities = new ArrayList<>();

        while (!unscheduledActivities.isEmpty() && currentTime.isBefore(dayEndTime)) {
            
            List<ScheduleCandidate> schedulableCandidates = buildSchedulableCandidates(currentPlace, currentTime, unscheduledActivities, dayEndTime);

            if (schedulableCandidates.isEmpty()) {
                    currentTime = currentTime.plusMinutes(15);
                    continue;
                }

            ScheduleCandidate selectedCandidate  = chooseNearestCandidate(schedulableCandidates);

            ScheduledActivityResponse scheduled = new ScheduledActivityResponse(
                selectedCandidate.getActivity().getId(),
                selectedCandidate.getActivity().getName(),
                selectedCandidate.getActivity().getActivityType(),
                selectedCandidate.getPossibleStartTime(),
                selectedCandidate.getPossibleEndTime(),
                selectedCandidate.getActivity().getPlace().getPlaceName());
                
            scheduledActivities.add(scheduled);

            unscheduledActivities.remove(selectedCandidate.getActivity());
            currentTime = selectedCandidate.getPossibleEndTime().plusMinutes(15);
            currentPlace = selectedCandidate.getActivity().getPlace();
        }

        return scheduledActivities;
    }

    private ScheduleCandidate chooseNearestCandidate(List<ScheduleCandidate> schedulableCandidates) {
        return schedulableCandidates.stream()
            .min(Comparator.comparingDouble(ScheduleCandidate::getDistanceFromCurrentPlace))
              .orElseThrow(() -> new IllegalStateException("No schedulable candidates found"));
    }

    }
