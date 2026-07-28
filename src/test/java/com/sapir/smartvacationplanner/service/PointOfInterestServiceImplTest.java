package com.sapir.smartvacationplanner.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;

import com.sapir.smartvacationplanner.common.place.Place;
import com.sapir.smartvacationplanner.dto.PointOfInterest.CreatePointOfInterestRequest;
import com.sapir.smartvacationplanner.dto.PointOfInterest.UpdatePointOfInterestRequest;
import com.sapir.smartvacationplanner.entity.PointOfInterest;
import com.sapir.smartvacationplanner.entity.enums.PointOfInterestCategory;
import com.sapir.smartvacationplanner.exception.ResourceNotFoundException;
import com.sapir.smartvacationplanner.integration.google.GooglePlacesClient;
import com.sapir.smartvacationplanner.integration.google.dto.PlaceResult;
import com.sapir.smartvacationplanner.repository.PointOfInterestRepository;

@ExtendWith(MockitoExtension.class)
class PointOfInterestServiceImplTest {

    private static final String PLACE_NAME = "Louvre Museum";
    private static final String PLACE_ID = "ChIJxxx";
    private static final String FORMATTED_ADDRESS = "Rue de Rivoli, 75001 Paris, France";
    private static final Double LATITUDE = 48.86;
    private static final Double LONGITUDE = 2.33;
    private static final String CITY = "Paris";
    private static final String COUNTRY = "France";
    private static final PointOfInterestCategory CATEGORY = PointOfInterestCategory.MUSEUM;
    private static final Integer DURATION_MINUTES = 120;
    private static final LocalTime OPENING_TIME = LocalTime.of(9, 0);
    private static final LocalTime CLOSING_TIME = LocalTime.of(18, 0);
    private static final Integer MINIMUM_AGE = 0;
    private static final String NOTES = "Book tickets in advance";
    private static final String PLACE_NOT_FOUND_MESSAGE =
            "No place found for query: " + PLACE_NAME;

    @Mock
    private PointOfInterestRepository repository;

    @Mock
    private GooglePlacesClient googlePlacesClient;

    private PointOfInterestServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new PointOfInterestServiceImpl(
                repository,
                googlePlacesClient
        );
    }

    private static CreatePointOfInterestRequest createRequest() {
        CreatePointOfInterestRequest request = new CreatePointOfInterestRequest();
        request.setPointOfInterestCategory(CATEGORY);
        request.setPlaceName(PLACE_NAME);
        request.setDurationMinutes(DURATION_MINUTES);
        request.setOpeningTime(OPENING_TIME);
        request.setClosingTime(CLOSING_TIME);
        request.setMinimumAge(MINIMUM_AGE);
        request.setNotes(NOTES);
        return request;
    }

    private static UpdatePointOfInterestRequest updateRequest(
            PointOfInterestCategory category,
            Integer durationMinutes,
            LocalTime openingTime,
            LocalTime closingTime,
            Integer minimumAge,
            String notes
    ) {
        UpdatePointOfInterestRequest request = new UpdatePointOfInterestRequest();
        request.setPointOfInterestCategory(category);
        request.setDurationMinutes(durationMinutes);
        request.setOpeningTime(openingTime);
        request.setClosingTime(closingTime);
        request.setMinimumAge(minimumAge);
        request.setNotes(notes);
        return request;
    }

    private static PlaceResult placeResult() {
        return new PlaceResult(
                PLACE_ID,
                FORMATTED_ADDRESS,
                LATITUDE,
                LONGITUDE,
                CITY,
                COUNTRY
        );
    }

    private static PointOfInterest existingPointOfInterest(Integer id, String placeName) {
        Place place = new Place(
                placeName,
                PLACE_ID,
                FORMATTED_ADDRESS,
                CITY,
                COUNTRY,
                LATITUDE,
                LONGITUDE
        );
        PointOfInterest existing = new PointOfInterest(
                CATEGORY,
                place,
                DURATION_MINUTES,
                OPENING_TIME,
                CLOSING_TIME,
                MINIMUM_AGE,
                NOTES
        );
        existing.setId(id);
        return existing;
    }

    private static void assertStoredFieldsUnchanged(PointOfInterest result, String expectedPlaceName) {
        assertAll(
                () -> assertEquals(expectedPlaceName, result.getPlace().getPlaceName()),
                () -> assertEquals(PLACE_ID, result.getPlace().getPlaceId()),
                () -> assertEquals(FORMATTED_ADDRESS, result.getPlace().getFormattedAddress()),
                () -> assertEquals(CITY, result.getPlace().getCity()),
                () -> assertEquals(COUNTRY, result.getPlace().getCountry()),
                () -> assertEquals(LATITUDE, result.getPlace().getLatitude()),
                () -> assertEquals(LONGITUDE, result.getPlace().getLongitude()),
                () -> assertEquals(CATEGORY, result.getPointOfInterestCategory()),
                () -> assertEquals(DURATION_MINUTES, result.getDurationMinutes()),
                () -> assertEquals(OPENING_TIME, result.getOpeningTime()),
                () -> assertEquals(CLOSING_TIME, result.getClosingTime()),
                () -> assertEquals(MINIMUM_AGE, result.getMinimumAge()),
                () -> assertEquals(NOTES, result.getNotes())
        );
    }

    @Test
    void createPointOfInterest_whenGooglePlacesReturnsResultAndNoDuplicate_mapsPlaceFieldsAndSaves() {
        // Arrange
        CreatePointOfInterestRequest request = createRequest();
        PlaceResult placeResult = placeResult();

        Mockito.when(repository.findByPlace_PlaceNameIgnoreCase(PLACE_NAME)).thenReturn(Optional.empty());
        Mockito.when(googlePlacesClient.searchPlace(PLACE_NAME)).thenReturn(placeResult);
        Mockito.when(repository.findByPlace_PlaceId(PLACE_ID)).thenReturn(Optional.empty());
        Mockito.when(repository.save(any(PointOfInterest.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        PointOfInterest saved = service.createPointOfInterest(request);

        // Assert
        assertNotNull(saved);
        assertNotNull(saved.getPlace());
        assertAll(
                () -> assertEquals(PLACE_NAME, saved.getPlace().getPlaceName()),
                () -> assertEquals(CATEGORY, saved.getPointOfInterestCategory()),
                () -> assertEquals(DURATION_MINUTES, saved.getDurationMinutes()),
                () -> assertEquals(OPENING_TIME, saved.getOpeningTime()),
                () -> assertEquals(CLOSING_TIME, saved.getClosingTime()),
                () -> assertEquals(MINIMUM_AGE, saved.getMinimumAge()),
                () -> assertEquals(NOTES, saved.getNotes()),
                () -> assertEquals(PLACE_ID, saved.getPlace().getPlaceId()),
                () -> assertEquals(FORMATTED_ADDRESS, saved.getPlace().getFormattedAddress()),
                () -> assertEquals(LATITUDE, saved.getPlace().getLatitude()),
                () -> assertEquals(LONGITUDE, saved.getPlace().getLongitude()),
                () -> assertEquals(CITY, saved.getPlace().getCity()),
                () -> assertEquals(COUNTRY, saved.getPlace().getCountry())
        );

        Mockito.verify(repository).findByPlace_PlaceNameIgnoreCase(eq(PLACE_NAME));
        Mockito.verify(googlePlacesClient).searchPlace(eq(PLACE_NAME));
        Mockito.verify(repository).findByPlace_PlaceId(eq(PLACE_ID));
        Mockito.verify(repository, Mockito.times(1)).save(saved);
    }

    @Test
    void createPointOfInterest_whenGooglePlacesFindsNoPlace_propagatesIllegalArgumentExceptionAndDoesNotSave() {
        // Arrange
        CreatePointOfInterestRequest request = createRequest();

        Mockito.when(repository.findByPlace_PlaceNameIgnoreCase(PLACE_NAME)).thenReturn(Optional.empty());
        Mockito.when(googlePlacesClient.searchPlace(PLACE_NAME))
                .thenThrow(new IllegalArgumentException(PLACE_NOT_FOUND_MESSAGE));

        // Act & Assert
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> service.createPointOfInterest(request)
        );
        assertEquals(PLACE_NOT_FOUND_MESSAGE, exception.getMessage());

        Mockito.verify(googlePlacesClient).searchPlace(eq(PLACE_NAME));
        Mockito.verify(repository, never()).findByPlace_PlaceId(anyString());
        Mockito.verify(repository, never()).save(any(PointOfInterest.class));
    }

    @Test
    void createPointOfInterest_whenExistingFoundByPlaceNameIgnoreCase_returnsExistingWithoutCallingGoogleOrSaving() {
        // Arrange
        String requestPlaceName = "louvre museum";
        PointOfInterest existing = existingPointOfInterest(1, PLACE_NAME);

        CreatePointOfInterestRequest request = new CreatePointOfInterestRequest();
        request.setPointOfInterestCategory(PointOfInterestCategory.RESTAURANT);
        request.setPlaceName(requestPlaceName);
        request.setDurationMinutes(90);
        request.setOpeningTime(LocalTime.of(10, 0));
        request.setClosingTime(LocalTime.of(20, 0));
        request.setMinimumAge(12);
        request.setNotes("New notes");

        Mockito.when(repository.findByPlace_PlaceNameIgnoreCase(requestPlaceName))
                .thenReturn(Optional.of(existing));

        // Act
        PointOfInterest result = service.createPointOfInterest(request);

        // Assert
        assertSame(existing, result);
        assertStoredFieldsUnchanged(result, PLACE_NAME);

        Mockito.verify(repository).findByPlace_PlaceNameIgnoreCase(eq(requestPlaceName));
        Mockito.verify(googlePlacesClient, never()).searchPlace(anyString());
        Mockito.verify(repository, never()).findByPlace_PlaceId(anyString());
        Mockito.verify(repository, never()).save(any(PointOfInterest.class));
    }

    @Test
    void createPointOfInterest_whenNoNameMatchButExistingFoundByPlaceId_returnsExistingWithoutSaving() {
        // Arrange
        String requestPlaceName = "Musée du Louvre";
        PointOfInterest existing = existingPointOfInterest(2, PLACE_NAME);
        PlaceResult placeResult = placeResult();

        CreatePointOfInterestRequest request = new CreatePointOfInterestRequest();
        request.setPointOfInterestCategory(PointOfInterestCategory.RESTAURANT);
        request.setPlaceName(requestPlaceName);
        request.setDurationMinutes(90);
        request.setOpeningTime(LocalTime.of(10, 0));
        request.setClosingTime(LocalTime.of(20, 0));
        request.setMinimumAge(12);
        request.setNotes("New notes");

        Mockito.when(repository.findByPlace_PlaceNameIgnoreCase(requestPlaceName))
                .thenReturn(Optional.empty());
        Mockito.when(googlePlacesClient.searchPlace(requestPlaceName)).thenReturn(placeResult);
        Mockito.when(repository.findByPlace_PlaceId(PLACE_ID)).thenReturn(Optional.of(existing));

        // Act
        PointOfInterest result = service.createPointOfInterest(request);

        // Assert
        assertSame(existing, result);
        assertStoredFieldsUnchanged(result, PLACE_NAME);

        Mockito.verify(repository).findByPlace_PlaceNameIgnoreCase(eq(requestPlaceName));
        Mockito.verify(googlePlacesClient, Mockito.times(1)).searchPlace(eq(requestPlaceName));
        Mockito.verify(repository).findByPlace_PlaceId(eq(PLACE_ID));
        Mockito.verify(repository, never()).save(any(PointOfInterest.class));
    }

    @Test
    void updatePointOfInterest_whenPointOfInterestExists_updatesMutableFieldsAndLeavesPlaceUnchanged() {
        // Arrange
        PointOfInterest existing = existingPointOfInterest(1, PLACE_NAME);
        Place originalPlace = existing.getPlace();

        PointOfInterestCategory updatedCategory = PointOfInterestCategory.RESTAURANT;
        Integer updatedDuration = 90;
        LocalTime updatedOpening = LocalTime.of(10, 0);
        LocalTime updatedClosing = LocalTime.of(20, 0);
        Integer updatedMinimumAge = 12;
        String updatedNotes = "Updated notes";
        UpdatePointOfInterestRequest request = updateRequest(
                updatedCategory,
                updatedDuration,
                updatedOpening,
                updatedClosing,
                updatedMinimumAge,
                updatedNotes
        );

        Mockito.when(repository.findById(1)).thenReturn(Optional.of(existing));
        Mockito.when(repository.save(existing)).thenReturn(existing);

        // Act
        PointOfInterest result = service.updatePointOfInterest(1, request);

        // Assert
        assertAll(
                () -> assertSame(existing, result),
                () -> assertSame(originalPlace, result.getPlace()),
                () -> assertEquals(updatedCategory, result.getPointOfInterestCategory()),
                () -> assertEquals(updatedDuration, result.getDurationMinutes()),
                () -> assertEquals(updatedOpening, result.getOpeningTime()),
                () -> assertEquals(updatedClosing, result.getClosingTime()),
                () -> assertEquals(updatedMinimumAge, result.getMinimumAge()),
                () -> assertEquals(updatedNotes, result.getNotes()),
                () -> assertEquals(1, result.getId()),
                () -> assertEquals(PLACE_NAME, result.getPlace().getPlaceName()),
                () -> assertEquals(PLACE_ID, result.getPlace().getPlaceId()),
                () -> assertEquals(FORMATTED_ADDRESS, result.getPlace().getFormattedAddress()),
                () -> assertEquals(CITY, result.getPlace().getCity()),
                () -> assertEquals(COUNTRY, result.getPlace().getCountry()),
                () -> assertEquals(LATITUDE, result.getPlace().getLatitude()),
                () -> assertEquals(LONGITUDE, result.getPlace().getLongitude())
        );

        Mockito.verify(repository).findById(1);
        Mockito.verify(repository, Mockito.times(1)).save(existing);
        Mockito.verify(googlePlacesClient, never()).searchPlace(anyString());
    }

    @Test
    void updatePointOfInterest_whenPointOfInterestDoesNotExist_throwsResourceNotFoundExceptionAndDoesNotSave() {
        // Arrange
        UpdatePointOfInterestRequest request = updateRequest(
                PointOfInterestCategory.RESTAURANT,
                90,
                LocalTime.of(10, 0),
                LocalTime.of(20, 0),
                12,
                "Updated notes"
        );

        Mockito.when(repository.findById(99)).thenReturn(Optional.empty());

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.updatePointOfInterest(99, request)
        );
        assertEquals("Point of interest not found", exception.getMessage());

        Mockito.verify(repository).findById(99);
        Mockito.verify(repository, never()).save(any(PointOfInterest.class));
        Mockito.verify(googlePlacesClient, never()).searchPlace(anyString());
    }
}
