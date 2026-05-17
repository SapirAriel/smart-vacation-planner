package com.sapir.smartvacationplanner.service;
import java.util.List;
import com.sapir.smartvacationplanner.entity.Vacation;
import com.sapir.smartvacationplanner.entity.VacationDay;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import com.sapir.smartvacationplanner.entity.enums.TravelerType;
import com.sapir.smartvacationplanner.entity.enums.Pace;
import java.math.BigDecimal;

/**
 * VacationService is a service interface for the Vacation entity.
 * It is used to perform CRUD operations on the Vacation entity.
 */

public interface VacationService {

    List<Vacation> getAllVacations();

    Page<Vacation> searchVacations(String country, String city, LocalDate startDate, LocalDate endDate, TravelerType travelerType, BigDecimal budget, Pace pace, Pageable pageable);

    Vacation getVacationById(Integer id);

    Vacation createVacation(Vacation vacation);

    Vacation updateVacation(Integer id, Vacation Vacation);

    Vacation patchVacation(Integer id, Vacation vacation);

    List<VacationDay> getVacationDays(Integer id);

    void deleteVacation(Integer id);

}