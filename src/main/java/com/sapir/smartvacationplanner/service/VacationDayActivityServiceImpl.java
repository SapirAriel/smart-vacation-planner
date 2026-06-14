package com.sapir.smartvacationplanner.service;
import org.springframework.stereotype.Service;
import com.sapir.smartvacationplanner.entity.VacationDayActivity;
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
    public VacationDayActivity createVacationDayActivity(Integer vacationId, Integer vacationDayId, int pointOfInterestId) {
        
        VacationDay vacationDay = authorizationService.getVacationDayForCurrentUser(vacationId, vacationDayId);
        PointOfInterest pointOfInterest = pointOfInterestService.getPointOfInterestById(pointOfInterestId);
        VacationDayActivity vacationDayActivity = new VacationDayActivity(vacationDay, pointOfInterest);
        return vacationDayActivityRepository.save(vacationDayActivity);
    }

    @Override
    public VacationDayActivity updateVacationDayActivity(Integer vacationId, Integer vacationDayId, Integer id, int pointOfInterestId) {

        VacationDayActivity existing = authorizationService.getVacationDayActivityForCurrentUser(vacationId, vacationDayId, id);
        PointOfInterest pointOfInterest = pointOfInterestService.getPointOfInterestById(pointOfInterestId);
        existing.setPointOfInterest(pointOfInterest);
        return vacationDayActivityRepository.save(existing);
    }

    @Override
    public void deleteVacationDayActivity(Integer vacationId, Integer vacationDayId, Integer id) {
        VacationDayActivity existing = authorizationService.getVacationDayActivityForCurrentUser(vacationId, vacationDayId, id);
        vacationDayActivityRepository.delete(existing);
    }

    
}
