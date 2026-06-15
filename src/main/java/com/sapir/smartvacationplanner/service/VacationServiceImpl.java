package com.sapir.smartvacationplanner.service;
import java.util.List;
import org.springframework.stereotype.Service;
import com.sapir.smartvacationplanner.entity.Vacation;
import com.sapir.smartvacationplanner.repository.VacationRepository;
import com.sapir.smartvacationplanner.repository.VacationDayRepository;
import com.sapir.smartvacationplanner.entity.VacationDay;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import com.sapir.smartvacationplanner.entity.enums.TravelerType;
import com.sapir.smartvacationplanner.entity.enums.Pace;
import java.math.BigDecimal;
import com.sapir.smartvacationplanner.entity.User;
import com.sapir.smartvacationplanner.entity.enums.Role;

/**
 * VacationServiceImpl is a service implementation for the Vacation entity.
 * It is used to perform CRUD operations on the Vacation entity.
 */

@Service
public class VacationServiceImpl implements VacationService {

    private final VacationRepository vacationRepository;
    private final VacationDayRepository vacationDayRepository;
    private final AuthorizationService authorizationService;

    public VacationServiceImpl(VacationRepository vacationRepository,
            VacationDayRepository vacationDayRepository, AuthorizationService authorizationService) {
        this.vacationRepository = vacationRepository;
        this.vacationDayRepository = vacationDayRepository;
        this.authorizationService = authorizationService;
    }   

    @Override
    public List<Vacation> getAllVacations() {
        User currentUser = authorizationService.getCurrentUser();
        // if the current user is an admin, return all vacations
        if (currentUser.getRole() == Role.ADMIN) {
            return vacationRepository.findAll();
        }
        return vacationRepository.findByUserId(currentUser.getId());
    }

    @Override
    public Page<Vacation> searchVacations(String country, String city, LocalDate startDate, LocalDate endDate, TravelerType travelerType, BigDecimal budget, Pace pace, Pageable pageable) {
        User currentUser = authorizationService.getCurrentUser();
        // if the current user is an admin, return all vacations
        if (currentUser.getRole() == Role.ADMIN) {
            return vacationRepository.searchVacations(country, city, startDate, endDate, travelerType, budget, pace, pageable);
        }
        return vacationRepository.searchVacationsByUserId(currentUser.getId(), country, city, startDate, endDate, travelerType, budget, pace, pageable);
    }

    @Override
    public Vacation getVacationById(Integer id) {
        return authorizationService.getVacationForCurrentUser(id);
    }

    @Override
    public Vacation createVacation(Vacation vacation) {
        
        validateVacationConstraints(vacation);
        User currentUser = authorizationService.getCurrentUser();
        vacation.setUser(currentUser);
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
        validateVacationConstraints(existingVacation);
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
        vacationRepository.delete(existingVacation);
    }

    private void validateVacationConstraints(Vacation vacation) {
        if (vacation.getStartDate() != null && vacation.getEndDate() != null
            && vacation.getEndDate().isBefore(vacation.getStartDate())) {
            throw new IllegalArgumentException("endDate must be after or equal to startDate");}
    }

}
