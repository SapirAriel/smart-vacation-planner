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
import java.util.List;
import com.sapir.smartvacationplanner.entity.Activity;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestParam;
import com.sapir.smartvacationplanner.entity.enums.ActivityType;
import java.time.LocalTime;


@RestController
@RequestMapping("/api/v1/vacations/{vacationId}/days/{vacationDayId}/activities")
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping
    public List<ActivityResponse> getAllActivities(@PathVariable Integer vacationDayId, @PathVariable Integer vacationId) {
        
        return activityService.getAllActivities(vacationId, vacationDayId).stream().map(this::toResponse).toList();
        
    }

    @GetMapping("/page")
    public Page<ActivityResponse> searchActivities(@RequestParam(required = false) String name,
        @RequestParam(required = false) ActivityType activityType,
        @RequestParam(required = false) String location,
        @RequestParam(required = false) Integer durationMinutes,
        @RequestParam(required = false) LocalTime openingTime,
        @RequestParam(required = false) LocalTime closingTime,
        @RequestParam(required = false) Integer minimumAge,
        @RequestParam(required = false) String notes,
        @PathVariable Integer vacationDayId, @PathVariable Integer vacationId, 
        @PageableDefault(page = 0, size = 5, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        
        return activityService.searchActivities(vacationId, vacationDayId, name, activityType, location, durationMinutes, openingTime, closingTime, minimumAge, notes, pageable).map(this::toResponse);
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
        activityResponse.setOpeningTime(activity.getOpeningTime());
        activityResponse.setClosingTime(activity.getClosingTime());
        activityResponse.setMinimumAge(activity.getMinimumAge());
        activityResponse.setNotes(activity.getNotes());
        return activityResponse;
    }
} //ActivityController
