package com.sapir.smartvacationplanner.service;

import com.sapir.smartvacationplanner.entity.User;
import com.sapir.smartvacationplanner.entity.Vacation;
import com.sapir.smartvacationplanner.entity.enums.Role;
import com.sapir.smartvacationplanner.exception.ResourceNotFoundException;
import com.sapir.smartvacationplanner.repository.UserRepository;
import com.sapir.smartvacationplanner.repository.VacationDayActivityRepository;
import com.sapir.smartvacationplanner.repository.VacationDayRepository;
import com.sapir.smartvacationplanner.repository.VacationRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.TestingAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.eq;

class AuthorizationServiceTest {

    private static final Integer VACATION_ID = 10;
    private static final Integer VACATION_DAY_ID = 20;

    private static final String ACCESS_DENIED_MESSAGE =
            "Access denied for vacation with id: " + VACATION_ID;

    private static final String VACATION_DAY_NOT_FOUND_MESSAGE =
            "Vacation day not found with id: " + VACATION_DAY_ID;

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldRejectAccessWhenVacationBelongsToAnotherUser() {
        // Arrange
        User owner = createUser(1, "owner@example.com");
        User otherUser = createUser(2, "other@example.com");
        Vacation vacation = createVacation(VACATION_ID, owner);

        UserRepository userRepository = Mockito.mock(UserRepository.class);
        VacationRepository vacationRepository = Mockito.mock(VacationRepository.class);
        AuthorizationService authorizationService = createService(userRepository, vacationRepository);

        authenticateAs(otherUser.getEmail());
        Mockito.when(userRepository.findByEmail(otherUser.getEmail())).thenReturn(otherUser);
        Mockito.when(vacationRepository.findById(vacation.getId())).thenReturn(Optional.of(vacation));

        // Act + Assert
        AccessDeniedException ex = assertThrows(AccessDeniedException.class, () -> {
            authorizationService.getVacationForCurrentUser(vacation.getId());
        });

        assertEquals(ACCESS_DENIED_MESSAGE, ex.getMessage());
    }

    @Test
    void shouldAllowAccessWhenVacationBelongsToCurrentUser() {
        // Arrange
        User owner = createUser(1, "owner@example.com");
        Vacation vacation = createVacation(VACATION_ID, owner);

        UserRepository userRepository = Mockito.mock(UserRepository.class);
        VacationRepository vacationRepository = Mockito.mock(VacationRepository.class);
        AuthorizationService authorizationService = createService(userRepository, vacationRepository);

        authenticateAs(owner.getEmail());
        Mockito.when(userRepository.findByEmail(owner.getEmail())).thenReturn(owner);
        Mockito.when(vacationRepository.findById(vacation.getId())).thenReturn(Optional.of(vacation));

        // Act
        Vacation result = authorizationService.getVacationForCurrentUser(vacation.getId());

        // Assert
        assertAll(
                () -> assertEquals(VACATION_ID, result.getId()),
                () -> assertEquals(1, result.getUser().getId())
        );
    }

    @Test
    void getVacationDayForCurrentUser_whenDayDoesNotBelongToVacation_throwsResourceNotFoundException() {
        // Arrange
        User owner = createUser(1, "owner@example.com");
        Vacation vacation = createVacation(VACATION_ID, owner);

        UserRepository userRepository = Mockito.mock(UserRepository.class);
        VacationRepository vacationRepository = Mockito.mock(VacationRepository.class);
        VacationDayRepository vacationDayRepository = Mockito.mock(VacationDayRepository.class);
        AuthorizationService authorizationService = createService(
                userRepository,
                vacationRepository,
                vacationDayRepository
        );

        authenticateAs(owner.getEmail());
        Mockito.when(userRepository.findByEmail(owner.getEmail())).thenReturn(owner);
        Mockito.when(vacationRepository.findById(VACATION_ID)).thenReturn(Optional.of(vacation));
        Mockito.when(vacationDayRepository.findByVacationAndId(vacation, VACATION_DAY_ID))
                .thenReturn(Optional.empty());

        // Act + Assert
        ResourceNotFoundException ex = assertThrows(ResourceNotFoundException.class, () -> {
            authorizationService.getVacationDayForCurrentUser(VACATION_ID, VACATION_DAY_ID);
        });

        assertEquals(VACATION_DAY_NOT_FOUND_MESSAGE, ex.getMessage());
        Mockito.verify(vacationDayRepository).findByVacationAndId(eq(vacation), eq(VACATION_DAY_ID));
    }

    private static AuthorizationService createService(
            UserRepository userRepository,
            VacationRepository vacationRepository
    ) {
        return createService(
                userRepository,
                vacationRepository,
                Mockito.mock(VacationDayRepository.class)
        );
    }

    private static AuthorizationService createService(
            UserRepository userRepository,
            VacationRepository vacationRepository,
            VacationDayRepository vacationDayRepository
    ) {
        VacationDayActivityRepository vacationDayActivityRepository =
                Mockito.mock(VacationDayActivityRepository.class);
        return new AuthorizationService(
                userRepository,
                vacationRepository,
                vacationDayRepository,
                vacationDayActivityRepository
        );
    }

    private static User createUser(Integer id, String email) {
        User user = new User();
        user.setId(id);
        user.setEmail(email);
        user.setRole(Role.CUSTOMER);
        return user;
    }

    private static Vacation createVacation(Integer id, User owner) {
        Vacation vacation = new Vacation();
        vacation.setId(id);
        vacation.setUser(owner);
        return vacation;
    }

    private static void authenticateAs(String email) {
        SecurityContextHolder.getContext().setAuthentication(
                new TestingAuthenticationToken(email, "password", "ROLE_CUSTOMER")
        );
    }
}
