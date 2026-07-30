package com.sapir.smartvacationplanner.controller;

import com.sapir.smartvacationplanner.common.place.Place;
import com.sapir.smartvacationplanner.dto.PointOfInterest.CreatePointOfInterestRequest;
import com.sapir.smartvacationplanner.dto.PointOfInterest.UpdatePointOfInterestRequest;
import com.sapir.smartvacationplanner.entity.PointOfInterest;
import com.sapir.smartvacationplanner.entity.enums.PointOfInterestCategory;
import com.sapir.smartvacationplanner.security.SecurityConfig;
import com.sapir.smartvacationplanner.service.PointOfInterestService;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import javax.sql.DataSource;
import java.time.LocalTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = PointOfInterestController.class)
@Import(SecurityConfig.class)
class PointOfInterestSecurityTest {

    private static final String POI_BASE_PATH = "/api/v1/points-of-interest";
    private static final String PLACE_NAME = "Louvre Museum";
    private static final String PLACE_ID = "ChIJxxx";
    private static final String FORMATTED_ADDRESS = "Rue de Rivoli, 75001 Paris, France";
    private static final String CITY = "Paris";
    private static final String COUNTRY = "France";
    private static final Double LATITUDE = 48.86;
    private static final Double LONGITUDE = 2.33;
    private static final PointOfInterestCategory CATEGORY = PointOfInterestCategory.MUSEUM;
    private static final Integer DURATION_MINUTES = 120;
    private static final LocalTime OPENING_TIME = LocalTime.of(9, 0);
    private static final LocalTime CLOSING_TIME = LocalTime.of(18, 0);
    private static final Integer MINIMUM_AGE = 0;
    private static final String NOTES = "Book tickets in advance";

    private static final String VALID_CREATE_BODY = """
            {
              "pointOfInterestCategory": "MUSEUM",
              "placeName": "Louvre Museum",
              "durationMinutes": 120,
              "openingTime": "09:00:00",
              "closingTime": "18:00:00",
              "minimumAge": 0,
              "notes": "Book tickets in advance"
            }
            """;

    private static final String VALID_UPDATE_BODY = """
            {
              "pointOfInterestCategory": "MUSEUM",
              "durationMinutes": 120,
              "openingTime": "09:00:00",
              "closingTime": "18:00:00",
              "minimumAge": 0,
              "notes": "Book tickets in advance"
            }
            """;

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PointOfInterestService pointOfInterestService;

    @MockitoBean
    private DataSource dataSource;

    private static PointOfInterest samplePointOfInterest(Integer id) {
        Place place = new Place(
                PLACE_NAME,
                PLACE_ID,
                FORMATTED_ADDRESS,
                CITY,
                COUNTRY,
                LATITUDE,
                LONGITUDE
        );
        PointOfInterest poi = new PointOfInterest(
                CATEGORY,
                place,
                DURATION_MINUTES,
                OPENING_TIME,
                CLOSING_TIME,
                MINIMUM_AGE,
                NOTES
        );
        poi.setId(id);
        return poi;
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getAllPointOfInterests_whenCustomer_reachesServiceAndIsNotRejected() throws Exception {
        // Arrange
        PointOfInterest poi = samplePointOfInterest(1);
        when(pointOfInterestService.getAllPointOfInterests()).thenReturn(List.of(poi));

        // Act + Assert
        mockMvc.perform(get(POI_BASE_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].placeName").value(PLACE_NAME));

        verify(pointOfInterestService).getAllPointOfInterests();
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void getPointOfInterestById_whenCustomer_reachesServiceAndIsNotRejected() throws Exception {
        // Arrange
        PointOfInterest poi = samplePointOfInterest(1);
        when(pointOfInterestService.getPointOfInterestById(1)).thenReturn(poi);

        // Act + Assert
        mockMvc.perform(get(POI_BASE_PATH + "/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.placeName").value(PLACE_NAME))
                .andExpect(jsonPath("$.placeId").value(PLACE_ID));

        verify(pointOfInterestService).getPointOfInterestById(1);
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void createPointOfInterest_whenCustomer_returns403WithCustomAccessDeniedJsonAndDoesNotInvokeService()
            throws Exception {
        // Act + Assert
        mockMvc.perform(post(POI_BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_CREATE_BODY))
                .andExpect(status().isForbidden())
                .andExpect(jsonPath("$.message").value("You are not allowed to perform this action"))
                .andExpect(jsonPath("$.status").value(403));

        verify(pointOfInterestService, never())
                .createPointOfInterest(any(CreatePointOfInterestRequest.class));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void updatePointOfInterest_whenCustomer_returns403AndDoesNotInvokeService() throws Exception {
        // Act + Assert
        mockMvc.perform(put(POI_BASE_PATH + "/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_UPDATE_BODY))
                .andExpect(status().isForbidden());

        verify(pointOfInterestService, never())
                .updatePointOfInterest(anyInt(), any(UpdatePointOfInterestRequest.class));
    }

    @Test
    @WithMockUser(roles = "CUSTOMER")
    void deletePointOfInterest_whenCustomer_returns403AndDoesNotInvokeService() throws Exception {
        // Act + Assert
        mockMvc.perform(delete(POI_BASE_PATH + "/1"))
                .andExpect(status().isForbidden());

        verify(pointOfInterestService, never()).deletePointOfInterest(anyInt());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void createPointOfInterest_whenAdmin_reachesServiceAndIsNotForbidden() throws Exception {
        // Arrange
        PointOfInterest saved = samplePointOfInterest(10);
        when(pointOfInterestService.createPointOfInterest(any(CreatePointOfInterestRequest.class)))
                .thenReturn(saved);

        // Act + Assert
        mockMvc.perform(post(POI_BASE_PATH)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(VALID_CREATE_BODY))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(10))
                .andExpect(jsonPath("$.placeName").value(PLACE_NAME))
                .andExpect(jsonPath("$.pointOfInterestCategory").value("MUSEUM"))
                .andExpect(jsonPath("$.city").value(CITY));

        verify(pointOfInterestService, Mockito.times(1))
                .createPointOfInterest(ArgumentMatchers.any(CreatePointOfInterestRequest.class));
    }

    @Test
    void getAllPointOfInterests_whenUnauthenticated_returns401AndDoesNotInvokeService() throws Exception {
        // Act + Assert
        mockMvc.perform(get(POI_BASE_PATH))
                .andExpect(status().isUnauthorized());

        verify(pointOfInterestService, never()).getAllPointOfInterests();
        verify(pointOfInterestService, never()).getPointOfInterestById(anyInt());
        verify(pointOfInterestService, never())
                .createPointOfInterest(any(CreatePointOfInterestRequest.class));
        verify(pointOfInterestService, never())
                .updatePointOfInterest(anyInt(), any(UpdatePointOfInterestRequest.class));
        verify(pointOfInterestService, never()).deletePointOfInterest(anyInt());
    }
}
