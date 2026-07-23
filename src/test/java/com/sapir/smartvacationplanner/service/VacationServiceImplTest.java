package com.sapir.smartvacationplanner.service;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;

import com.sapir.smartvacationplanner.entity.User;
import com.sapir.smartvacationplanner.entity.Vacation;
import com.sapir.smartvacationplanner.entity.enums.Role;
import com.sapir.smartvacationplanner.entity.enums.TravelerType;
import com.sapir.smartvacationplanner.exception.DuplicateResourceException;
import com.sapir.smartvacationplanner.repository.VacationRepository;
import com.sapir.smartvacationplanner.repository.VacationDayRepository;

class VacationServiceImplTest {

    private static VacationServiceImpl createService(
            VacationRepository repo,
            AuthorizationService authService
    ) {
        VacationDayRepository dayRepo = Mockito.mock(VacationDayRepository.class);
        return new VacationServiceImpl(repo, dayRepo, authService);
    }

    private static User createUser(Integer id) {
        User user = new User();
        user.setId(id);
        user.setEmail("user" + id + "@example.com");
        user.setRole(Role.CUSTOMER);
        return user;
    }

    @Test
    void createVacation_whenEndDateBeforeStartDate_throwsIllegalArgumentException() {
        // Arrange
        VacationRepository repo = Mockito.mock(VacationRepository.class);
        AuthorizationService authService = Mockito.mock(AuthorizationService.class);
        VacationServiceImpl service = createService(repo, authService);
        Vacation vacation = new Vacation();
        vacation.setName("Test Vacation");
        vacation.setCountry("Test Country");
        vacation.setCity("Test City");
        vacation.setTravelerType(TravelerType.INDIVIDUAL);

        // prepare input
        vacation.setStartDate(LocalDate.of(2026, 5, 10));
        vacation.setEndDate(LocalDate.of(2026, 5, 9));

        // Act + Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            service.createVacation(vacation);
        });

        assertEquals("endDate must be after or equal to startDate", ex.getMessage());
        Mockito.verify(repo, Mockito.never()).save(Mockito.any(Vacation.class));
    }

    @Test
    void createVacation_whenEndDateAfterStartDate_createsVacation() {
        // Arrange
        VacationRepository repo = Mockito.mock(VacationRepository.class);
        AuthorizationService authService = Mockito.mock(AuthorizationService.class);
        VacationServiceImpl service = createService(repo, authService);
        User currentUser = createUser(1);

        Vacation vacation = new Vacation();
        vacation.setName("Test Vacation");
        vacation.setCountry("Test Country");
        vacation.setCity("Test City");
        vacation.setTravelerType(TravelerType.INDIVIDUAL);

        // prepare input
        vacation.setStartDate(LocalDate.of(2026, 5, 10));
        vacation.setEndDate(LocalDate.of(2026, 5, 11));

        Mockito.when(authService.getCurrentUser()).thenReturn(currentUser);
        Mockito.when(repo.existsByUserIdAndName(currentUser.getId(), vacation.getName())).thenReturn(false);
        Mockito.when(repo.save(vacation)).thenReturn(vacation);

        // Act
        Vacation savedVacation = service.createVacation(vacation);

        // Assert
        assertNotNull(savedVacation);
        assertEquals("Test Vacation", savedVacation.getName());
        assertEquals("Test Country", savedVacation.getCountry());
        assertEquals("Test City", savedVacation.getCity());
        assertEquals(LocalDate.of(2026, 5, 10), savedVacation.getStartDate());
        assertEquals(LocalDate.of(2026, 5, 11), savedVacation.getEndDate());
        assertEquals(TravelerType.INDIVIDUAL, savedVacation.getTravelerType());
        assertEquals(currentUser, savedVacation.getUser());
        Mockito.verify(repo).existsByUserIdAndName(1, "Test Vacation");
        Mockito.verify(repo).save(vacation);
    }

    @Test
    void createVacation_whenNameAlreadyExistsForCurrentUser_throwsDuplicateResourceException() {
        // Arrange
        VacationRepository repo = Mockito.mock(VacationRepository.class);
        AuthorizationService authService = Mockito.mock(AuthorizationService.class);
        VacationServiceImpl service = createService(repo, authService);
        User currentUser = createUser(1);

        Vacation vacation = new Vacation();
        vacation.setName("Test Vacation");
        vacation.setCountry("Test Country");
        vacation.setCity("Test City");
        vacation.setTravelerType(TravelerType.INDIVIDUAL);
        vacation.setStartDate(LocalDate.of(2026, 5, 10));
        vacation.setEndDate(LocalDate.of(2026, 5, 11));

        Mockito.when(authService.getCurrentUser()).thenReturn(currentUser);
        Mockito.when(repo.existsByUserIdAndName(currentUser.getId(), vacation.getName())).thenReturn(true);

        // Act + Assert
        DuplicateResourceException ex = assertThrows(DuplicateResourceException.class, () -> {
            service.createVacation(vacation);
        });

        assertEquals("Vacation name already exists for this user", ex.getMessage());
        Mockito.verify(repo).existsByUserIdAndName(1, "Test Vacation");
        Mockito.verify(repo, Mockito.never()).save(Mockito.any(Vacation.class));
    }

    @Test
    void updateVacation_whenEndDateBeforeStartDate_throwsIllegalArgumentException() {
        // Arrange
        VacationRepository repo = Mockito.mock(VacationRepository.class);
        AuthorizationService authService = Mockito.mock(AuthorizationService.class);
        VacationServiceImpl service = createService(repo, authService);
        User owner = createUser(1);

        Vacation existingVacation = new Vacation();
        existingVacation.setId(1);
        existingVacation.setUser(owner);
        existingVacation.setName("Old Vacation");
        existingVacation.setCountry("Old Country");
        existingVacation.setCity("Old City");
        existingVacation.setTravelerType(TravelerType.COUPLE);
        existingVacation.setStartDate(LocalDate.of(2026, 5, 1));
        existingVacation.setEndDate(LocalDate.of(2026, 5, 5));

        Vacation vacation = new Vacation();
        vacation.setName("Test Vacation");
        vacation.setCountry("Test Country");
        vacation.setCity("Test City");
        vacation.setTravelerType(TravelerType.INDIVIDUAL);

        Mockito.when(authService.getVacationForCurrentUser(1)).thenReturn(existingVacation);
        Mockito.when(repo.existsByUserIdAndName(owner.getId(), vacation.getName())).thenReturn(false);

        // prepare input
        vacation.setStartDate(LocalDate.of(2026, 5, 10));
        vacation.setEndDate(LocalDate.of(2026, 5, 9));

        // Act + Assert
        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, () -> {
            service.updateVacation(1, vacation);
        });

        assertEquals("endDate must be after or equal to startDate", ex.getMessage());
        Mockito.verify(repo, Mockito.never()).save(Mockito.any(Vacation.class));
    }

    @Test
    void updateVacation_whenEndDateAfterStartDate_updatesVacation() {
        // Arrange
        VacationRepository repo = Mockito.mock(VacationRepository.class);
        AuthorizationService authService = Mockito.mock(AuthorizationService.class);
        VacationServiceImpl service = createService(repo, authService);
        User owner = createUser(1);

        Vacation existingVacation = new Vacation();
        existingVacation.setId(1);
        existingVacation.setUser(owner);
        existingVacation.setName("Old Vacation");
        existingVacation.setCountry("Old Country");
        existingVacation.setCity("Old City");
        existingVacation.setTravelerType(TravelerType.COUPLE);
        existingVacation.setStartDate(LocalDate.of(2026, 5, 1));
        existingVacation.setEndDate(LocalDate.of(2026, 5, 5));

        Vacation vacation = new Vacation();
        vacation.setName("Test Vacation");
        vacation.setCountry("Test Country");
        vacation.setCity("Test City");
        vacation.setTravelerType(TravelerType.INDIVIDUAL);

        Mockito.when(authService.getVacationForCurrentUser(1)).thenReturn(existingVacation);
        Mockito.when(repo.existsByUserIdAndName(owner.getId(), vacation.getName())).thenReturn(false);
        Mockito.when(repo.save(existingVacation)).thenReturn(existingVacation);

        // prepare input
        vacation.setStartDate(LocalDate.of(2026, 5, 10));
        vacation.setEndDate(LocalDate.of(2026, 5, 11));

        // Act
        Vacation updatedVacation = service.updateVacation(1, vacation);

        // Assert
        assertNotNull(updatedVacation);
        assertEquals(1, updatedVacation.getId());
        assertEquals(owner, updatedVacation.getUser());
        assertEquals("Test Vacation", updatedVacation.getName());
        assertEquals("Test Country", updatedVacation.getCountry());
        assertEquals("Test City", updatedVacation.getCity());
        assertEquals(LocalDate.of(2026, 5, 10), updatedVacation.getStartDate());
        assertEquals(LocalDate.of(2026, 5, 11), updatedVacation.getEndDate());
        assertEquals(TravelerType.INDIVIDUAL, updatedVacation.getTravelerType());
        Mockito.verify(authService).getVacationForCurrentUser(1);
        Mockito.verify(repo).existsByUserIdAndName(1, "Test Vacation");
        Mockito.verify(repo).save(existingVacation);
    }

    @Test
    void updateVacation_whenNameAlreadyExistsForSameUser_throwsDuplicateResourceException() {
        // Arrange
        VacationRepository repo = Mockito.mock(VacationRepository.class);
        AuthorizationService authService = Mockito.mock(AuthorizationService.class);
        VacationServiceImpl service = createService(repo, authService);
        User owner = createUser(1);

        Vacation existingVacation = new Vacation();
        existingVacation.setId(1);
        existingVacation.setUser(owner);
        existingVacation.setName("Old Vacation");
        existingVacation.setCountry("Old Country");
        existingVacation.setCity("Old City");
        existingVacation.setTravelerType(TravelerType.COUPLE);
        existingVacation.setStartDate(LocalDate.of(2026, 5, 1));
        existingVacation.setEndDate(LocalDate.of(2026, 5, 5));

        Vacation vacation = new Vacation();
        vacation.setName("Taken Name");
        vacation.setCountry("Test Country");
        vacation.setCity("Test City");
        vacation.setTravelerType(TravelerType.INDIVIDUAL);
        vacation.setStartDate(LocalDate.of(2026, 5, 10));
        vacation.setEndDate(LocalDate.of(2026, 5, 11));

        Mockito.when(authService.getVacationForCurrentUser(1)).thenReturn(existingVacation);
        Mockito.when(repo.existsByUserIdAndName(owner.getId(), vacation.getName())).thenReturn(true);

        // Act + Assert
        DuplicateResourceException ex = assertThrows(DuplicateResourceException.class, () -> {
            service.updateVacation(1, vacation);
        });

        assertEquals("Vacation name already exists for this user", ex.getMessage());
        Mockito.verify(repo).existsByUserIdAndName(1, "Taken Name");
        Mockito.verify(repo, Mockito.never()).save(Mockito.any(Vacation.class));
    }

    @Test
    void updateVacation_whenNameUnchanged_updatesVacationWithoutDuplicateCheck() {
        // Arrange
        VacationRepository repo = Mockito.mock(VacationRepository.class);
        AuthorizationService authService = Mockito.mock(AuthorizationService.class);
        VacationServiceImpl service = createService(repo, authService);
        User owner = createUser(1);

        Vacation existingVacation = new Vacation();
        existingVacation.setId(1);
        existingVacation.setUser(owner);
        existingVacation.setName("Same Name");
        existingVacation.setCountry("Old Country");
        existingVacation.setCity("Old City");
        existingVacation.setTravelerType(TravelerType.COUPLE);
        existingVacation.setStartDate(LocalDate.of(2026, 5, 1));
        existingVacation.setEndDate(LocalDate.of(2026, 5, 5));

        Vacation vacation = new Vacation();
        vacation.setName("Same Name");
        vacation.setCountry("New Country");
        vacation.setCity("New City");
        vacation.setTravelerType(TravelerType.INDIVIDUAL);
        vacation.setStartDate(LocalDate.of(2026, 5, 10));
        vacation.setEndDate(LocalDate.of(2026, 5, 11));

        Mockito.when(authService.getVacationForCurrentUser(1)).thenReturn(existingVacation);
        Mockito.when(repo.save(existingVacation)).thenReturn(existingVacation);

        // Act
        Vacation updatedVacation = service.updateVacation(1, vacation);

        // Assert
        assertEquals(1, updatedVacation.getId());
        assertEquals("Same Name", updatedVacation.getName());
        assertEquals("New Country", updatedVacation.getCountry());
        assertEquals("New City", updatedVacation.getCity());
        assertEquals(LocalDate.of(2026, 5, 10), updatedVacation.getStartDate());
        assertEquals(LocalDate.of(2026, 5, 11), updatedVacation.getEndDate());
        assertEquals(TravelerType.INDIVIDUAL, updatedVacation.getTravelerType());
        Mockito.verify(repo, Mockito.never()).existsByUserIdAndName(anyInt(), anyString());
        Mockito.verify(repo).save(existingVacation);
    }
}
