package com.sapir.smartvacationplanner.service;
import org.springframework.stereotype.Service;
import com.sapir.smartvacationplanner.entity.Vacation;
import com.sapir.smartvacationplanner.entity.VacationDay;
import com.sapir.smartvacationplanner.entity.Activity;
import com.sapir.smartvacationplanner.dto.itinerary.ItineraryResponse;
import java.util.List;
import org.springframework.data.domain.Sort;
import java.util.ArrayList;
import com.sapir.smartvacationplanner.dto.itinerary.ScheduledActivityResponse;
import java.time.LocalTime;
import com.sapir.smartvacationplanner.dto.itinerary.DayScheduleResponse;
import java.time.LocalDate;


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
        List<DayScheduleResponse> days = new ArrayList<>();
        itineraryResponse.setDays(days);

        LocalDate currentDate = vacation.getStartDate();
        int dayNumber = 1;

        while (!currentDate.isAfter(vacation.getEndDate())) {
            DayScheduleResponse dayScheduleResponse = new DayScheduleResponse();
            dayScheduleResponse.setDayNumber(dayNumber);
            dayScheduleResponse.setDate(currentDate);
            VacationDay vacationDay = getVacationDayByDate(vacationId, currentDate);

            List<Activity> activities = getActivities(vacationId, vacationDay.getId());

            List<ScheduledActivityResponse> scheduledActivities = new ArrayList<>();
            LocalTime currentTime = LocalTime.of(9, 0);

            for (Activity activity : activities) {
                LocalTime possibleStart = currentTime;

                if (possibleStart.isBefore(activity.getOpeningTime())) {
                    possibleStart = activity.getOpeningTime();
                }

                LocalTime possibleEnd = possibleStart.plusMinutes(activity.getDurationMinutes());

                if (possibleEnd.isAfter(activity.getClosingTime())) {
                    continue; // cannot fit the activity in this day
                }

                ScheduledActivityResponse scheduled = new ScheduledActivityResponse(
                    activity.getId(),
                    activity.getName(),
                    activity.getActivityType(),
                    possibleStart,
                    possibleEnd,
                    activity.getPlace().getPlaceName());

                scheduledActivities.add(scheduled);

                currentTime = possibleEnd.plusMinutes(30); // buffer
            }//end of for loop

            dayScheduleResponse.setActivities(scheduledActivities);
            itineraryResponse.addDay(dayScheduleResponse);

            currentDate = currentDate.plusDays(1);
            dayNumber++;

        }//end of while loop

            return itineraryResponse;
    }

    }
