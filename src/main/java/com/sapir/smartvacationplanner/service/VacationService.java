package com.sapir.smartvacationplanner.service;
import java.util.List;
import com.sapir.smartvacationplanner.entity.Vacation;
import com.sapir.smartvacationplanner.entity.VacationDay;

/**
 * VacationService is a service interface for the Vacation entity.
 * It is used to perform CRUD operations on the Vacation entity.
 */

public interface VacationService {

    List<Vacation> getAllVacations();

    Vacation getVacationById(Integer id);

    Vacation createVacation(Vacation vacation);

    Vacation updateVacation(Integer id, Vacation Vacation);

    Vacation patchVacation(Integer id, Vacation vacation);

    List<VacationDay> getVacationDays(Integer id);

    void deleteVacation(Integer id);

}