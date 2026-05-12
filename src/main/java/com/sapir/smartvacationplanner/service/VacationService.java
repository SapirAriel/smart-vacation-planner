package com.sapir.smartvacationplanner.service;
import java.util.List;
import com.sapir.smartvacationplanner.entity.Vacation;
import com.sapir.smartvacationplanner.entity.VacationDay;

public interface VacationService {

    List<Vacation> getAllVacations();

    Vacation getVacationById(Integer id);

    Vacation createVacation(Vacation vacation);

    Vacation updateVacation(Integer id, Vacation Vacation);

    Vacation patchVacation(Integer id, Vacation vacation);

    List<VacationDay> getVacationDays(Integer id);

    void deleteVacation(Integer id);

}