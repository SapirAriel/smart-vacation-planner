package com.sapir.smartvacationplanner.service;
import java.util.List;
import com.sapir.smartvacationplanner.entity.Activity;
import com.sapir.smartvacationplanner.dto.activity.CreateActivityRequest;
import com.sapir.smartvacationplanner.dto.activity.UpdateActivityRequest;
import com.sapir.smartvacationplanner.dto.activity.PatchActivityRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.sapir.smartvacationplanner.entity.enums.ActivityType;
import java.time.LocalTime;

/**
 * ActivityService is a service interface for the Activity entity.
 * It is used to perform CRUD operations on the Activity entity.
 */

public interface ActivityService {

    List<Activity> getAllActivities(Integer vacationId, Integer vacationDayId);

    Page<Activity> searchActivities(Integer vacationId, Integer vacationDayId, String name, ActivityType activityType, String placeName, Integer durationMinutes, LocalTime openingTime, LocalTime closingTime, Integer minimumAge, String notes, Pageable pageable);

    Activity getActivityById(Integer vacationId, Integer vacationDayId, Integer id);
    
    Activity createActivity(Integer vacationId,Integer vacationDayId, CreateActivityRequest request);
    
    Activity updateActivity(Integer vacationId, Integer vacationDayId, Integer id, UpdateActivityRequest request);
    
    Activity patchActivity(Integer vacationId, Integer vacationDayId, Integer id, PatchActivityRequest request);
    
    void deleteActivity(Integer vacationId, Integer vacationDayId, Integer id);
    
}
