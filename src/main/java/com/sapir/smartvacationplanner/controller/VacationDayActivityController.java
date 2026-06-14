package com.sapir.smartvacationplanner.controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import com.sapir.smartvacationplanner.entity.VacationDayActivity;
import com.sapir.smartvacationplanner.service.VacationDayActivityService;
import com.sapir.smartvacationplanner.dto.VacationDayActivity.VacationDayActivityResponse;
import java.util.List;


@RestController
@RequestMapping("/api/v1/vacations/{vacationId}/days/{vacationDayId}/vacationDayActivities")
public class VacationDayActivityController {

    private final VacationDayActivityService vacationDayActivityService;

    public VacationDayActivityController(VacationDayActivityService vacationDayActivityService) {
        this.vacationDayActivityService = vacationDayActivityService;
    }

    @GetMapping
    public List<VacationDayActivityResponse> getAllVacationDayActivities(@PathVariable Integer vacationDayId, @PathVariable Integer vacationId) {
        
        return vacationDayActivityService.getAllVacationDayActivities(vacationId, vacationDayId).stream().map(this::toResponse).toList();
        
    }

    @GetMapping("/{id}")
    public VacationDayActivityResponse getVacationDayActivityById(@PathVariable Integer vacationId, 
        @PathVariable Integer vacationDayId, @PathVariable int id) {
        VacationDayActivity vacationDayActivity = vacationDayActivityService.getVacationDayActivityById(vacationId, vacationDayId, id);
        return toResponse(vacationDayActivity);
    }
    
    @PostMapping("/{pointOfInterestId}")
    public VacationDayActivityResponse createVacationDayActivity(@PathVariable Integer vacationId, @PathVariable Integer vacationDayId, @PathVariable int pointOfInterestId) {
        
        return toResponse(vacationDayActivityService.createVacationDayActivity(vacationId, vacationDayId, pointOfInterestId));
    }
    
    @PutMapping("/{id}/{pointOfInterestId}")
    public VacationDayActivityResponse updateVacationDayActivity(@PathVariable Integer vacationId, @PathVariable Integer vacationDayId, @PathVariable int id, @PathVariable int pointOfInterestId) {
        
        return toResponse(vacationDayActivityService.updateVacationDayActivity(vacationId, vacationDayId, id, pointOfInterestId));
    }

    @DeleteMapping("/{id}")
    public void deleteVacationDayActivity(@PathVariable Integer vacationId, @PathVariable Integer vacationDayId, @PathVariable int id) {
        
        vacationDayActivityService.deleteVacationDayActivity(vacationId, vacationDayId, id);
    }

    private VacationDayActivityResponse toResponse(VacationDayActivity vacationDayActivity) {
        VacationDayActivityResponse vacationDayActivityResponse = new VacationDayActivityResponse();
        vacationDayActivityResponse.setId(vacationDayActivity.getId());
        vacationDayActivityResponse.setVacationDayId(vacationDayActivity.getVacationDay().getId());
        vacationDayActivityResponse.setPointOfInterestId(vacationDayActivity.getPointOfInterest().getId());
        return vacationDayActivityResponse;
    }
} //VacationDayActivityController