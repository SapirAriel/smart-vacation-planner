package com.sapir.smartvacationplanner.service;
import java.util.List;
import com.sapir.smartvacationplanner.entity.VacationDay;
import com.sapir.smartvacationplanner.dto.vacationDay.CreateVacationDayRequest;
import com.sapir.smartvacationplanner.dto.vacationDay.UpdateVacationDayRequest;
import com.sapir.smartvacationplanner.dto.vacationDay.PatchVacationDayRequest;
import com.sapir.smartvacationplanner.entity.Activity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.sapir.smartvacationplanner.entity.enums.DayType;
import java.time.LocalDate;

/**
 * VacationDayService is a service interface for the VacationDay entity.
 * It is used to perform CRUD operations on the VacationDay entity.
 */

public interface VacationDayService {

    List<VacationDay> getAllVacationDays(Integer vacationId);

    Page<VacationDay> searchVacationDays(Integer vacationId, DayType dayType, LocalDate date, Integer dayNumber, Pageable pageable);

    VacationDay getVacationDayById(Integer vacationId, Integer id);

    VacationDay createVacationDay(Integer vacationId, CreateVacationDayRequest request);

    VacationDay updateVacationDay(Integer vacationId, Integer id, UpdateVacationDayRequest request);

    VacationDay patchVacationDay(Integer vacationId, Integer id, PatchVacationDayRequest request);

    List<Activity> getActivities(Integer vacationDayId);

    void deleteVacationDay(Integer vacationId,Integer id);
}
