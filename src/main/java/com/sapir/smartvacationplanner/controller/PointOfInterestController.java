package com.sapir.smartvacationplanner.controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import com.sapir.smartvacationplanner.service.PointOfInterestService;
import com.sapir.smartvacationplanner.dto.PointOfInterest.CreatePointOfInterestRequest;
import com.sapir.smartvacationplanner.dto.PointOfInterest.UpdatePointOfInterestRequest;
import com.sapir.smartvacationplanner.dto.PointOfInterest.PointOfInterestResponse;
import jakarta.validation.Valid;
import java.util.List;
import com.sapir.smartvacationplanner.entity.PointOfInterest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.RequestParam;
import com.sapir.smartvacationplanner.entity.enums.PointOfInterestCategory;
import java.time.LocalTime;




@RestController
@RequestMapping("/api/v1/pointOfInterests")
public class PointOfInterestController {

    private final PointOfInterestService pointOfInterestService;

    public PointOfInterestController(PointOfInterestService pointOfInterestService) {
        this.pointOfInterestService = pointOfInterestService;
    }

    @GetMapping
    public List<PointOfInterestResponse> getAllPointOfInterests() {
        
        return pointOfInterestService.getAllPointOfInterests().stream().map(this::toResponse).toList();
        
    }

    @GetMapping("/search")
    public Page<PointOfInterestResponse> searchPointOfInterests(@RequestParam(required = false) String name,
        @RequestParam(required = false) PointOfInterestCategory pointOfInterestCategory,
        @RequestParam(required = false) String placeName,
        @RequestParam(required = false) String placeId,
        @RequestParam(required = false) String formattedAddress,
        @RequestParam(required = false) Double latitude,
        @RequestParam(required = false) Double longitude,
        @RequestParam(required = false) String city,
        @RequestParam(required = false) String country,
        @RequestParam(required = false) Integer durationMinutes,
        @RequestParam(required = false) LocalTime openingTime,
        @RequestParam(required = false) LocalTime closingTime,
        @RequestParam(required = false) Integer minimumAge,
        @RequestParam(required = false) String notes,
        @PageableDefault(page = 0, size = 5, sort = "name", direction = Sort.Direction.ASC) Pageable pageable) {
        
        return pointOfInterestService.searchPointOfInterests(name, pointOfInterestCategory, placeName, city, country, durationMinutes, openingTime, closingTime, minimumAge, notes, pageable).map(this::toResponse);
    }

    @GetMapping("/{id}")
    public PointOfInterestResponse getPointOfInterestById(@PathVariable int id) {
        PointOfInterest pointOfInterest = pointOfInterestService.getPointOfInterestById(id);
       
        return toResponse(pointOfInterest);
    }
    
    @PostMapping
    public PointOfInterestResponse createPointOfInterest(@Valid @RequestBody CreatePointOfInterestRequest pointOfInterestRequest) {
        
        return toResponse(pointOfInterestService.createPointOfInterest(pointOfInterestRequest));
    }
    
    @PutMapping("/{id}")
    public PointOfInterestResponse updatePointOfInterest(@PathVariable int id, @Valid @RequestBody UpdatePointOfInterestRequest pointOfInterestRequest) {
        
        return toResponse(pointOfInterestService.updatePointOfInterest(id, pointOfInterestRequest));
    }
    
    @DeleteMapping("/{id}")
    public void deletePointOfInterest(@PathVariable int id) {
        
        pointOfInterestService.deletePointOfInterest(id);
    }

    private PointOfInterestResponse toResponse(PointOfInterest pointOfInterest) {
        PointOfInterestResponse pointOfInterestResponse = new PointOfInterestResponse();
        pointOfInterestResponse.setId(pointOfInterest.getId());
        pointOfInterestResponse.setName(pointOfInterest.getName());
        pointOfInterestResponse.setPointOfInterestCategory(pointOfInterest.getPointOfInterestCategory());
        pointOfInterestResponse.setPlaceName(pointOfInterest.getPlace().getPlaceName());
        pointOfInterestResponse.setPlaceId(pointOfInterest.getPlace().getPlaceId());
        pointOfInterestResponse.setFormattedAddress(pointOfInterest.getPlace().getFormattedAddress());
        pointOfInterestResponse.setLatitude(pointOfInterest.getPlace().getLatitude());
        pointOfInterestResponse.setLongitude(pointOfInterest.getPlace().getLongitude());
        pointOfInterestResponse.setCity(pointOfInterest.getPlace().getCity());
        pointOfInterestResponse.setCountry(pointOfInterest.getPlace().getCountry());
        pointOfInterestResponse.setDurationMinutes(pointOfInterest.getDurationMinutes());
        pointOfInterestResponse.setOpeningTime(pointOfInterest.getOpeningTime());
        pointOfInterestResponse.setClosingTime(pointOfInterest.getClosingTime());
        pointOfInterestResponse.setMinimumAge(pointOfInterest.getMinimumAge());
        pointOfInterestResponse.setNotes(pointOfInterest.getNotes());
        return pointOfInterestResponse;
    }
} //PointOfInterestController
