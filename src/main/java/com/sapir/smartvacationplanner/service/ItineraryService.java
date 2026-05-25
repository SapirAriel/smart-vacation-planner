package com.sapir.smartvacationplanner.service;
import com.sapir.smartvacationplanner.dto.itinerary.ItineraryResponse;
import com.sapir.smartvacationplanner.entity.Vacation;
import com.sapir.smartvacationplanner.entity.VacationDay;
import com.sapir.smartvacationplanner.entity.Activity;  
import java.util.List;
import java.time.LocalDate;

public interface ItineraryService {

    Vacation getVacationById(Integer vacationId);
    List<VacationDay> getVacationDays(Integer vacationId);
    VacationDay getVacationDayByDate(Integer vacationId, LocalDate date);
    List<Activity> getActivities(Integer vacationId, Integer vacationDayId);

    ItineraryResponse generateItinerary(Integer vacationId);
    
}
