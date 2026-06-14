package com.sapir.smartvacationplanner.service;

import org.springframework.stereotype.Service;
import com.sapir.smartvacationplanner.repository.UserRepository;
import com.sapir.smartvacationplanner.repository.VacationRepository;
import com.sapir.smartvacationplanner.entity.User;
import com.sapir.smartvacationplanner.entity.Vacation;
import com.sapir.smartvacationplanner.exception.ResourceNotFoundException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.access.AccessDeniedException;
import com.sapir.smartvacationplanner.entity.enums.Role;
import com.sapir.smartvacationplanner.repository.VacationDayRepository;
import com.sapir.smartvacationplanner.repository.VacationDayActivityRepository;
import com.sapir.smartvacationplanner.entity.VacationDay;
import com.sapir.smartvacationplanner.entity.VacationDayActivity;
import java.util.List;
import org.springframework.data.domain.Sort;
import java.time.LocalDate;

/**
 * Handles access checks for resources that belong to the current authenticated user.
 */

@Service
public class AuthorizationService {

    private final UserRepository userRepository;
    private final VacationRepository vacationRepository;
    private final VacationDayRepository vacationDayRepository;
    private final VacationDayActivityRepository vacationDayActivityRepository;

    public AuthorizationService(UserRepository userRepository, VacationRepository vacationRepository, 
        VacationDayRepository vacationDayRepository, VacationDayActivityRepository vacationDayActivityRepository) {

        this.userRepository = userRepository;
        this.vacationRepository = vacationRepository;
        this.vacationDayRepository = vacationDayRepository;
        this.vacationDayActivityRepository = vacationDayActivityRepository;
    }
   
    public User getCurrentUser() {
        // get the current user from the security context
        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();
    
        String email = authentication.getName();
    
        return userRepository.findByEmail(email);
    }

    public Vacation getVacationForCurrentUser(Integer id) {
        
        User currentUser = getCurrentUser();
        Vacation vacation = vacationRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Vacation not found with id: " + id));

        if (currentUser.getRole() != Role.ADMIN &&
        !vacation.getUser().getId().equals(currentUser.getId())) {
            throw new AccessDeniedException("Access denied for vacation with id: " + id);
        }

        return vacation;
    }

    public VacationDay getVacationDayForCurrentUser (Integer vacationId, Integer vacationDayId) {

        Vacation vacation = getVacationForCurrentUser(vacationId);
        return vacationDayRepository.findByVacationAndId(vacation, vacationDayId).orElseThrow(() 
        -> new ResourceNotFoundException("Vacation day not found with id: " + vacationDayId));
    }

    public VacationDay getVacationDayForCurrentUser(Integer vacationId, LocalDate date) {
        Vacation vacation = getVacationForCurrentUser(vacationId);
        return vacationDayRepository.findByVacationAndDate(vacation, date).orElseThrow(() 
        -> new ResourceNotFoundException("Vacation day not found with date: " + date));
    }

    public List<VacationDay> getVacationDaysForCurrentUser(Integer vacationId) {
        Vacation vacation = getVacationForCurrentUser(vacationId);
        return vacationDayRepository.findByVacation(vacation);
    }

    public VacationDayActivity getVacationDayActivityForCurrentUser(Integer vacationId, Integer vacationDayId, Integer vacationDayActivityId) {

        VacationDay vacationDay = getVacationDayForCurrentUser(vacationId,vacationDayId);
        return vacationDayActivityRepository.findByVacationDayAndId(vacationDay, vacationDayActivityId).orElseThrow(() 
        -> new ResourceNotFoundException("Vacation day activity not found with id: " + vacationDayActivityId));
    }

    public List<VacationDayActivity> getVacationDayActivitiesForCurrentUser(Integer vacationId, Integer vacationDayId, Sort sort) {
        VacationDay vacationDay = getVacationDayForCurrentUser(vacationId, vacationDayId);
        return vacationDayActivityRepository.findByVacationDay(vacationDay, sort);
    }

}