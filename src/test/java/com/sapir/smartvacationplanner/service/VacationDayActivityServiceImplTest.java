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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;

import com.sapir.smartvacationplanner.entity.PointOfInterest;
import com.sapir.smartvacationplanner.entity.VacationDay;
import com.sapir.smartvacationplanner.entity.VacationDayActivity;
import com.sapir.smartvacationplanner.exception.DuplicateResourceException;
import com.sapir.smartvacationplanner.exception.ResourceNotFoundException;
import com.sapir.smartvacationplanner.repository.VacationDayActivityRepository;
import org.springframework.security.access.AccessDeniedException;

@ExtendWith(MockitoExtension.class)
class VacationDayActivityServiceImplTest {

    private static final Integer VACATION_ID = 10;
    private static final Integer VACATION_DAY_ID = 20;
    private static final Integer POINT_OF_INTEREST_ID = 30;
    private static final Integer REPLACEMENT_POINT_OF_INTEREST_ID = 31;
    private static final Integer OTHER_POINT_OF_INTEREST_ID = 32;
    private static final Integer ACTIVITY_ID = 40;
    private static final Integer OTHER_ACTIVITY_ID = 41;
    private static final Integer MISSING_POINT_OF_INTEREST_ID = 999;
    private static final String ACCESS_DENIED_MESSAGE =
            "Access denied for vacation with id: " + VACATION_ID;
    private static final String ACTIVITY_NOT_FOUND_MESSAGE =
            "Vacation day activity not found with id: " + ACTIVITY_ID;
    private static final String DUPLICATE_POI_MESSAGE =
            "Point of interest is already assigned to this vacation day";

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
    void createVacationDayActivity_whenOwnedDayAndExistingPointOfInterest_savesActivityAndInvalidatesScheduling() {
        // Arrange
        VacationDay vacationDay = new VacationDay();
        vacationDay.setId(VACATION_DAY_ID);

        PointOfInterest pointOfInterest = new PointOfInterest();
        pointOfInterest.setId(POINT_OF_INTEREST_ID);

        Mockito.when(authorizationService.getVacationDayForCurrentUser(VACATION_ID, VACATION_DAY_ID))
                .thenReturn(vacationDay);
        Mockito.when(pointOfInterestService.getPointOfInterestById(POINT_OF_INTEREST_ID))
                .thenReturn(pointOfInterest);
        Mockito.when(activityRepository.existsByVacationDay_IdAndPointOfInterest_Id(
                VACATION_DAY_ID, POINT_OF_INTEREST_ID
        )).thenReturn(false);
        Mockito.when(activityRepository.save(any(VacationDayActivity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(activityRepository.clearPlanningDataByVacationDayId(VACATION_DAY_ID))
                .thenReturn(1);

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
                () -> assertTrue(vacationDay.getVacationDayActivities().contains(saved)),
                () -> assertEquals(1, vacationDay.getVacationDayActivities().size()),
                () -> assertNull(saved.getPlannedStartTime()),
                () -> assertNull(saved.getPlannedEndTime()),
                () -> assertEquals(0, saved.getTravelMinutesFromPrevious()),
                () -> assertEquals(0.0, saved.getDistanceKmFromPrevious())
        );

        Mockito.verify(authorizationService).getVacationDayForCurrentUser(VACATION_ID, VACATION_DAY_ID);
        Mockito.verify(pointOfInterestService).getPointOfInterestById(POINT_OF_INTEREST_ID);
        Mockito.verify(activityRepository).existsByVacationDay_IdAndPointOfInterest_Id(
                VACATION_DAY_ID, POINT_OF_INTEREST_ID
        );
        Mockito.verify(activityRepository, Mockito.times(1)).save(saved);
        Mockito.verify(activityRepository, Mockito.times(1))
                .clearPlanningDataByVacationDayId(VACATION_DAY_ID);
        Mockito.verify(activityRepository, never()).delete(any(VacationDayActivity.class));
    }

    @Test
    void createVacationDayActivity_whenPointOfInterestAlreadyAssignedToDay_throwsDuplicateResourceExceptionAndDoesNotSaveOrInvalidate() {
        // Arrange
        VacationDay vacationDay = new VacationDay();
        vacationDay.setId(VACATION_DAY_ID);

        PointOfInterest pointOfInterest = new PointOfInterest();
        pointOfInterest.setId(POINT_OF_INTEREST_ID);

        Mockito.when(authorizationService.getVacationDayForCurrentUser(VACATION_ID, VACATION_DAY_ID))
                .thenReturn(vacationDay);
        Mockito.when(pointOfInterestService.getPointOfInterestById(POINT_OF_INTEREST_ID))
                .thenReturn(pointOfInterest);
        Mockito.when(activityRepository.existsByVacationDay_IdAndPointOfInterest_Id(
                VACATION_DAY_ID, POINT_OF_INTEREST_ID
        )).thenReturn(true);

        // Act & Assert
        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> service.createVacationDayActivity(
                        VACATION_ID,
                        VACATION_DAY_ID,
                        POINT_OF_INTEREST_ID
                )
        );
        assertEquals(DUPLICATE_POI_MESSAGE, exception.getMessage());
        assertTrue(vacationDay.getVacationDayActivities().isEmpty());

        Mockito.verify(activityRepository).existsByVacationDay_IdAndPointOfInterest_Id(
                VACATION_DAY_ID, POINT_OF_INTEREST_ID
        );
        Mockito.verify(activityRepository, never()).save(any(VacationDayActivity.class));
        Mockito.verify(activityRepository, never()).clearPlanningDataByVacationDayId(anyInt());
        Mockito.verify(activityRepository, never()).delete(any(VacationDayActivity.class));
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
        assertTrue(vacationDay.getVacationDayActivities().isEmpty());

        Mockito.verify(authorizationService).getVacationDayForCurrentUser(VACATION_ID, VACATION_DAY_ID);
        Mockito.verify(pointOfInterestService).getPointOfInterestById(MISSING_POINT_OF_INTEREST_ID);
        Mockito.verify(activityRepository, never())
                .existsByVacationDay_IdAndPointOfInterest_Id(anyInt(), anyInt());
        Mockito.verify(activityRepository, never()).save(any(VacationDayActivity.class));
        Mockito.verify(activityRepository, never()).clearPlanningDataByVacationDayId(anyInt());
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
        Mockito.verify(activityRepository, never())
                .existsByVacationDay_IdAndPointOfInterest_Id(anyInt(), anyInt());
        Mockito.verify(activityRepository, never()).save(any(VacationDayActivity.class));
        Mockito.verify(activityRepository, never()).clearPlanningDataByVacationDayId(anyInt());
    }

    @Test
    void createVacationDayActivity_whenClearPlanningDataThrows_propagatesException() {
        // Arrange
        VacationDay vacationDay = new VacationDay();
        vacationDay.setId(VACATION_DAY_ID);

        PointOfInterest pointOfInterest = new PointOfInterest();
        pointOfInterest.setId(POINT_OF_INTEREST_ID);

        Mockito.when(authorizationService.getVacationDayForCurrentUser(VACATION_ID, VACATION_DAY_ID))
                .thenReturn(vacationDay);
        Mockito.when(pointOfInterestService.getPointOfInterestById(POINT_OF_INTEREST_ID))
                .thenReturn(pointOfInterest);
        Mockito.when(activityRepository.existsByVacationDay_IdAndPointOfInterest_Id(
                VACATION_DAY_ID, POINT_OF_INTEREST_ID
        )).thenReturn(false);
        Mockito.when(activityRepository.save(any(VacationDayActivity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));
        Mockito.when(activityRepository.clearPlanningDataByVacationDayId(VACATION_DAY_ID))
                .thenThrow(new RuntimeException("clear planning data failed"));

        // Act & Assert
        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> service.createVacationDayActivity(
                        VACATION_ID,
                        VACATION_DAY_ID,
                        POINT_OF_INTEREST_ID
                )
        );
        assertEquals("clear planning data failed", exception.getMessage());

        Mockito.verify(activityRepository, Mockito.times(1)).save(any(VacationDayActivity.class));
        Mockito.verify(activityRepository, Mockito.times(1))
                .clearPlanningDataByVacationDayId(VACATION_DAY_ID);
    }

    @Test
    void updateVacationDayActivity_whenAccessibleActivityAndExistingPointOfInterest_replacesPointOfInterestAndInvalidatesScheduling() {
        // Arrange
        VacationDay vacationDay = new VacationDay();
        vacationDay.setId(VACATION_DAY_ID);

        PointOfInterest originalPointOfInterest = new PointOfInterest();
        originalPointOfInterest.setId(POINT_OF_INTEREST_ID);

        VacationDayActivity existing = new VacationDayActivity(vacationDay, originalPointOfInterest);
        existing.setId(ACTIVITY_ID);
        existing.setPlannedStartTime(LocalTime.of(9, 0));
        existing.setPlannedEndTime(LocalTime.of(11, 0));
        existing.setTravelMinutesFromPrevious(15);
        existing.setDistanceKmFromPrevious(2.5);
        vacationDay.addActivity(existing);

        PointOfInterest replacementPointOfInterest = new PointOfInterest();
        replacementPointOfInterest.setId(REPLACEMENT_POINT_OF_INTEREST_ID);

        Mockito.when(authorizationService.getVacationDayActivityForCurrentUser(
                VACATION_ID, VACATION_DAY_ID, ACTIVITY_ID
        )).thenReturn(existing);
        Mockito.when(pointOfInterestService.getPointOfInterestById(REPLACEMENT_POINT_OF_INTEREST_ID))
                .thenReturn(replacementPointOfInterest);
        Mockito.when(activityRepository.existsByVacationDay_IdAndPointOfInterest_IdAndIdNot(
                VACATION_DAY_ID, REPLACEMENT_POINT_OF_INTEREST_ID, ACTIVITY_ID
        )).thenReturn(false);
        Mockito.when(activityRepository.save(existing)).thenReturn(existing);
        Mockito.when(activityRepository.clearPlanningDataByVacationDayId(VACATION_DAY_ID))
                .thenReturn(1);

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
                () -> assertEquals(1, vacationDay.getVacationDayActivities().size()),
                () -> assertTrue(vacationDay.getVacationDayActivities().contains(existing)),
                () -> assertNull(result.getPlannedStartTime()),
                () -> assertNull(result.getPlannedEndTime()),
                () -> assertEquals(0, result.getTravelMinutesFromPrevious()),
                () -> assertEquals(0.0, result.getDistanceKmFromPrevious())
        );

        Mockito.verify(authorizationService).getVacationDayActivityForCurrentUser(
                VACATION_ID, VACATION_DAY_ID, ACTIVITY_ID
        );
        Mockito.verify(pointOfInterestService).getPointOfInterestById(REPLACEMENT_POINT_OF_INTEREST_ID);
        Mockito.verify(activityRepository).existsByVacationDay_IdAndPointOfInterest_IdAndIdNot(
                VACATION_DAY_ID, REPLACEMENT_POINT_OF_INTEREST_ID, ACTIVITY_ID
        );
        Mockito.verify(activityRepository, Mockito.times(1)).save(existing);
        Mockito.verify(activityRepository, Mockito.times(1))
                .clearPlanningDataByVacationDayId(VACATION_DAY_ID);
    }

    @Test
    void updateVacationDayActivity_whenPointOfInterestAlreadyUsedByAnotherActivity_throwsDuplicateResourceExceptionAndDoesNotMutate() {
        // Arrange
        VacationDay vacationDay = new VacationDay();
        vacationDay.setId(VACATION_DAY_ID);

        PointOfInterest originalPointOfInterest = new PointOfInterest();
        originalPointOfInterest.setId(POINT_OF_INTEREST_ID);

        PointOfInterest otherPointOfInterest = new PointOfInterest();
        otherPointOfInterest.setId(OTHER_POINT_OF_INTEREST_ID);

        VacationDayActivity existing = new VacationDayActivity(vacationDay, originalPointOfInterest);
        existing.setId(ACTIVITY_ID);
        existing.setPlannedStartTime(LocalTime.of(9, 0));
        existing.setTravelMinutesFromPrevious(15);
        vacationDay.addActivity(existing);

        VacationDayActivity other = new VacationDayActivity(vacationDay, otherPointOfInterest);
        other.setId(OTHER_ACTIVITY_ID);
        vacationDay.addActivity(other);

        PointOfInterest replacementPointOfInterest = new PointOfInterest();
        replacementPointOfInterest.setId(OTHER_POINT_OF_INTEREST_ID);

        Mockito.when(authorizationService.getVacationDayActivityForCurrentUser(
                VACATION_ID, VACATION_DAY_ID, ACTIVITY_ID
        )).thenReturn(existing);
        Mockito.when(pointOfInterestService.getPointOfInterestById(OTHER_POINT_OF_INTEREST_ID))
                .thenReturn(replacementPointOfInterest);
        Mockito.when(activityRepository.existsByVacationDay_IdAndPointOfInterest_IdAndIdNot(
                VACATION_DAY_ID, OTHER_POINT_OF_INTEREST_ID, ACTIVITY_ID
        )).thenReturn(true);

        // Act & Assert
        DuplicateResourceException exception = assertThrows(
                DuplicateResourceException.class,
                () -> service.updateVacationDayActivity(
                        VACATION_ID,
                        VACATION_DAY_ID,
                        ACTIVITY_ID,
                        OTHER_POINT_OF_INTEREST_ID
                )
        );
        assertEquals(DUPLICATE_POI_MESSAGE, exception.getMessage());

        assertAll(
                () -> assertSame(originalPointOfInterest, existing.getPointOfInterest()),
                () -> assertEquals(2, vacationDay.getVacationDayActivities().size()),
                () -> assertEquals(LocalTime.of(9, 0), existing.getPlannedStartTime()),
                () -> assertEquals(15, existing.getTravelMinutesFromPrevious())
        );

        Mockito.verify(activityRepository).existsByVacationDay_IdAndPointOfInterest_IdAndIdNot(
                eq(VACATION_DAY_ID), eq(OTHER_POINT_OF_INTEREST_ID), eq(ACTIVITY_ID)
        );
        Mockito.verify(activityRepository, never()).save(any(VacationDayActivity.class));
        Mockito.verify(activityRepository, never()).clearPlanningDataByVacationDayId(anyInt());
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
        Mockito.verify(activityRepository, never())
                .existsByVacationDay_IdAndPointOfInterest_IdAndIdNot(anyInt(), anyInt(), anyInt());
        Mockito.verify(activityRepository, never()).save(any(VacationDayActivity.class));
        Mockito.verify(activityRepository, never()).clearPlanningDataByVacationDayId(anyInt());
    }

    @Test
    void deleteVacationDayActivity_whenActivityIsAccessible_removesFromCollectionAndInvalidatesScheduling() {
        // Arrange
        VacationDay vacationDay = new VacationDay();
        vacationDay.setId(VACATION_DAY_ID);

        VacationDayActivity existing = new VacationDayActivity();
        existing.setId(ACTIVITY_ID);
        vacationDay.addActivity(existing);

        Mockito.when(authorizationService.getVacationDayActivityForCurrentUser(
                VACATION_ID, VACATION_DAY_ID, ACTIVITY_ID
        )).thenReturn(existing);
        Mockito.when(activityRepository.clearPlanningDataByVacationDayId(VACATION_DAY_ID))
                .thenReturn(0);

        // Act
        service.deleteVacationDayActivity(VACATION_ID, VACATION_DAY_ID, ACTIVITY_ID);

        // Assert
        assertAll(
                () -> assertFalse(vacationDay.getVacationDayActivities().contains(existing)),
                () -> assertEquals(0, vacationDay.getVacationDayActivities().size()),
                () -> assertSame(vacationDay, existing.getVacationDay())
        );

        Mockito.verify(authorizationService).getVacationDayActivityForCurrentUser(
                VACATION_ID, VACATION_DAY_ID, ACTIVITY_ID
        );
        Mockito.verify(activityRepository, never()).delete(any(VacationDayActivity.class));
        Mockito.verify(activityRepository, Mockito.times(1))
                .clearPlanningDataByVacationDayId(VACATION_DAY_ID);
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
        Mockito.verify(activityRepository, never()).clearPlanningDataByVacationDayId(anyInt());
        Mockito.verify(pointOfInterestService, never()).getPointOfInterestById(anyInt());
    }
}
