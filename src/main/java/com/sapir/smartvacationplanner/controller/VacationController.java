package com.sapir.smartvacationplanner.controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import com.sapir.smartvacationplanner.entity.Vacation;
import com.sapir.smartvacationplanner.service.VacationService;
import com.sapir.smartvacationplanner.dto.vacation.CreateVacationRequest;
import com.sapir.smartvacationplanner.dto.vacation.UpdateVacationRequest;
import com.sapir.smartvacationplanner.dto.vacation.PatchVacationRequest;
import com.sapir.smartvacationplanner.dto.vacation.VacationResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import java.time.LocalDate;
import com.sapir.smartvacationplanner.entity.enums.TravelerType;
import com.sapir.smartvacationplanner.entity.enums.Pace;
import java.math.BigDecimal;

/**
 * VacationController is a controller for the Vacation entity.
 * It is used to perform CRUD operations on the Vacation entity.
 */

@RestController
@RequestMapping("/api/v1/vacations")
public class VacationController {
    
    private final VacationService vacationService;

public VacationController(VacationService vacationService) {
    this.vacationService = vacationService;
}

    @GetMapping
    
    public List<VacationResponse> getAllVacations() {
        return vacationService.getAllVacations().stream().map(this::toResponse).toList();

    }

    @GetMapping("/page")
    
    public Page<VacationResponse> searchVacations(@RequestParam(required = false) String country,
        @RequestParam(required = false) String city,
        @RequestParam(required = false) LocalDate startDate,
        @RequestParam(required = false) LocalDate endDate,
        @RequestParam(required = false) TravelerType travelerType,
        @RequestParam(required = false) BigDecimal budget,
        @RequestParam(required = false) Pace pace,
        @PageableDefault(page = 0, size = 5, sort = "startDate", direction = Sort.Direction.DESC) Pageable pageable) {
        return vacationService.searchVacations(country, city, startDate, endDate, travelerType, budget, pace, pageable).map(this::toResponse);

    }
    

    @GetMapping("/{id}")
    public VacationResponse getVacationById(@PathVariable int id) {
        
        Vacation vacation = vacationService.getVacationById(id);
        return toResponse(vacation);

    }

    @PostMapping
    public VacationResponse createVacation(@Valid @RequestBody CreateVacationRequest vacationRequest) {
        
        Vacation vacation = new Vacation ();
        
        vacation.setName(vacationRequest.getName());
        vacation.setCountry(vacationRequest.getCountry());
        vacation.setCity(vacationRequest.getCity());
        vacation.setStartDate(vacationRequest.getStartDate());
        vacation.setEndDate(vacationRequest.getEndDate());
        vacation.setTravelerType(vacationRequest.getTravelerType());
        vacation.setBudget(vacationRequest.getBudget());
        vacation.setPace(vacationRequest.getPace());

        Vacation savedVacation = vacationService.createVacation(vacation);
        return toResponse(savedVacation);
    }

    @PutMapping("/{id}")
    public VacationResponse updateVacation(@PathVariable int id, @Valid @RequestBody UpdateVacationRequest vacationRequest) {
       
        Vacation vacation = new Vacation();

        vacation.setName(vacationRequest.getName());
        vacation.setCountry(vacationRequest.getCountry());
        vacation.setCity(vacationRequest.getCity());
        vacation.setStartDate(vacationRequest.getStartDate());
        vacation.setEndDate(vacationRequest.getEndDate());
        vacation.setTravelerType(vacationRequest.getTravelerType());
        vacation.setBudget(vacationRequest.getBudget());
        vacation.setPace(vacationRequest.getPace());

        Vacation savedVacation  = vacationService.updateVacation(id, vacation);
        return toResponse(savedVacation);
    }


    @PatchMapping("/{id}")
    public VacationResponse patchVacation(@PathVariable int id,
                             @RequestBody PatchVacationRequest vacationRequest) {

    Vacation vacation = vacationService.getVacationById(id);

    if (vacationRequest.getName() != null) {
        vacation.setName(vacationRequest.getName());
    }

    if (vacationRequest.getCountry() != null) {
        vacation.setCountry(vacationRequest.getCountry());
    }

    if (vacationRequest.getCity() != null) {
        vacation.setCity(vacationRequest.getCity());
    }

    if (vacationRequest.getStartDate() != null) {
        vacation.setStartDate(vacationRequest.getStartDate());
    }

    if (vacationRequest.getEndDate() != null) {
        vacation.setEndDate(vacationRequest.getEndDate());
    }

    if (vacationRequest.getTravelerType() != null) {
        vacation.setTravelerType(vacationRequest.getTravelerType());
    }
    if (vacationRequest.getBudget() != null) {
        vacation.setBudget(vacationRequest.getBudget());
    }
    if (vacationRequest.getPace() != null) {
        vacation.setPace(vacationRequest.getPace());
    }

    Vacation savedVacation = vacationService.patchVacation(id,vacation);
    return toResponse(savedVacation);
}

    @DeleteMapping("/{id}")
    public void deleteVacation(@PathVariable int id) {
        vacationService.deleteVacation(id);
    }


    private VacationResponse toResponse(Vacation vacation) {
        
    VacationResponse vacationResponse = new VacationResponse();
    vacationResponse.setId(vacation.getId());
    vacationResponse.setName(vacation.getName());
    vacationResponse.setCountry(vacation.getCountry());
    vacationResponse.setCity(vacation.getCity());
    vacationResponse.setStartDate(vacation.getStartDate());
    vacationResponse.setEndDate(vacation.getEndDate());
    vacationResponse.setTravelerType(vacation.getTravelerType());
    vacationResponse.setBudget(vacation.getBudget());
    vacationResponse.setPace(vacation.getPace());
    return vacationResponse;
}
    
    

}