package com.sapir.smartvacationplanner.service;
import java.util.List;
import com.sapir.smartvacationplanner.dto.vacationDay.CreateVacationDayRequest;
import com.sapir.smartvacationplanner.dto.vacationDay.PatchVacationDayRequest;
import com.sapir.smartvacationplanner.dto.vacationDay.UpdateVacationDayRequest;
import com.sapir.smartvacationplanner.entity.Vacation;
import com.sapir.smartvacationplanner.entity.VacationDay;
import com.sapir.smartvacationplanner.repository.VacationDayRepository;
import com.sapir.smartvacationplanner.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import com.sapir.smartvacationplanner.repository.VacationDayActivityRepository;
import java.time.temporal.ChronoUnit;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.sapir.smartvacationplanner.entity.enums.DayType;
import java.time.LocalDate;
import com.sapir.smartvacationplanner.common.place.Place;
import com.sapir.smartvacationplanner.integration.google.GooglePlacesClient;
import com.sapir.smartvacationplanner.integration.google.dto.PlaceResult;
import com.sapir.smartvacationplanner.entity.VacationDayActivity;

/**
 * VacationDayServiceImpl is a service implementation for the VacationDay entity.
 * It is used to perform CRUD operations on the VacationDay entity.
 */

@Service
public class VacationDayServiceImpl implements VacationDayService {

    private final VacationDayRepository vacationDayRepository;
    private final VacationDayActivityRepository vacationDayActivityRepository;
    private final AuthorizationService authorizationService;
    private final GooglePlacesClient googlePlacesClient;

    public VacationDayServiceImpl(VacationDayRepository vacationDayRepository,
            VacationDayActivityRepository vacationDayActivityRepository, AuthorizationService authorizationService, GooglePlacesClient googlePlacesClient) {
        this.vacationDayRepository = vacationDayRepository;
        this.vacationDayActivityRepository = vacationDayActivityRepository;
        this.authorizationService = authorizationService;
        this.googlePlacesClient = googlePlacesClient;
    }

    @Override
    public List<VacationDay> getAllVacationDays(Integer vacationId) {
        Vacation vacation = authorizationService.getVacationForCurrentUser(vacationId);
        return vacationDayRepository.findByVacation(vacation);
    }

    @Override
    public Page<VacationDay> searchVacationDays(Integer vacationId, DayType dayType, LocalDate date, Integer dayNumber, Pageable pageable) {
        Vacation vacation = authorizationService.getVacationForCurrentUser(vacationId);
        return vacationDayRepository.searchVacationDays(vacation, dayType, date, dayNumber, pageable);
    }

    @Override
    public VacationDay getVacationDayById(Integer vacationId, Integer id) {
        return getVacationDayForCurrentUser(vacationId, id);
    }

    @Override
    public VacationDay createVacationDay(Integer vacationId, CreateVacationDayRequest request) {
        Vacation vacation = authorizationService.getVacationForCurrentUser(vacationId);
        PlaceResult placeResult = googlePlacesClient.searchPlace(request.getHotelPlaceName());
        Place hotelPlace = new Place(request.getHotelPlaceName(), placeResult.placeId(), placeResult.formattedAddress(), placeResult.city(), placeResult.country(), placeResult.latitude(), placeResult.longitude());
        VacationDay vacationDay = new VacationDay(vacation, request.getDate(), request.getDayNumber(), request.getDayType(), hotelPlace);
        validateVacationDayConstraints(vacationDay);
        return vacationDayRepository.save(vacationDay);
    }

    @Override
    public VacationDay updateVacationDay(Integer vacationId, Integer id, UpdateVacationDayRequest request) {
        VacationDay existing = getVacationDayForCurrentUser(vacationId, id);

        if (!request.getHotelPlaceName().equals(existing.getHotelPlace().getPlaceName())) {
            PlaceResult placeResult = googlePlacesClient.searchPlace(request.getHotelPlaceName());
            Place hotelPlace = new Place(request.getHotelPlaceName(), placeResult.placeId(), placeResult.formattedAddress(), placeResult.city(), placeResult.country(), placeResult.latitude(), placeResult.longitude());
            existing.setHotelPlace(hotelPlace);
        }
        existing.setDate(request.getDate());
        existing.setDayNumber(request.getDayNumber());
        existing.setDayType(request.getDayType());
        validateVacationDayConstraints(existing);
        return vacationDayRepository.save(existing);
    }

    @Override
    public VacationDay patchVacationDay(Integer vacationId, Integer id, PatchVacationDayRequest request) {
        VacationDay existing = getVacationDayForCurrentUser(vacationId, id);
        
        if (request.getDate() != null) {
            existing.setDate(request.getDate());
        }
        if (request.getDayNumber() != null) {
            existing.setDayNumber(request.getDayNumber());
        }
        if (request.getDayType() != null) {
            existing.setDayType(request.getDayType());
        }
        if (request.getHotelPlaceName() != null && !request.getHotelPlaceName().equals(existing.getHotelPlace().getPlaceName())) {
            PlaceResult placeResult = googlePlacesClient.searchPlace(request.getHotelPlaceName());
            Place hotelPlace = new Place(request.getHotelPlaceName(), placeResult.placeId(), placeResult.formattedAddress(), placeResult.city(), placeResult.country(), placeResult.latitude(), placeResult.longitude());
            existing.setHotelPlace(hotelPlace);
        }
        validateVacationDayConstraints(existing);
        return vacationDayRepository.save(existing);
    }

    @Override
    public List<VacationDayActivity> getAllVacationDayActivities(Integer vacationId, Integer vacationDayId) {
        VacationDay vacationDay = getVacationDayForCurrentUser(vacationId, vacationDayId);
        return vacationDayActivityRepository.findByVacationDay(vacationDay);
    }

    @Override
    public void deleteVacationDay(Integer vacationId, Integer id) {
        VacationDay vacationDay = getVacationDayForCurrentUser(vacationId, id);        
        vacationDayRepository.delete(vacationDay);
    }

    private void validateVacationDayConstraints(VacationDay vacationDay) {

        if (vacationDay.getDate() != null
        && vacationDay.getDate().isBefore(vacationDay.getVacation().getStartDate())) {
            throw new IllegalArgumentException("date must be on or after vacation startDate");}

        if (vacationDay.getDate() != null
        && vacationDay.getDate().isAfter(vacationDay.getVacation().getEndDate())) {
            throw new IllegalArgumentException("date must be on or before vacation endDate");}

        long vacationDuration = 1+ChronoUnit.DAYS.between(vacationDay.getVacation().getStartDate(), vacationDay.getVacation().getEndDate());
        if (vacationDay.getDayNumber() > vacationDuration) {
            throw new IllegalArgumentException("day number must be less than or equal to vacation duration");}

    }

    private VacationDay getVacationDayForCurrentUser (Integer vacationId, Integer vacationDayId) {
        Vacation vacation = authorizationService.getVacationForCurrentUser(vacationId);
        return vacationDayRepository.findByVacationAndId(vacation, vacationDayId).orElseThrow(() 
        -> new ResourceNotFoundException("Vacation day not found with id: " + vacationDayId));
    }
}
