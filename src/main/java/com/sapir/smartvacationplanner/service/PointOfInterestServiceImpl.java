package com.sapir.smartvacationplanner.service;
import org.springframework.stereotype.Service;
import com.sapir.smartvacationplanner.entity.PointOfInterest;
import com.sapir.smartvacationplanner.common.place.Place;
import com.sapir.smartvacationplanner.dto.PointOfInterest.CreatePointOfInterestRequest;
import com.sapir.smartvacationplanner.dto.PointOfInterest.UpdatePointOfInterestRequest;
import com.sapir.smartvacationplanner.repository.PointOfInterestRepository;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalTime;
import com.sapir.smartvacationplanner.integration.google.GooglePlacesClient;
import com.sapir.smartvacationplanner.integration.google.dto.PlaceResult;
import com.sapir.smartvacationplanner.exception.ResourceNotFoundException;
import com.sapir.smartvacationplanner.entity.enums.PointOfInterestCategory;


/**
 * PointOfInterestServiceImpl is a service implementation for the PointOfInterest entity.
 * It is used to perform CRUD operations on the PointOfInterest entity.
 */

@Service
public class PointOfInterestServiceImpl implements PointOfInterestService {

    private final PointOfInterestRepository pointOfInterestRepository;
    private final GooglePlacesClient googlePlacesClient;

    public PointOfInterestServiceImpl(PointOfInterestRepository pointOfInterestRepository, GooglePlacesClient googlePlacesClient) {
        this.pointOfInterestRepository = pointOfInterestRepository;
        this.googlePlacesClient = googlePlacesClient;
    }

    @Override
    public List<PointOfInterest> getAllPointOfInterests() {

        return pointOfInterestRepository.findAll();
    }

    @Override
    public Page<PointOfInterest> searchPointOfInterests(String name, PointOfInterestCategory pointOfInterestCategory, String placeName, String city, String country, Integer durationMinutes, LocalTime openingTime, LocalTime closingTime, Integer minimumAge, String notes, Pageable pageable) {
        
        return pointOfInterestRepository.searchPointOfInterests(name, pointOfInterestCategory, placeName, city, country, durationMinutes, openingTime, closingTime, minimumAge, notes, pageable);
    }

    @Override
    public PointOfInterest getPointOfInterestById(Integer id) {
        return pointOfInterestRepository.findById(id)
        .orElseThrow(() -> new ResourceNotFoundException("Point of interest not found"));   
    }

    @Override
    public PointOfInterest createPointOfInterest(CreatePointOfInterestRequest request) {
        PlaceResult placeResult = googlePlacesClient.searchPlace(request.getPlaceName());
        Place place = new Place(request.getPlaceName(), placeResult.placeId(), placeResult.formattedAddress(), placeResult.city(), placeResult.country(), placeResult.latitude(), placeResult.longitude());
        
        PointOfInterest pointOfInterest = new PointOfInterest(request.getName(), request.getPointOfInterestCategory(), place, 
        request.getDurationMinutes(), request.getOpeningTime(), request.getClosingTime(), request.getMinimumAge(), request.getNotes());

        return pointOfInterestRepository.save(pointOfInterest);
    }

    @Override
    public PointOfInterest updatePointOfInterest(Integer id, UpdatePointOfInterestRequest request) {

        PointOfInterest existing = getPointOfInterestById(id);
        
        if (!existing.getPlace().getPlaceName().equals(request.getPlaceName())) {
            PlaceResult placeResult = googlePlacesClient.searchPlace(request.getPlaceName());
            Place place = new Place(request.getPlaceName(), placeResult.placeId(), placeResult.formattedAddress(), placeResult.city(), placeResult.country(), placeResult.latitude(), placeResult.longitude());
            existing.setPlace(place);
        }

        existing.setName(request.getName());
        existing.setPointOfInterestCategory(request.getPointOfInterestCategory());
        existing.setDurationMinutes(request.getDurationMinutes());
        existing.setOpeningTime(request.getOpeningTime());
        existing.setClosingTime(request.getClosingTime());
        existing.setMinimumAge(request.getMinimumAge()); 
        existing.setNotes(request.getNotes());
        return pointOfInterestRepository.save(existing);
    }


    @Override
    public void deletePointOfInterest(Integer id) {
        pointOfInterestRepository.deleteById(id);
    }

    
}
