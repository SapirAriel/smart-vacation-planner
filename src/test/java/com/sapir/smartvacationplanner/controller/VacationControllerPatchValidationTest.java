package com.sapir.smartvacationplanner.controller;

import com.sapir.smartvacationplanner.entity.Vacation;
import com.sapir.smartvacationplanner.entity.enums.Pace;
import com.sapir.smartvacationplanner.entity.enums.TravelerType;
import com.sapir.smartvacationplanner.exception.GlobalExceptionHandler;
import com.sapir.smartvacationplanner.service.VacationService;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VacationController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class VacationControllerPatchValidationTest {

    private static final Integer VACATION_ID = 1;
    private static final String VACATION_PATH = "/api/v1/vacations/" + VACATION_ID;
    private static final BigDecimal ORIGINAL_BUDGET = new BigDecimal("500");

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VacationService vacationService;

    private static Vacation existingVacation() {
        Vacation vacation = new Vacation();
        vacation.setId(VACATION_ID);
        vacation.setName("Test Vacation");
        vacation.setCountry("Test Country");
        vacation.setCity("Test City");
        vacation.setStartDate(LocalDate.of(2026, 5, 10));
        vacation.setEndDate(LocalDate.of(2026, 5, 11));
        vacation.setTravelerType(TravelerType.INDIVIDUAL);
        vacation.setBudget(ORIGINAL_BUDGET);
        vacation.setPace(Pace.BALANCED);
        return vacation;
    }

    @Test
    void patchVacation_whenBudgetIsZero_returns400AndDoesNotInvokeService() throws Exception {
        // Arrange — no service stubs; validation must reject before controller flow

        // Act + Assert
        mockMvc.perform(patch(VACATION_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "budget": 0
                                }
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Validation failed"))
                .andExpect(jsonPath("$.status").value(400))
                .andExpect(jsonPath("$.path").value(VACATION_PATH))
                .andExpect(jsonPath("$.fieldErrors[?(@.field=='budget')].error")
                        .value("Budget must be greater than 0"));

        verify(vacationService, never()).getVacationById(anyInt());
        verify(vacationService, never()).patchVacation(anyInt(), any(Vacation.class));
    }

    @Test
    void patchVacation_whenBudgetIsPositive_invokesService() throws Exception {
        // Arrange
        Vacation existing = existingVacation();
        Vacation saved = existingVacation();
        saved.setBudget(new BigDecimal("1000"));

        when(vacationService.getVacationById(VACATION_ID)).thenReturn(existing);
        when(vacationService.patchVacation(eq(VACATION_ID), any(Vacation.class))).thenReturn(saved);

        // Act + Assert
        mockMvc.perform(patch(VACATION_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "budget": 1000
                                }
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(VACATION_ID))
                .andExpect(jsonPath("$.budget").value(1000));

        verify(vacationService).getVacationById(VACATION_ID);

        ArgumentCaptor<Vacation> vacationCaptor = ArgumentCaptor.forClass(Vacation.class);
        verify(vacationService).patchVacation(eq(VACATION_ID), vacationCaptor.capture());
        assertEquals(0, vacationCaptor.getValue().getBudget().compareTo(new BigDecimal("1000")));
    }

    @Test
    void patchVacation_whenBudgetIsOmitted_doesNotFailBudgetValidation() throws Exception {
        // Arrange
        Vacation existing = existingVacation();
        Vacation saved = existingVacation();

        when(vacationService.getVacationById(VACATION_ID)).thenReturn(existing);
        when(vacationService.patchVacation(eq(VACATION_ID), any(Vacation.class))).thenReturn(saved);

        // Act + Assert
        mockMvc.perform(patch(VACATION_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(VACATION_ID))
                .andExpect(jsonPath("$.budget").value(500));

        verify(vacationService).getVacationById(VACATION_ID);

        ArgumentCaptor<Vacation> vacationCaptor = ArgumentCaptor.forClass(Vacation.class);
        verify(vacationService).patchVacation(eq(VACATION_ID), vacationCaptor.capture());
        assertEquals(0, vacationCaptor.getValue().getBudget().compareTo(ORIGINAL_BUDGET));
    }
}
