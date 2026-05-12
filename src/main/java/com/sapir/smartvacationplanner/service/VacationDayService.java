package com.sapir.smartvacationplanner.service;
import java.util.List;
import com.sapir.smartvacationplanner.entity.VacationDay;
import com.sapir.smartvacationplanner.dto.vacationDay.CreateVacationDayRequest;
import com.sapir.smartvacationplanner.dto.vacationDay.UpdateVacationDayRequest;
import com.sapir.smartvacationplanner.dto.vacationDay.PatchVacationDayRequest;

public interface VacationDayService {

    List<VacationDay> getAllVacationDays(Integer vacationId);

    VacationDay getVacationDayById(Integer vacationId, Integer id);

    VacationDay createVacationDay(Integer vacationId, CreateVacationDayRequest request);

    VacationDay updateVacationDay(Integer vacationId, Integer id, UpdateVacationDayRequest request);

    VacationDay patchVacationDay(Integer vacationId, Integer id, PatchVacationDayRequest request);

    void deleteVacationDay(Integer vacationId,Integer id);
}
