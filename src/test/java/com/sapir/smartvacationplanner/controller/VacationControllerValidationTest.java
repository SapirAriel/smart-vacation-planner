package com.sapir.smartvacationplanner.controller;
import com.sapir.smartvacationplanner.exception.GlobalExceptionHandler;
import com.sapir.smartvacationplanner.service.VacationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;

@WebMvcTest(VacationController.class)
// Loads only the web layer for VacationController
// This includes request handling, JSON binding, validation, and response mapping
// It does not load the full application context

@Import(GlobalExceptionHandler.class)
// Adds the custom global exception handler to this test context
// This allows us to verify the actual error response shape returned by the API

class VacationControllerValidationTest {

    @Autowired
    private MockMvc mockMvc;
    // MockMvc is used to simulate HTTP requests without starting a real server

    @MockitoBean
    private VacationService vacationService;
    // Mocked service dependency
    // We are not testing service logic here, only controller/web behavior

    @Test
    void createVacation_whenRequiredFieldsAreMissing_returns400WithFieldErrors() throws Exception {
        // Scenario:
        // Send a POST request with an empty JSON body
        // Since all required fields are missing, validation should fail

        mockMvc.perform(post("/api/v1/vacations")
                        .contentType(MediaType.APPLICATION_JSON)
                        // Tell Spring that the request body is JSON
                        .content("{}"))
                        // Empty JSON body -> required fields are missing

                .andExpect(status().isBadRequest())
                // Expect HTTP 400 because request validation fails

                .andExpect(jsonPath("$.message").value("Validation failed"))
                // Verify the general error message in the JSON response

                .andExpect(jsonPath("$.status").value(400))
                // Verify the status field inside the JSON response

                .andExpect(jsonPath("$.path").value("/api/v1/vacations"))
                // Verify that the response includes the request path

                .andExpect(jsonPath("$.fieldErrors").isArray())
                // Verify that fieldErrors exists and is returned as an array

                .andExpect(jsonPath("$.fieldErrors.length()").value(8))
                // There are 8 required fields in CreateVacationRequest
                // Sending {} should produce 8 validation errors

                .andExpect(jsonPath("$.fieldErrors[?(@.field=='budget')].error").value("Budget is required"))
                .andExpect(jsonPath("$.fieldErrors[?(@.field=='pace')].error").value("Pace is required"));
    }
}