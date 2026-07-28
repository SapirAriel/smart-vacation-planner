package com.sapir.smartvacationplanner.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;

import com.sapir.smartvacationplanner.entity.PointOfInterest;
import com.sapir.smartvacationplanner.entity.VacationDay;
import com.sapir.smartvacationplanner.entity.VacationDayActivity;
import com.sapir.smartvacationplanner.exception.ResourceNotFoundException;
import com.sapir.smartvacationplanner.repository.VacationDayActivityRepository;
import org.springframework.security.access.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
class VacationDayActivityServiceImplTest {

    private static final Integer VACATION_ID = 10;
    private static final Integer VACATION_DAY_ID = 20;
    private static final Integer POINT_OF_INTEREST_ID = 30;
    private static final Integer REPLACEMENT_POINT_OF_INTEREST_ID = 31;
    private static final Integer ACTIVITY_ID = 40;
    private static final Integer MISSING_POINT_OF_INTEREST_ID = 999;
    private static final String ACCESS_DENIED_MESSAGE =
            "Access denied for vacation with id: " + VACATION_ID;
    private static final String ACTIVITY_NOT_FOUND_MESSAGE =
            "Vacation day activity not found with id: " + ACTIVITY_ID;

    @Mock
    private VacationDayActivityRepository activityRepository;

    @Mock
    private AuthorizationService authorizationService;

    @Mock
    private PointOfInterestService pointOfInterestService;

    private VacationDayActivityServiceImpl service;

    @BeforeEach
    void setUp() {
        service = new VacationDayActivityServiceImpl(
                activityRepository,
                authorizationService,
                pointOfInterestService
        );
    }

    @Test
    void createVacationDayActivity_whenOwnedDayAndExistingPointOfInterest_savesActivityWithNullSchedulingFields() {
        // Arrange
        VacationDay vacationDay = new VacationDay();
        vacationDay.setId(VACATION_DAY_ID);

        PointOfInterest pointOfInterest = new PointOfInterest();
        pointOfInterest.setId(POINT_OF_INTEREST_ID);

        Mockito.when(authorizationService.getVacationDayForCurrentUser(VACATION_ID, VACATION_DAY_ID))
                .thenReturn(vacationDay);
        Mockito.when(pointOfInterestService.getPointOfInterestById(POINT_OF_INTEREST_ID))
                .thenReturn(pointOfInterest);
        Mockito.when(activityRepository.save(any(VacationDayActivity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // Act
        VacationDayActivity saved = service.createVacationDayActivity(
                VACATION_ID,
                VACATION_DAY_ID,
                POINT_OF_INTEREST_ID
        );

        // Assert
        assertAll(
                () -> assertSame(vacationDay, saved.getVacationDay()),
                () -> assertSame(pointOfInterest, saved.getPointOfInterest()),
                () -> assertNull(saved.getPlannedStartTime()),
                () -> assertNull(saved.getPlannedEndTime()),
                () -> assertNull(saved.getTravelMinutesFromPrevious()),
                () -> assertNull(saved.getDistanceKmFromPrevious())
        );

        Mockito.verify(authorizationService).getVacationDayForCurrentUser(VACATION_ID, VACATION_DAY_ID);
        Mockito.verify(pointOfInterestService).getPointOfInterestById(POINT_OF_INTEREST_ID);
        Mockito.verify(activityRepository, Mockito.times(1)).save(saved);
    }

    @Test
    void createVacationDayActivity_whenPointOfInterestDoesNotExist_throwsResourceNotFoundExceptionAndDoesNotSave() {
        // Arrange
        VacationDay vacationDay = new VacationDay();
        vacationDay.setId(VACATION_DAY_ID);

        Mockito.when(authorizationService.getVacationDayForCurrentUser(VACATION_ID, VACATION_DAY_ID))
                .thenReturn(vacationDay);
        Mockito.when(pointOfInterestService.getPointOfInterestById(MISSING_POINT_OF_INTEREST_ID))
                .thenThrow(new ResourceNotFoundException("Point of interest not found"));

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.createVacationDayActivity(
                        VACATION_ID,
                        VACATION_DAY_ID,
                        MISSING_POINT_OF_INTEREST_ID
                )
        );
        assertEquals("Point of interest not found", exception.getMessage());

        Mockito.verify(authorizationService).getVacationDayForCurrentUser(VACATION_ID, VACATION_DAY_ID);
        Mockito.verify(pointOfInterestService).getPointOfInterestById(MISSING_POINT_OF_INTEREST_ID);
        Mockito.verify(activityRepository, never()).save(any(VacationDayActivity.class));
    }

    @Test
    void createVacationDayActivity_whenVacationBelongsToAnotherUser_throwsAccessDeniedExceptionAndDoesNotLookupPointOfInterest() {
        // Arrange
        Mockito.when(authorizationService.getVacationDayForCurrentUser(VACATION_ID, VACATION_DAY_ID))
                .thenThrow(new AccessDeniedException(ACCESS_DENIED_MESSAGE));

        // Act & Assert
        AccessDeniedException exception = assertThrows(
                AccessDeniedException.class,
                () -> service.createVacationDayActivity(
                        VACATION_ID,
                        VACATION_DAY_ID,
                        POINT_OF_INTEREST_ID
                )
        );
        assertEquals(ACCESS_DENIED_MESSAGE, exception.getMessage());

        Mockito.verify(authorizationService).getVacationDayForCurrentUser(VACATION_ID, VACATION_DAY_ID);
        Mockito.verify(pointOfInterestService, never()).getPointOfInterestById(anyInt());
        Mockito.verify(activityRepository, never()).save(any(VacationDayActivity.class));
    }

    @Test
    void updateVacationDayActivity_whenAccessibleActivityAndExistingPointOfInterest_replacesPointOfInterestAndLeavesSchedulingUnchanged() {
        // Arrange
        VacationDay vacationDay = new VacationDay();
        vacationDay.setId(VACATION_DAY_ID);

        PointOfInterest originalPointOfInterest = new PointOfInterest();
        originalPointOfInterest.setId(POINT_OF_INTEREST_ID);

        LocalTime originalPlannedStartTime = LocalTime.of(9, 0);
        LocalTime originalPlannedEndTime = LocalTime.of(11, 0);
        Integer originalTravelMinutesFromPrevious = 15;
        Double originalDistanceKmFromPrevious = 2.5;

        VacationDayActivity existing = new VacationDayActivity(vacationDay, originalPointOfInterest);
        existing.setId(ACTIVITY_ID);
        existing.setPlannedStartTime(originalPlannedStartTime);
        existing.setPlannedEndTime(originalPlannedEndTime);
        existing.setTravelMinutesFromPrevious(originalTravelMinutesFromPrevious);
        existing.setDistanceKmFromPrevious(originalDistanceKmFromPrevious);

        PointOfInterest replacementPointOfInterest = new PointOfInterest();
        replacementPointOfInterest.setId(REPLACEMENT_POINT_OF_INTEREST_ID);

        Mockito.when(authorizationService.getVacationDayActivityForCurrentUser(
                VACATION_ID, VACATION_DAY_ID, ACTIVITY_ID
        )).thenReturn(existing);
        Mockito.when(pointOfInterestService.getPointOfInterestById(REPLACEMENT_POINT_OF_INTEREST_ID))
                .thenReturn(replacementPointOfInterest);
        Mockito.when(activityRepository.save(existing)).thenReturn(existing);

        // Act
        VacationDayActivity result = service.updateVacationDayActivity(
                VACATION_ID,
                VACATION_DAY_ID,
                ACTIVITY_ID,
                REPLACEMENT_POINT_OF_INTEREST_ID
        );

        // Assert
        assertAll(
                () -> assertSame(existing, result),
                () -> assertSame(replacementPointOfInterest, result.getPointOfInterest()),
                () -> assertSame(vacationDay, result.getVacationDay()),
                () -> assertEquals(ACTIVITY_ID, result.getId()),
                () -> assertEquals(originalPlannedStartTime, result.getPlannedStartTime()),
                () -> assertEquals(originalPlannedEndTime, result.getPlannedEndTime()),
                () -> assertEquals(originalTravelMinutesFromPrevious, result.getTravelMinutesFromPrevious()),
                () -> assertEquals(originalDistanceKmFromPrevious, result.getDistanceKmFromPrevious())
        );

        Mockito.verify(authorizationService).getVacationDayActivityForCurrentUser(
                VACATION_ID, VACATION_DAY_ID, ACTIVITY_ID
        );
        Mockito.verify(pointOfInterestService).getPointOfInterestById(REPLACEMENT_POINT_OF_INTEREST_ID);
        Mockito.verify(activityRepository, Mockito.times(1)).save(existing);
    }

    @Test
    void updateVacationDayActivity_whenActivityCannotBeAccessed_throwsResourceNotFoundExceptionAndDoesNotLookupPointOfInterest() {
        // Arrange
        Mockito.when(authorizationService.getVacationDayActivityForCurrentUser(
                VACATION_ID, VACATION_DAY_ID, ACTIVITY_ID
        )).thenThrow(new ResourceNotFoundException(ACTIVITY_NOT_FOUND_MESSAGE));

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.updateVacationDayActivity(
                        VACATION_ID,
                        VACATION_DAY_ID,
                        ACTIVITY_ID,
                        REPLACEMENT_POINT_OF_INTEREST_ID
                )
        );
        assertEquals(ACTIVITY_NOT_FOUND_MESSAGE, exception.getMessage());

        Mockito.verify(authorizationService).getVacationDayActivityForCurrentUser(
                VACATION_ID, VACATION_DAY_ID, ACTIVITY_ID
        );
        Mockito.verify(pointOfInterestService, never()).getPointOfInterestById(anyInt());
        Mockito.verify(activityRepository, never()).save(any(VacationDayActivity.class));
    }

    @Test
    void deleteVacationDayActivity_whenActivityIsAccessible_deletesExistingActivity() {
        // Arrange
        VacationDayActivity existing = new VacationDayActivity();
        existing.setId(ACTIVITY_ID);

        Mockito.when(authorizationService.getVacationDayActivityForCurrentUser(
                VACATION_ID, VACATION_DAY_ID, ACTIVITY_ID
        )).thenReturn(existing);

        // Act
        service.deleteVacationDayActivity(VACATION_ID, VACATION_DAY_ID, ACTIVITY_ID);

        // Assert
        Mockito.verify(authorizationService).getVacationDayActivityForCurrentUser(
                VACATION_ID, VACATION_DAY_ID, ACTIVITY_ID
        );
        Mockito.verify(activityRepository, Mockito.times(1)).delete(existing);
        Mockito.verify(pointOfInterestService, never()).getPointOfInterestById(anyInt());
        Mockito.verify(activityRepository, never()).save(any(VacationDayActivity.class));
    }

    @Test
    void deleteVacationDayActivity_whenActivityCannotBeAccessed_throwsResourceNotFoundExceptionAndDoesNotDelete() {
        // Arrange
        Mockito.when(authorizationService.getVacationDayActivityForCurrentUser(
                VACATION_ID, VACATION_DAY_ID, ACTIVITY_ID
        )).thenThrow(new ResourceNotFoundException(ACTIVITY_NOT_FOUND_MESSAGE));

        // Act & Assert
        ResourceNotFoundException exception = assertThrows(
                ResourceNotFoundException.class,
                () -> service.deleteVacationDayActivity(VACATION_ID, VACATION_DAY_ID, ACTIVITY_ID)
        );
        assertEquals(ACTIVITY_NOT_FOUND_MESSAGE, exception.getMessage());

        Mockito.verify(authorizationService).getVacationDayActivityForCurrentUser(
                VACATION_ID, VACATION_DAY_ID, ACTIVITY_ID
        );
        Mockito.verify(activityRepository, never()).delete(any(VacationDayActivity.class));
        Mockito.verify(activityRepository, never()).save(any(VacationDayActivity.class));
        Mockito.verify(pointOfInterestService, never()).getPointOfInterestById(anyInt());
    }
}
