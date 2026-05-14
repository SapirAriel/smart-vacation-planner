package com.sapir.smartvacationplanner.service;
import org.springframework.stereotype.Service;
import com.sapir.smartvacationplanner.entity.Activity;
import com.sapir.smartvacationplanner.dto.activity.CreateActivityRequest;
import com.sapir.smartvacationplanner.dto.activity.UpdateActivityRequest;
import com.sapir.smartvacationplanner.dto.activity.PatchActivityRequest;
import com.sapir.smartvacationplanner.repository.ActivityRepository;
import com.sapir.smartvacationplanner.exception.ResourceNotFoundException;
import com.sapir.smartvacationplanner.repository.VacationDayRepository;
import java.util.List;
import com.sapir.smartvacationplanner.entity.VacationDay;
import com.sapir.smartvacationplanner.repository.VacationRepository;

/**
 * ActivityServiceImpl is a service implementation for the Activity entity.
 * It is used to perform CRUD operations on the Activity entity.
 */

@Service
public class ActivityServiceImpl implements ActivityService {

    private final ActivityRepository activityRepository;
    private final VacationDayRepository vacationDayRepository;
    private final VacationRepository vacationRepository;

    public ActivityServiceImpl(ActivityRepository activityRepository, VacationDayRepository vacationDayRepository, VacationRepository vacationRepository) {
        this.activityRepository = activityRepository;
        this.vacationDayRepository = vacationDayRepository;
        this.vacationRepository = vacationRepository;
    }

    @Override
    public List<Activity> getAllActivities(Integer vacationId, Integer vacationDayId) {
        vacationRepository.findById(vacationId).orElseThrow(() 
        -> new ResourceNotFoundException("Vacation not found with id: " + vacationId));
        VacationDay vacationDay = vacationDayRepository.findById(vacationDayId).orElseThrow(() 
        -> new ResourceNotFoundException("Vacation day not found with id: " + vacationDayId));
        return activityRepository.findByVacationDay(vacationDay);
    }

    @Override
    public Activity getActivityById(Integer vacationId, Integer vacationDayId, Integer id) {
        vacationRepository.findById(vacationId).orElseThrow(() 
        -> new ResourceNotFoundException("Vacation not found with id: " + vacationId));
        vacationDayRepository.findById(vacationDayId).orElseThrow(() 
        -> new ResourceNotFoundException("Vacation day not found with id: " + vacationDayId));
        Activity activity = activityRepository.findById(id).orElseThrow(() 
        -> new ResourceNotFoundException("Activity not found with id: " + id));
        if (activity.getVacationDay().getId() != vacationDayId) {
            throw new IllegalArgumentException("Activity not found with id: " + id);
        }
        return activity;
    }

    @Override
    public Activity createActivity(Integer vacationId, Integer vacationDayId, CreateActivityRequest request) {
        vacationRepository.findById(vacationId).orElseThrow(() 
        -> new ResourceNotFoundException("Vacation not found with id: " + vacationId));
        VacationDay vacationDay = vacationDayRepository.findById(vacationDayId).orElseThrow(() 
        -> new ResourceNotFoundException("Vacation day not found with id: " + vacationDayId));
        Activity activity = new Activity();
        activity.setVacationDay(vacationDay);
        activity.setName(request.getName());
        activity.setActivityType(request.getActivityType());
        activity.setLocation(request.getLocation());
        activity.setDurationMinutes(request.getDurationMinutes());
        activity.setOpeningHours(request.getOpeningHours());
        activity.setMinimumAge(request.getMinimumAge());
        activity.setNotes(request.getNotes());
        return activityRepository.save(activity);
    }

    @Override
    public Activity updateActivity(Integer vacationId, Integer vacationDayId, Integer id, UpdateActivityRequest request) {
        vacationRepository.findById(vacationId).orElseThrow(() 
        -> new ResourceNotFoundException("Vacation not found with id: " + vacationId));
        vacationDayRepository.findById(vacationDayId).orElseThrow(() 
        -> new ResourceNotFoundException("Vacation day not found with id: " + vacationDayId));
        Activity existing = getActivityById(vacationId, vacationDayId, id);
        existing.setName(request.getName());
        existing.setActivityType(request.getActivityType());
        existing.setLocation(request.getLocation());
        existing.setDurationMinutes(request.getDurationMinutes());
        existing.setOpeningHours(request.getOpeningHours());
        existing.setMinimumAge(request.getMinimumAge());
        existing.setNotes(request.getNotes());
        return activityRepository.save(existing);
    }

    @Override
    public Activity patchActivity(Integer vacationId, Integer vacationDayId, Integer id, PatchActivityRequest request) {
        vacationRepository.findById(vacationId).orElseThrow(() 
        -> new ResourceNotFoundException("Vacation not found with id: " + vacationId));
        vacationDayRepository.findById(vacationDayId).orElseThrow(() 
        -> new ResourceNotFoundException("Vacation day not found with id: " + vacationDayId));
        Activity existing = getActivityById(vacationId, vacationDayId, id);
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
        if (request.getOpeningHours() != null) {
        existing.setOpeningHours(request.getOpeningHours());
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
        vacationRepository.findById(vacationId).orElseThrow(() 
        -> new ResourceNotFoundException("Vacation not found with id: " + vacationId));
        vacationDayRepository.findById(vacationDayId).orElseThrow(() 
        -> new ResourceNotFoundException("Vacation day not found with id: " + vacationDayId));
        Activity existing = getActivityById(vacationId, vacationDayId, id);
        activityRepository.delete(existing);
    }

    
}
