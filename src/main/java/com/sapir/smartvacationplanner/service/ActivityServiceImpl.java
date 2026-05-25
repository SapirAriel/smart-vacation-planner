package com.sapir.smartvacationplanner.service;
import org.springframework.stereotype.Service;
import com.sapir.smartvacationplanner.entity.Activity;
import com.sapir.smartvacationplanner.dto.activity.CreateActivityRequest;
import com.sapir.smartvacationplanner.dto.activity.UpdateActivityRequest;
import com.sapir.smartvacationplanner.dto.activity.PatchActivityRequest;
import com.sapir.smartvacationplanner.repository.ActivityRepository;
import java.util.List;
import com.sapir.smartvacationplanner.entity.VacationDay;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.sapir.smartvacationplanner.entity.enums.ActivityType;
import java.time.LocalTime;

/**
 * ActivityServiceImpl is a service implementation for the Activity entity.
 * It is used to perform CRUD operations on the Activity entity.
 */

@Service
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository activityRepository;
    private final AuthorizationService authorizationService;

    public ActivityServiceImpl(ActivityRepository activityRepository, AuthorizationService authorizationService) {
        this.activityRepository = activityRepository;
        this.authorizationService = authorizationService;
    }

    @Override
    public List<Activity> getAllActivities(Integer vacationId, Integer vacationDayId) {

        VacationDay vacationDay = authorizationService.getVacationDayForCurrentUser(vacationId, vacationDayId);
        return activityRepository.findByVacationDay(vacationDay);
    }

    @Override
    public Page<Activity> searchActivities(Integer vacationId, Integer vacationDayId, String name, ActivityType activityType, String location, Integer durationMinutes, LocalTime openingTime, LocalTime closingTime, Integer minimumAge, String notes, Pageable pageable) {
        
        VacationDay vacationDay = authorizationService.getVacationDayForCurrentUser(vacationId, vacationDayId);
        return activityRepository.searchActivities(vacationDay, name, activityType, location, durationMinutes, openingTime, closingTime, minimumAge, notes, pageable);
    }

    @Override
    public Activity getActivityById(Integer vacationId, Integer vacationDayId, Integer id) {
        return authorizationService.getActivityForCurrentUser(vacationId, vacationDayId, id);   
    }

    @Override
    public Activity createActivity(Integer vacationId, Integer vacationDayId, CreateActivityRequest request) {
        VacationDay vacationDay = authorizationService.getVacationDayForCurrentUser(vacationId, vacationDayId);
        Activity activity = new Activity(vacationDay, request.getName(), request.getActivityType(), request.getLocation(), request.getDurationMinutes(), request.getOpeningTime(), request.getClosingTime(), request.getMinimumAge(), request.getNotes());
        return activityRepository.save(activity);
    }

    @Override
    public Activity updateActivity(Integer vacationId, Integer vacationDayId, Integer id, UpdateActivityRequest request) {
        Activity existing = authorizationService.getActivityForCurrentUser(vacationId, vacationDayId, id);
        existing.setName(request.getName());
        existing.setActivityType(request.getActivityType());
        existing.setLocation(request.getLocation());  
        existing.setDurationMinutes(request.getDurationMinutes());
        existing.setOpeningTime(request.getOpeningTime());
        existing.setClosingTime(request.getClosingTime());
        existing.setMinimumAge(request.getMinimumAge()); 
        existing.setNotes(request.getNotes());
        return activityRepository.save(existing);
    }

    @Override
    public Activity patchActivity(Integer vacationId, Integer vacationDayId, Integer id, PatchActivityRequest request) {
        Activity existing = authorizationService.getActivityForCurrentUser(vacationId, vacationDayId, id);
        if (request.getName() != null) {
            existing.setName(request.getName());
        }
        if (request.getActivityType() != null) {
            existing.setActivityType(request.getActivityType());
        }
        if (request.getLocation() != null) {
            existing.setLocation(request.getLocation());
        }   
        if (request.getDurationMinutes() != null) {
            existing.setDurationMinutes(request.getDurationMinutes());
        }
        if (request.getOpeningTime() != null) {
            existing.setOpeningTime(request.getOpeningTime());
        }
        if (request.getClosingTime() != null) {
            existing.setClosingTime(request.getClosingTime());
        }
        if (request.getMinimumAge() != null) {
            existing.setMinimumAge(request.getMinimumAge());
        }
        if (request.getNotes() != null) {
            existing.setNotes(request.getNotes());
        }
        return activityRepository.save(existing);
    }

    @Override
    public void deleteActivity(Integer vacationId, Integer vacationDayId, Integer id) {
        Activity activity = authorizationService.getActivityForCurrentUser(vacationId, vacationDayId, id);
        activityRepository.delete(activity);
    }

    
}
