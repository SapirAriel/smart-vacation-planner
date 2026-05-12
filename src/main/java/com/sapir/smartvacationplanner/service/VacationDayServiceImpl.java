package com.sapir.smartvacationplanner.service;
import java.util.List;
import com.sapir.smartvacationplanner.dto.vacationDay.CreateVacationDayRequest;
import com.sapir.smartvacationplanner.dto.vacationDay.PatchVacationDayRequest;
import com.sapir.smartvacationplanner.dto.vacationDay.UpdateVacationDayRequest;
import com.sapir.smartvacationplanner.entity.Vacation;
import com.sapir.smartvacationplanner.entity.VacationDay;
import com.sapir.smartvacationplanner.repository.VacationDayRepository;
import com.sapir.smartvacationplanner.repository.VacationRepository;
import com.sapir.smartvacationplanner.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class VacationDayServiceImpl implements VacationDayService {

    private final VacationDayRepository vacationDayRepository;
    private final VacationRepository vacationRepository;

    public VacationDayServiceImpl(VacationDayRepository vacationDayRepository,
            VacationRepository vacationRepository) {
        this.vacationDayRepository = vacationDayRepository;
        this.vacationRepository = vacationRepository;
    }

    @Override
    public List<VacationDay> getAllVacationDays(Integer vacationId) {
        Vacation vacation = vacationRepository.findById(vacationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vacation not found with id: " + vacationId));
        return vacationDayRepository.findByVacation(vacation);
    }

    @Override
    public VacationDay getVacationDayById(Integer vacationId, Integer id) {
        VacationDay vacationDay = vacationDayRepository.findById(id).orElseThrow(() 
        -> new ResourceNotFoundException("Vacation day not found with id: " + id));
        if (vacationDay.getVacation().getId() != vacationId) {
            throw new IllegalArgumentException("Vacation day not found with id: " + id);
        }
        return vacationDay;
    }

    @Override
    public VacationDay createVacationDay(Integer vacationId, CreateVacationDayRequest request) {
        Vacation vacation = vacationRepository.findById(vacationId)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Vacation not found with id: " + vacationId));
        VacationDay vacationDay = new VacationDay();
        vacationDay.setVacation(vacation);
        vacationDay.setDate(request.getDate());
        vacationDay.setDayNumber(request.getDayNumber());
        vacationDay.setDayType(request.getDayType());
        validateVacationDayConstraints(vacationDay);
        return vacationDayRepository.save(vacationDay);
    }

    @Override
    public VacationDay updateVacationDay(Integer vacationId, Integer id, UpdateVacationDayRequest request) {
        VacationDay existing = getVacationDayById(vacationId, id);

        existing.setDate(request.getDate());
        existing.setDayNumber(request.getDayNumber());
        existing.setDayType(request.getDayType());
        validateVacationDayConstraints(existing);
        return vacationDayRepository.save(existing);
    }

    @Override
    public VacationDay patchVacationDay(Integer vacationId, Integer id, PatchVacationDayRequest request) {
        VacationDay existing = getVacationDayById(vacationId, id);
        
        if (request.getDate() != null) {
            existing.setDate(request.getDate());
        }
        if (request.getDayNumber() != null) {
            existing.setDayNumber(request.getDayNumber());
        }
        if (request.getDayType() != null) {
            existing.setDayType(request.getDayType());
        }
        validateVacationDayConstraints(existing);
        return vacationDayRepository.save(existing);
    }

    @Override
    public void deleteVacationDay(Integer vacationId, Integer id) {
        getVacationDayById(vacationId, id);        
        vacationDayRepository.deleteById(id);
    }

    private void validateVacationDayConstraints(VacationDay vacationDay) {

        if (vacationDay.getDate() != null
        && vacationDay.getDate().isBefore(vacationDay.getVacation().getStartDate())) {
            throw new IllegalArgumentException("date must be on or after vacation startDate");}

        if (vacationDay.getDate() != null
        && vacationDay.getDate().isAfter(vacationDay.getVacation().getEndDate())) {
            throw new IllegalArgumentException("date must be on or before vacation endDate");}

        if (vacationDay.getDayNumber() < 1) {
            throw new IllegalArgumentException("day number must be greater than 0");}
    }
}
