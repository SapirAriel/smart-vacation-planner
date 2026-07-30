package com.sapir.smartvacationplanner.controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import com.sapir.smartvacationplanner.entity.VacationDay;
import com.sapir.smartvacationplanner.service.VacationDayService;
import com.sapir.smartvacationplanner.dto.vacationDay.CreateVacationDayRequest;
import com.sapir.smartvacationplanner.dto.vacationDay.UpdateVacationDayRequest;
import com.sapir.smartvacationplanner.dto.vacationDay.PatchVacationDayRequest;
import com.sapir.smartvacationplanner.dto.vacationDay.VacationDayResponse;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestParam;
import com.sapir.smartvacationplanner.entity.enums.DayType;
import java.time.LocalDate;

/**
 * VacationDayController is a controller for the VacationDay entity.
 * It is used to perform CRUD operations on the VacationDay entity.
 */

@RestController
@RequestMapping("/api/v1/vacations/{vacationId}/days")
public class VacationDayController {

    private final VacationDayService vacationDayService;

    public VacationDayController(VacationDayService vacationDayService) {
        this.vacationDayService = vacationDayService;
    }

    @GetMapping
    public List<VacationDayResponse> getAllVacationDays(@PathVariable Integer vacationId) {
     
        return vacationDayService.getAllVacationDays(vacationId).stream().map(this::toResponse).toList();
    }

    @GetMapping("/page")
    public Page<VacationDayResponse> searchVacationDays(@RequestParam(required = false) DayType dayType, 
    @RequestParam(required = false) LocalDate date,
    @RequestParam(required = false) Integer dayNumber,
    @PathVariable Integer vacationId, 
        @PageableDefault(page = 0, size = 5, sort = "date", direction = Sort.Direction.DESC) Pageable pageable) {
        return vacationDayService.searchVacationDays(vacationId, dayType, date, dayNumber, pageable).map(this::toResponse);
    }

    @GetMapping("/{id}")
    public VacationDayResponse getVacationDayById(@PathVariable Integer vacationId, @PathVariable int id) {
        
        return toResponse(vacationDayService.getVacationDayById(vacationId, id));
    }
    

    @PostMapping
    public VacationDayResponse createVacationDay(@PathVariable Integer vacationId, 
    @Valid @RequestBody CreateVacationDayRequest vacationDayRequest) {
        
        return toResponse(vacationDayService.createVacationDay(vacationId, vacationDayRequest));
    }

    @PutMapping("/{id}")
    public VacationDayResponse updateVacationDay(@PathVariable Integer vacationId, @PathVariable int id,
            @Valid @RequestBody UpdateVacationDayRequest vacationDayRequest) {
        
        return toResponse(vacationDayService.updateVacationDay(vacationId, id, vacationDayRequest));
    }

    @PatchMapping("/{id}")
    public VacationDayResponse patchVacationDay(@PathVariable Integer vacationId, @PathVariable int id,
            @Valid @RequestBody PatchVacationDayRequest vacationDayRequest) {

        return toResponse(vacationDayService.patchVacationDay(vacationId, id, vacationDayRequest));
    }

    @DeleteMapping("/{id}")
    public void deleteVacationDay(@PathVariable Integer vacationId ,@PathVariable int id) {

        vacationDayService.deleteVacationDay(vacationId, id);
    }

    private VacationDayResponse toResponse(VacationDay vacationDay) {

        VacationDayResponse vacationDayResponse = new VacationDayResponse();
        vacationDayResponse.setId(vacationDay.getId());
        vacationDayResponse.setVacationId(vacationDay.getVacation().getId());
        vacationDayResponse.setDate(vacationDay.getDate());
        vacationDayResponse.setDayNumber(vacationDay.getDayNumber());
        vacationDayResponse.setDayType(vacationDay.getDayType());

        if (vacationDay.getHotelPlace() != null) {
            vacationDayResponse.setHotelPlaceName(vacationDay.getHotelPlace().getPlaceName());
        }
        return vacationDayResponse;
    }

}
