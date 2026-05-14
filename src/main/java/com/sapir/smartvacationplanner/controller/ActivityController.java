package com.sapir.smartvacationplanner.controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import com.sapir.smartvacationplanner.service.ActivityService;
import com.sapir.smartvacationplanner.dto.activity.ActivityResponse;
import com.sapir.smartvacationplanner.dto.activity.CreateActivityRequest;
import com.sapir.smartvacationplanner.dto.activity.UpdateActivityRequest;
import com.sapir.smartvacationplanner.dto.activity.PatchActivityRequest;
import jakarta.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import com.sapir.smartvacationplanner.entity.Activity;
import org.springframework.web.bind.annotation.PatchMapping;


@RestController
@RequestMapping("/api/v1/vacations/{vacationId}/days{vacationDayId}/activities")
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping
    public List<ActivityResponse> getAllActivities(@PathVariable Integer vacationDayId, @PathVariable Integer vacationId) {
        List<Activity> activities = activityService.getAllActivities(vacationId, vacationDayId);
        List<ActivityResponse> activityResponse = new ArrayList<>();
        for (Activity activity : activities) {
            activityResponse.add(toResponse(activity));
        }
        return activityResponse;
    }

    @GetMapping("/{id}")
    public ActivityResponse getActivityById(@PathVariable Integer vacationId, @PathVariable Integer vacationDayId, @PathVariable int id) {
        Activity activity = activityService.getActivityById(vacationId, vacationDayId, id);
        return toResponse(activity);
    }
    
    @PostMapping
    public ActivityResponse createActivity(@PathVariable Integer vacationId, @PathVariable Integer vacationDayId, @Valid @RequestBody CreateActivityRequest activityRequest) {
        return toResponse(activityService.createActivity(vacationId, vacationDayId, activityRequest));
    }
    
    @PutMapping("/{id}")
    public ActivityResponse updateActivity(@PathVariable Integer vacationId, @PathVariable Integer vacationDayId, @PathVariable int id, @Valid @RequestBody UpdateActivityRequest activityRequest) {
        return toResponse(activityService.updateActivity(vacationId, vacationDayId, id, activityRequest));
    }

    @PatchMapping("/{id}")
    public ActivityResponse patchActivity(@PathVariable Integer vacationId, @PathVariable Integer vacationDayId, @PathVariable int id, @Valid @RequestBody PatchActivityRequest activityRequest) {
        return toResponse(activityService.patchActivity(vacationId, vacationDayId, id, activityRequest));
    }
    
    @DeleteMapping("/{id}")
    public void deleteActivity(@PathVariable Integer vacationId, @PathVariable Integer vacationDayId, @PathVariable int id) {
        activityService.deleteActivity(vacationId, vacationDayId, id);
    }

    private ActivityResponse toResponse(Activity activity) {
        ActivityResponse activityResponse = new ActivityResponse();
        activityResponse.setId(activity.getId());
        activityResponse.setVacationDayId(activity.getVacationDay().getId());
        activityResponse.setName(activity.getName());
        activityResponse.setActivityType(activity.getActivityType());
        activityResponse.setLocation(activity.getLocation());
        activityResponse.setDurationMinutes(activity.getDurationMinutes());
        activityResponse.setOpeningHours(activity.getOpeningHours());
        activityResponse.setMinimumAge(activity.getMinimumAge());
        activityResponse.setNotes(activity.getNotes());
        return activityResponse;
    }
} //ActivityController
