package com.sapir.smartvacationplanner.service;
import java.util.List;
import org.springframework.stereotype.Service;
import com.sapir.smartvacationplanner.entity.Vacation;
import com.sapir.smartvacationplanner.repository.VacationRepository;
import com.sapir.smartvacationplanner.repository.VacationDayRepository;
import com.sapir.smartvacationplanner.exception.ResourceNotFoundException;
import com.sapir.smartvacationplanner.entity.VacationDay;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import com.sapir.smartvacationplanner.entity.enums.TravelerType;
import com.sapir.smartvacationplanner.entity.enums.Pace;
import java.math.BigDecimal;

/**
 * VacationServiceImpl is a service implementation for the Vacation entity.
 * It is used to perform CRUD operations on the Vacation entity.
 */

@Service
public class VacationServiceImpl implements VacationService {

    private final VacationRepository vacationRepository;
    private final VacationDayRepository vacationDayRepository;

    public VacationServiceImpl(VacationRepository vacationRepository,
            VacationDayRepository vacationDayRepository) {
        this.vacationRepository = vacationRepository;
        this.vacationDayRepository = vacationDayRepository;
    }   

    @Override
    public List<Vacation> getAllVacations() {
        return vacationRepository.findAll();
    }

    @Override
    public Page<Vacation> searchVacations(String country, String city, LocalDate startDate, LocalDate endDate, TravelerType travelerType, BigDecimal budget, Pace pace, Pageable pageable) {
    
        return vacationRepository.searchVacations(country, city, startDate, endDate, travelerType, budget, pace, pageable);
    }

    @Override
    public Vacation getVacationById(Integer id) {
        return vacationRepository.findById(id).orElseThrow(() 
        -> new ResourceNotFoundException("Vacation not found with id: " + id));
    }

    @Override
    public Vacation createVacation(Vacation vacation) {
        
        validateVacationConstraints(vacation);
        return vacationRepository.save(vacation);
    }

    @Override

    public Vacation updateVacation(Integer id, Vacation vacation) {

        Vacation existingVacation = getVacationById(id);

        existingVacation.setName(vacation.getName());
        existingVacation.setCountry(vacation.getCountry());
        existingVacation.setCity(vacation.getCity());
        existingVacation.setStartDate(vacation.getStartDate());
        existingVacation.setEndDate(vacation.getEndDate());
        existingVacation.setTravelerType(vacation.getTravelerType());
        existingVacation.setBudget(vacation.getBudget());
        existingVacation.setPace(vacation.getPace());
    
        validateVacationConstraints(existingVacation);
        return vacationRepository.save(existingVacation);
    }

    @Override
    public Vacation patchVacation(Integer id, Vacation vacation) {
        Vacation existingVacation = getVacationById(id);
        if (vacation.getName() != null) {
        existingVacation.setName(vacation.getName());
        }
        if (vacation.getCountry() != null) {
            existingVacation.setCountry(vacation.getCountry());
        }
        if (vacation.getCity() != null) {
            existingVacation.setCity(vacation.getCity());
        }
        if (vacation.getStartDate() != null) {
            existingVacation.setStartDate(vacation.getStartDate());
        }
        if (vacation.getEndDate() != null) {
            existingVacation.setEndDate(vacation.getEndDate());
        }
        if (vacation.getTravelerType() != null) {
            existingVacation.setTravelerType(vacation.getTravelerType());
        }
        if (vacation.getBudget() != null) {
            existingVacation.setBudget(vacation.getBudget());
        }
        if (vacation.getPace() != null) {
            existingVacation.setPace(vacation.getPace());
        }
        return vacationRepository.save(existingVacation);
    }

    @Override
    public List<VacationDay> getVacationDays(Integer id) {
        getVacationById(id);
        return vacationDayRepository.findByVacation_Id(id);
    }

    @Override
    public void deleteVacation(Integer id) {
        Vacation existingVacation = getVacationById(id);
        if (existingVacation == null) {
            throw new IllegalArgumentException("Vacation not found with id: " + id);}
        vacationRepository.delete(existingVacation);
    }

    private void validateVacationConstraints(Vacation vacation) {
        if (vacation.getStartDate() != null && vacation.getEndDate() != null
            && vacation.getEndDate().isBefore(vacation.getStartDate())) {
            throw new IllegalArgumentException("endDate must be after or equal to startDate");}
    }
}