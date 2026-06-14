package com.sapir.smartvacationplanner.service;
import java.util.List;
import com.sapir.smartvacationplanner.entity.VacationDayActivity;

/**
 * PointOfInterestService is a service interface for the PointOfInterest entity.
 * It is used to perform CRUD operations on the PointOfInterest entity.
 */

public interface VacationDayActivityService {

    List<VacationDayActivity> getAllVacationDayActivities(Integer vacationId, Integer vacationDayId);

    VacationDayActivity getVacationDayActivityById(Integer vacationId, Integer vacationDayId, Integer id);
    
    VacationDayActivity createVacationDayActivity(Integer vacationId,Integer vacationDayId, int pointOfInterestId);
    
    VacationDayActivity updateVacationDayActivity(Integer vacationId, Integer vacationDayId, Integer id, int pointOfInterestId);
    
    void deleteVacationDayActivity(Integer vacationId, Integer vacationDayId, Integer id);
}
