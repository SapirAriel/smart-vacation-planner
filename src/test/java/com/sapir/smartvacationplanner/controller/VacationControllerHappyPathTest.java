package com.sapir.smartvacationplanner.controller;

import com.sapir.smartvacationplanner.entity.Vacation;
import com.sapir.smartvacationplanner.entity.enums.TravelerType;
import com.sapir.smartvacationplanner.service.VacationService;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.sapir.smartvacationplanner.entity.enums.Pace;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VacationController.class)
// Loads only the web layer for VacationController
// This includes request handling, JSON binding, validation, and response mapping
// It does not load the full application context

class VacationControllerHappyPathTest {

    @Autowired
    private MockMvc mockMvc;
    // MockMvc is used to simulate HTTP requests without starting a real server

    @MockitoBean
    private VacationService vacationService;
    // Mocked service dependency
    // We are not testing service logic here, only controller/web behavior

    @Test
    void createVacation_whenValidBody_returns200WithIdAndName() throws Exception {
        // Arrange:
        // Create the Vacation object that the mocked service will return
        // This simulates the "saved" vacation coming back from the service layer

        Vacation savedVacation = new Vacation();
        savedVacation.setId(1);
        // Set an id to simulate a successful save in the database

        savedVacation.setName("Test Vacation");
        savedVacation.setCountry("Test Country");
        savedVacation.setCity("Test City");
        savedVacation.setStartDate(LocalDate.of(2026, 5, 10));
        savedVacation.setEndDate(LocalDate.of(2026, 5, 11));
        savedVacation.setTravelerType(TravelerType.INDIVIDUAL);
        savedVacation.setBudget(new BigDecimal("1000"));
        savedVacation.setPace(Pace.BALANCED);
        // Fill the returned object with data we expect to see in the response

        when(vacationService.createVacation(ArgumentMatchers.any(Vacation.class)))
                .thenReturn(savedVacation);
        // Define the behavior of the mocked VacationService
        // When createVacation(...) is called with any Vacation object,
        // return the savedVacation object we prepared above
        //
        // We use any(Vacation.class) because the controller creates a new Vacation object
        // from the request body, so we do not want to depend on one exact object instance

        // Act + Assert:
        // Send a valid POST request to create a vacation
        // Then verify the response status and selected JSON fields

        mockMvc.perform(post("/api/v1/vacations")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "name": "Test Vacation",
                                  "country": "Test Country",
                                  "city": "Test City",
                                  "startDate": "2026-05-10",
                                  "endDate": "2026-05-11",
                                  "travelerType": "INDIVIDUAL",
                                  "budget": 1000,
                                  "pace": "BALANCED"
                                }
                                """))
                .andExpect(status().isOk())
                // Verify that the controller returns HTTP 200 for a valid request

                .andExpect(jsonPath("$.id").value(1))
                // Verify that the response contains the id returned by the mocked service

                .andExpect(jsonPath("$.name").value("Test Vacation"));
                // Verify that the response contains the expected vacation name
    }
}