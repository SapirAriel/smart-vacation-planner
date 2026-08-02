package com.sapir.smartvacationplanner.service;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.sapir.smartvacationplanner.entity.VacationDayActivity;
import com.sapir.smartvacationplanner.exception.DuplicateResourceException;
import com.sapir.smartvacationplanner.repository.VacationDayActivityRepository;
import java.util.List;
import com.sapir.smartvacationplanner.entity.VacationDay;
import com.sapir.smartvacationplanner.entity.PointOfInterest;


/**
 * VacationDayActivityServiceImpl is a service implementation for the VacationDayActivity entity.
 * It is used to perform CRUD operations on the VacationDayActivity entity.
 */

@Service
public class VacationDayActivityServiceImpl implements VacationDayActivityService {


    private final VacationDayActivityRepository vacationDayActivityRepository;
    private final AuthorizationService authorizationService;
    private final PointOfInterestService pointOfInterestService;

    public VacationDayActivityServiceImpl(VacationDayActivityRepository vacationDayActivityRepository, AuthorizationService authorizationService, PointOfInterestService pointOfInterestService) {
        this.vacationDayActivityRepository = vacationDayActivityRepository;
        this.authorizationService = authorizationService;
        this.pointOfInterestService = pointOfInterestService;
    }

    @Override
    public List<VacationDayActivity> getAllVacationDayActivities(Integer vacationId, Integer vacationDayId) {

        VacationDay vacationDay = authorizationService.getVacationDayForCurrentUser(vacationId, vacationDayId);
        return vacationDayActivityRepository.findByVacationDay(vacationDay);
    }

    @Override
    public VacationDayActivity getVacationDayActivityById(Integer vacationId, Integer vacationDayId, Integer id) {
        return authorizationService.getVacationDayActivityForCurrentUser(vacationId, vacationDayId, id);   
    }

    @Override
    @Transactional
    public VacationDayActivity createVacationDayActivity(Integer vacationId, Integer vacationDayId, int pointOfInterestId) {
        
        VacationDay vacationDay = authorizationService.getVacationDayForCurrentUser(vacationId, vacationDayId);
        PointOfInterest pointOfInterest = pointOfInterestService.getPointOfInterestById(pointOfInterestId);

        if (vacationDayActivityRepository.existsByVacationDay_IdAndPointOfInterest_Id(
                vacationDay.getId(), pointOfInterest.getId())) {
            throw new DuplicateResourceException("Point of interest is already assigned to this vacation day");
        }

        VacationDayActivity vacationDayActivity = new VacationDayActivity(vacationDay, pointOfInterest);
        vacationDay.addActivity(vacationDayActivity);
        VacationDayActivity saved = vacationDayActivityRepository.save(vacationDayActivity);
        vacationDayActivityRepository.clearPlanningDataByVacationDayId(vacationDay.getId());
        saved.clearPlanningData();
        return saved;
    }

    @Override
    @Transactional
    public VacationDayActivity updateVacationDayActivity(Integer vacationId, Integer vacationDayId, Integer id, int pointOfInterestId) {

        VacationDayActivity existing = authorizationService.getVacationDayActivityForCurrentUser(vacationId, vacationDayId, id);
        PointOfInterest pointOfInterest = pointOfInterestService.getPointOfInterestById(pointOfInterestId);

        Integer dayId = existing.getVacationDay().getId();
        if (vacationDayActivityRepository.existsByVacationDay_IdAndPointOfInterest_IdAndIdNot(
                dayId, pointOfInterest.getId(), existing.getId())) {
            throw new DuplicateResourceException("Point of interest is already assigned to this vacation day");
        }

        existing.setPointOfInterest(pointOfInterest);
        VacationDayActivity saved = vacationDayActivityRepository.save(existing);
        vacationDayActivityRepository.clearPlanningDataByVacationDayId(dayId);
        saved.clearPlanningData();
        return saved;
    }

    @Override
    @Transactional
    public void deleteVacationDayActivity(Integer vacationId, Integer vacationDayId, Integer id) {
        VacationDayActivity existing = authorizationService.getVacationDayActivityForCurrentUser(vacationId, vacationDayId, id);
        VacationDay vacationDay = existing.getVacationDay();
        vacationDay.removeActivity(existing);
        vacationDayActivityRepository.clearPlanningDataByVacationDayId(vacationDay.getId());
    }

    

    
}
