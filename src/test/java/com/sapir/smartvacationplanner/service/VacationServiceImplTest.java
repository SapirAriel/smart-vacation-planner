package com.sapir.smartvacationplanner.service;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import java.time.LocalDate;
import static org.junit.jupiter.api.Assertions.*;
import com.sapir.smartvacationplanner.entity.Vacation;
import com.sapir.smartvacationplanner.entity.enums.TravelerType;
import com.sapir.smartvacationplanner.repository.VacationRepository;
import com.sapir.smartvacationplanner.repository.VacationDayRepository;

class VacationServiceImplTest {

    private static VacationServiceImpl createService(VacationRepository repo) {
        VacationDayRepository dayRepo = Mockito.mock(VacationDayRepository.class);
        return new VacationServiceImpl(repo, dayRepo);
    }

    @Test
    void createVacation_whenEndDateBeforeStartDate_throwsIllegalArgumentException() {
        // Arrange
        VacationRepository repo = Mockito.mock(VacationRepository.class);
        VacationServiceImpl service = createService(repo);
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
        VacationServiceImpl service = createService(repo);
        Vacation vacation = new Vacation();
        vacation.setName("Test Vacation");
        vacation.setCountry("Test Country");
        vacation.setCity("Test City");
        vacation.setTravelerType(TravelerType.INDIVIDUAL);

        // prepare input
        vacation.setStartDate(LocalDate.of(2026, 5, 10));
        vacation.setEndDate(LocalDate.of(2026, 5, 11));
        Mockito.when(repo.save(vacation)).thenReturn(vacation);

        // Act
        Vacation savedVacation = service.createVacation(vacation);

        // Assert
        assertNotNull(savedVacation);
        Mockito.verify(repo).save(vacation);
    }

    @Test
    void updateVacation_whenEndDateBeforeStartDate_throwsIllegalArgumentException() {
        // Arrange
        VacationRepository repo = Mockito.mock(VacationRepository.class);
        VacationServiceImpl service = createService(repo);
        Vacation existingVacation = new Vacation();
        existingVacation.setId(1);
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

        Mockito.when(repo.findById(1)).thenReturn(java.util.Optional.of(existingVacation));

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
        VacationServiceImpl service = createService(repo);
        
        Vacation existingVacation = new Vacation();
        existingVacation.setId(1);
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

        Mockito.when(repo.findById(1)).thenReturn(java.util.Optional.of(existingVacation));
        Mockito.when(repo.save(Mockito.any(Vacation.class))).thenReturn(vacation);

        // prepare input
        vacation.setStartDate(LocalDate.of(2026, 5, 10));
        vacation.setEndDate(LocalDate.of(2026, 5, 11));

        // Act
        Vacation updatedVacation = service.updateVacation(1, vacation);

        // Assert
        assertNotNull(updatedVacation);
        Mockito.verify(repo).findById(1);
        Mockito.verify(repo).save(Mockito.any(Vacation.class));
    }
    
}