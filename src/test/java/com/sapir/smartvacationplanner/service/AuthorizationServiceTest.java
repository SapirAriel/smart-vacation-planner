package com.sapir.smartvacationplanner.service;

import com.sapir.smartvacationplanner.entity.User;
import com.sapir.smartvacationplanner.entity.Vacation;
import com.sapir.smartvacationplanner.entity.enums.Role;
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

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AuthorizationServiceTest {

    @AfterEach
    void clearSecurityContext() {
        SecurityContextHolder.clearContext();
    }

    @Test
    void shouldRejectAccessWhenVacationBelongsToAnotherUser() {
        // Arrange
        User owner = createUser(1, "owner@example.com");
        User otherUser = createUser(2, "other@example.com");
        Vacation vacation = createVacation(10, owner);

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

        assertEquals("Access denied for vacation with id: 10", ex.getMessage());
    }

    @Test
    void shouldAllowAccessWhenVacationBelongsToCurrentUser() {
        // Arrange
        User owner = createUser(1, "owner@example.com");
        Vacation vacation = createVacation(10, owner);

        UserRepository userRepository = Mockito.mock(UserRepository.class);
        VacationRepository vacationRepository = Mockito.mock(VacationRepository.class);
        AuthorizationService authorizationService = createService(userRepository, vacationRepository);

        authenticateAs(owner.getEmail());
        Mockito.when(userRepository.findByEmail(owner.getEmail())).thenReturn(owner);
        Mockito.when(vacationRepository.findById(vacation.getId())).thenReturn(Optional.of(vacation));

        // Act
        Vacation result = authorizationService.getVacationForCurrentUser(vacation.getId());

        // Assert
        assertEquals(10, result.getId());
        assertEquals(1, result.getUser().getId());
    }

    private static AuthorizationService createService(
            UserRepository userRepository,
            VacationRepository vacationRepository
    ) {
        VacationDayRepository vacationDayRepository = Mockito.mock(VacationDayRepository.class);
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
