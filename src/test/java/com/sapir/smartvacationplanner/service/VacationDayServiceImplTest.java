package com.sapir.smartvacationplanner.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

import com.sapir.smartvacationplanner.dto.vacationDay.CreateVacationDayRequest;
import com.sapir.smartvacationplanner.entity.Vacation;
import com.sapir.smartvacationplanner.entity.VacationDay;
import com.sapir.smartvacationplanner.entity.enums.DayType;
import com.sapir.smartvacationplanner.exception.DuplicateResourceException;
import com.sapir.smartvacationplanner.integration.google.GooglePlacesClient;
import com.sapir.smartvacationplanner.integration.google.dto.PlaceResult;
import com.sapir.smartvacationplanner.repository.VacationDayActivityRepository;
import com.sapir.smartvacationplanner.repository.VacationDayRepository;
import org.springframework.security.access.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
class VacationDayServiceImplTest {

    private static final Integer VACATION_ID = 10;
    private static final LocalDate VACATION_START = LocalDate.of(2026, 7, 1);
    private static final LocalDate VACATION_END = LocalDate.of(2026, 7, 5);
    private static final String HOTEL_NAME = "Test Hotel";
    private static final String ACCESS_DENIED_MESSAGE =
            "Access denied for vacation with id: " + VACATION_ID;

    @Mock
    private VacationDayRepository dayRepo;

    @Mock
    private VacationDayActivityRepository activityRepo;

    @Mock
    private AuthorizationService authService;

    @Mock
    private GooglePlacesClient googlePlacesClient;

    private VacationDayServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new VacationDayServiceImpl(
                dayRepo,
                activityRepo,
                authService,
                googlePlacesClient
        );
    }

    private static Vacation createVacation(Integer id, LocalDate startDate, LocalDate endDate) {
        Vacation vacation = new Vacation();
        vacation.setId(id);
        vacation.setStartDate(startDate);
        vacation.setEndDate(endDate);
        return vacation;
    }

    private static CreateVacationDayRequest createRequest(LocalDate date, int dayNumber) {
        CreateVacationDayRequest request = new CreateVacationDayRequest();
        request.setDate(date);
        request.setDayNumber(dayNumber);
        request.setDayType(DayType.DAY);
        request.setHotelPlaceName(HOTEL_NAME);
        return request;
    }

    private static PlaceResult placeResult() {
        return new PlaceResult(
                "place-1",
                "1 Test Street",
                1.0,
                2.0,
                "Test City",
                "Test Country"
        );
    }

    private void stubSuccessfulPreconditions(
            Vacation vacation,
            CreateVacationDayRequest request
    ) {
        Mockito.when(dayRepo.existsByVacationIdAndDayNumber(VACATION_ID, request.getDayNumber()))
                .thenReturn(false);
        Mockito.when(dayRepo.existsByVacationIdAndDate(VACATION_ID, request.getDate()))
                .thenReturn(false);
        Mockito.when(authService.getVacationForCurrentUser(VACATION_ID)).thenReturn(vacation);
        Mockito.when(googlePlacesClient.searchPlace(HOTEL_NAME)).thenReturn(placeResult());
    }

    @Test
    void createVacationDay_whenDateEqualsVacationStartDate_createsVacationDay() {
        // Arrange
        Vacation vacation = createVacation(VACATION_ID, VACATION_START, VACATION_END);
        CreateVacationDayRequest request = createRequest(VACATION_START, 1);
        stubSuccessfulPreconditions(vacation, request);

        Mockito.when(dayRepo.save(any(VacationDay.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        VacationDay saved = service.createVacationDay(VACATION_ID, request);

        // Assert
        assertNotNull(saved);
        assertNotNull(saved.getHotelPlace());
        assertAll(
                () -> assertEquals(VACATION_START, saved.getDate()),
                () -> assertEquals(1, saved.getDayNumber()),
                () -> assertEquals(vacation, saved.getVacation()),
                () -> assertEquals(DayType.DAY, saved.getDayType()),
                () -> assertEquals(HOTEL_NAME, saved.getHotelPlace().getPlaceName()),
                () -> assertEquals("place-1", saved.getHotelPlace().getPlaceId()),
                () -> assertEquals("1 Test Street", saved.getHotelPlace().getFormattedAddress()),
                () -> assertEquals("Test City", saved.getHotelPlace().getCity()),
                () -> assertEquals("Test Country", saved.getHotelPlace().getCountry()),
                () -> assertEquals(1.0, saved.getHotelPlace().getLatitude()),
                () -> assertEquals(2.0, saved.getHotelPlace().getLongitude())
        );
        Mockito.verify(dayRepo, Mockito.times(1)).save(saved);
        Mockito.verify(dayRepo).existsByVacationIdAndDayNumber(VACATION_ID, request.getDayNumber());
        Mockito.verify(dayRepo).existsByVacationIdAndDate(VACATION_ID, request.getDate());
    }

    @Test
    void createVacationDay_whenDateEqualsVacationEndDate_createsVacationDay() {
        // Arrange
        Vacation vacation = createVacation(VACATION_ID, VACATION_START, VACATION_END);
        CreateVacationDayRequest request = createRequest(VACATION_END, 5);
        stubSuccessfulPreconditions(vacation, request);

        Mockito.when(dayRepo.save(any(VacationDay.class))).thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        VacationDay saved = service.createVacationDay(VACATION_ID, request);

        // Assert
        assertNotNull(saved);
        assertNotNull(saved.getHotelPlace());
        assertAll(
                () -> assertEquals(VACATION_END, saved.getDate()),
                () -> assertEquals(5, saved.getDayNumber()),
                () -> assertEquals(vacation, saved.getVacation()),
                () -> assertEquals(DayType.DAY, saved.getDayType()),
                () -> assertEquals(HOTEL_NAME, saved.getHotelPlace().getPlaceName()),
                () -> assertEquals("place-1", saved.getHotelPlace().getPlaceId()),
                () -> assertEquals("1 Test Street", saved.getHotelPlace().getFormattedAddress()),
                () -> assertEquals("Test City", saved.getHotelPlace().getCity()),
                () -> assertEquals("Test Country", saved.getHotelPlace().getCountry()),
                () -> assertEquals(1.0, saved.getHotelPlace().getLatitude()),
                () -> assertEquals(2.0, saved.getHotelPlace().getLongitude())
        );
        Mockito.verify(dayRepo, Mockito.times(1)).save(saved);
        Mockito.verify(dayRepo).existsByVacationIdAndDayNumber(VACATION_ID, request.getDayNumber());
        Mockito.verify(dayRepo).existsByVacationIdAndDate(VACATION_ID, request.getDate());
    }

    @Test
    void createVacationDay_whenDateBeforeVacationStartDate_throwsIllegalArgumentException() {
        // Arrange
        Vacation vacation = createVacation(VACATION_ID, VACATION_START, VACATION_END);
        LocalDate dateBeforeStart = LocalDate.of(2026, 6, 30);
        CreateVacationDayRequest request = createRequest(dateBeforeStart, 1);
        Mockito.when(authService.getVacationForCurrentUser(VACATION_ID)).thenReturn(vacation);

        // Act + Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            service.createVacationDay(VACATION_ID, request);
        });

        assertEquals("Date must be on or after vacation startDate", ex.getMessage());
        Mockito.verify(dayRepo, Mockito.never()).existsByVacationIdAndDayNumber(anyInt(), anyInt());
        Mockito.verify(dayRepo, Mockito.never()).existsByVacationIdAndDate(anyInt(), any(LocalDate.class));
        Mockito.verify(googlePlacesClient, Mockito.never()).searchPlace(anyString());
        Mockito.verify(dayRepo, Mockito.never()).save(any(VacationDay.class));
    }

    @Test
    void createVacationDay_whenDateAfterVacationEndDate_throwsIllegalArgumentException() {
        // Arrange
        Vacation vacation = createVacation(VACATION_ID, VACATION_START, VACATION_END);
        LocalDate dateAfterEnd = LocalDate.of(2026, 7, 6);
        CreateVacationDayRequest request = createRequest(dateAfterEnd, 1);
        Mockito.when(authService.getVacationForCurrentUser(VACATION_ID)).thenReturn(vacation);

        // Act + Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            service.createVacationDay(VACATION_ID, request);
        });

        assertEquals("Date must be on or before vacation endDate", ex.getMessage());
        Mockito.verify(dayRepo, Mockito.never()).existsByVacationIdAndDayNumber(anyInt(), anyInt());
        Mockito.verify(dayRepo, Mockito.never()).existsByVacationIdAndDate(anyInt(), any(LocalDate.class));
        Mockito.verify(googlePlacesClient, Mockito.never()).searchPlace(anyString());
        Mockito.verify(dayRepo, Mockito.never()).save(any(VacationDay.class));
    }

    @Test
    void createVacationDay_whenDayNumberAlreadyExists_throwsDuplicateResourceException() {
        // Arrange
        Vacation vacation = createVacation(VACATION_ID, VACATION_START, VACATION_END);
        CreateVacationDayRequest request = createRequest(VACATION_START, 1);

        Mockito.when(dayRepo.existsByVacationIdAndDayNumber(VACATION_ID, 1)).thenReturn(true);
        Mockito.when(authService.getVacationForCurrentUser(VACATION_ID)).thenReturn(vacation);

        // Act + Assert
        DuplicateResourceException ex = assertThrows(DuplicateResourceException.class, () -> {
            service.createVacationDay(VACATION_ID, request);
        });

        assertEquals("Day number already exists for this vacation", ex.getMessage());
        Mockito.verify(dayRepo).existsByVacationIdAndDayNumber(VACATION_ID, 1);
        Mockito.verify(googlePlacesClient, Mockito.never()).searchPlace(anyString());
        Mockito.verify(dayRepo, Mockito.never()).save(any(VacationDay.class));
    }

    @Test
    void createVacationDay_whenDateAlreadyExists_throwsDuplicateResourceException() {
        // Arrange
        Vacation vacation = createVacation(VACATION_ID, VACATION_START, VACATION_END);
        LocalDate duplicateDate = LocalDate.of(2026, 7, 2);
        CreateVacationDayRequest request = createRequest(duplicateDate, 2);

        Mockito.when(dayRepo.existsByVacationIdAndDayNumber(VACATION_ID, 2)).thenReturn(false);
        Mockito.when(dayRepo.existsByVacationIdAndDate(VACATION_ID, duplicateDate)).thenReturn(true);
        Mockito.when(authService.getVacationForCurrentUser(VACATION_ID)).thenReturn(vacation);

        // Act + Assert
        DuplicateResourceException ex = assertThrows(DuplicateResourceException.class, () -> {
            service.createVacationDay(VACATION_ID, request);
        });

        assertEquals("Date already exists for this vacation", ex.getMessage());
        Mockito.verify(dayRepo).existsByVacationIdAndDayNumber(VACATION_ID, 2);
        Mockito.verify(dayRepo).existsByVacationIdAndDate(VACATION_ID, duplicateDate);
        Mockito.verify(googlePlacesClient, Mockito.never()).searchPlace(anyString());
        Mockito.verify(dayRepo, Mockito.never()).save(any(VacationDay.class));
    }

    @Test
    void createVacationDay_whenDayNumberIsZero_throwsIllegalArgumentException() {
        // Arrange
        Vacation vacation = createVacation(VACATION_ID, VACATION_START, VACATION_END);
        LocalDate inRangeDate = LocalDate.of(2026, 7, 2);
        CreateVacationDayRequest request = createRequest(inRangeDate, 0);
        Mockito.when(authService.getVacationForCurrentUser(VACATION_ID)).thenReturn(vacation);

        // Act + Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            service.createVacationDay(VACATION_ID, request);
        });

        assertEquals("Day number must be greater than 0", ex.getMessage());
        Mockito.verify(dayRepo, Mockito.never()).existsByVacationIdAndDayNumber(anyInt(), anyInt());
        Mockito.verify(dayRepo, Mockito.never()).existsByVacationIdAndDate(anyInt(), any(LocalDate.class));
        Mockito.verify(googlePlacesClient, Mockito.never()).searchPlace(anyString());
        Mockito.verify(dayRepo, Mockito.never()).save(any(VacationDay.class));
    }

    @Test
    void createVacationDay_whenDayNumberExceedsVacationDuration_throwsIllegalArgumentException() {
        // Arrange
        Vacation vacation = createVacation(VACATION_ID, VACATION_START, VACATION_END);
        LocalDate inRangeDate = VACATION_START.plusDays(1);
        CreateVacationDayRequest request = createRequest(inRangeDate, 6);
        Mockito.when(authService.getVacationForCurrentUser(VACATION_ID)).thenReturn(vacation);

        // Act + Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            service.createVacationDay(VACATION_ID, request);
        });

        assertEquals("Day number must be less than or equal to vacation duration", ex.getMessage());
        Mockito.verify(dayRepo, Mockito.never()).existsByVacationIdAndDayNumber(anyInt(), anyInt());
        Mockito.verify(dayRepo, Mockito.never()).existsByVacationIdAndDate(anyInt(), any(LocalDate.class));
        Mockito.verify(googlePlacesClient, Mockito.never()).searchPlace(anyString());
        Mockito.verify(dayRepo, Mockito.never()).save(any(VacationDay.class));
    }

    @Test
    void createVacationDay_whenVacationBelongsToAnotherUser_throwsAccessDeniedException() {
        // Arrange
        CreateVacationDayRequest request = createRequest(VACATION_START, 1);

        Mockito.when(authService.getVacationForCurrentUser(VACATION_ID))
                .thenThrow(new AccessDeniedException(ACCESS_DENIED_MESSAGE));

        // Act + Assert
        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () -> {
            service.createVacationDay(VACATION_ID, request);
        });

        assertEquals(ACCESS_DENIED_MESSAGE, ex.getMessage());
        Mockito.verify(authService).getVacationForCurrentUser(VACATION_ID);
        Mockito.verify(googlePlacesClient, Mockito.never()).searchPlace(anyString());
        Mockito.verify(dayRepo, Mockito.never()).save(any(VacationDay.class));
    }

    @Test
    void createVacationDay_whenUnauthorizedAndDuplicateDataExists_throwsAccessDeniedException() {
        // Arrange
        CreateVacationDayRequest request = createRequest(VACATION_START, 1);

        Mockito.when(authService.getVacationForCurrentUser(VACATION_ID))
                .thenThrow(new AccessDeniedException(ACCESS_DENIED_MESSAGE));

        // Act + Assert
        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () -> {
            service.createVacationDay(VACATION_ID, request);
        });

        assertEquals(ACCESS_DENIED_MESSAGE, ex.getMessage());
        Mockito.verify(authService).getVacationForCurrentUser(VACATION_ID);
        Mockito.verify(dayRepo, Mockito.never()).existsByVacationIdAndDayNumber(anyInt(), anyInt());
        Mockito.verify(dayRepo, Mockito.never()).existsByVacationIdAndDate(anyInt(), any(LocalDate.class));
        Mockito.verify(googlePlacesClient, Mockito.never()).searchPlace(anyString());
        Mockito.verify(dayRepo, Mockito.never()).save(any(VacationDay.class));
    }
}
