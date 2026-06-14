package com.sapir.smartvacationplanner.service;
import java.util.List;
import com.sapir.smartvacationplanner.entity.PointOfInterest;
import com.sapir.smartvacationplanner.dto.PointOfInterest.CreatePointOfInterestRequest;
import com.sapir.smartvacationplanner.dto.PointOfInterest.UpdatePointOfInterestRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.sapir.smartvacationplanner.entity.enums.PointOfInterestCategory;
import java.time.LocalTime;

/**
 * PointOfInterestService is a service interface for the PointOfInterest entity.
 * It is used to perform CRUD operations on the PointOfInterest entity.
 */

public interface PointOfInterestService {

    List<PointOfInterest> getAllPointOfInterests();

    Page<PointOfInterest> searchPointOfInterests(String name, PointOfInterestCategory pointOfInterestCategory, String placeName, String city, String country, Integer durationMinutes, LocalTime openingTime, LocalTime closingTime, Integer minimumAge, String notes, Pageable pageable);

    PointOfInterest getPointOfInterestById(Integer id);
    
    PointOfInterest createPointOfInterest(CreatePointOfInterestRequest request);
    
    PointOfInterest updatePointOfInterest(Integer id, UpdatePointOfInterestRequest request);
        
    void deletePointOfInterest(Integer id);
}
