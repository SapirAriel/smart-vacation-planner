package com.sapir.smartvacationplanner.controller;

import com.sapir.smartvacationplanner.common.place.Place;
import com.sapir.smartvacationplanner.entity.Vacation;
import com.sapir.smartvacationplanner.entity.VacationDay;
import com.sapir.smartvacationplanner.entity.enums.DayType;
import com.sapir.smartvacationplanner.service.VacationDayService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;

import static org.hamcrest.Matchers.nullValue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(VacationDayController.class)
@AutoConfigureMockMvc(addFilters = false)
class VacationDayControllerResponseMappingTest {

    private static final Integer VACATION_ID = 10;
    private static final Integer DAY_ID = 1;
    private static final String DAY_PATH =
            "/api/v1/vacations/" + VACATION_ID + "/days/" + DAY_ID;
    private static final String HOTEL_NAME = "Test Hotel";

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private VacationDayService vacationDayService;

    private static Vacation parentVacation() {
        Vacation vacation = new Vacation();
        vacation.setId(VACATION_ID);
        return vacation;
    }

    private static VacationDay vacationDayWithHotel(Place hotelPlace) {
        VacationDay day = new VacationDay(
                parentVacation(),
                LocalDate.of(2026, 7, 1),
                1,
                DayType.DAY,
                hotelPlace
        );
        day.setId(DAY_ID);
        return day;
    }

    @Test
    void getVacationDayById_whenHotelPlaceExists_returnsHotelPlaceName() throws Exception {
        // Arrange
        VacationDay day = vacationDayWithHotel(new Place(HOTEL_NAME));
        when(vacationDayService.getVacationDayById(VACATION_ID, DAY_ID)).thenReturn(day);

        // Act + Assert
        mockMvc.perform(get(DAY_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(DAY_ID))
                .andExpect(jsonPath("$.vacationId").value(VACATION_ID))
                .andExpect(jsonPath("$.hotelPlaceName").value(HOTEL_NAME));

        verify(vacationDayService).getVacationDayById(VACATION_ID, DAY_ID);
    }

    @Test
    void getVacationDayById_whenHotelPlaceIsNull_returns200WithoutMappingFailure() throws Exception {
        // Arrange
        VacationDay day = vacationDayWithHotel(null);
        when(vacationDayService.getVacationDayById(VACATION_ID, DAY_ID)).thenReturn(day);

        // Act + Assert
        mockMvc.perform(get(DAY_PATH))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(DAY_ID))
                .andExpect(jsonPath("$.vacationId").value(VACATION_ID))
                .andExpect(jsonPath("$.hotelPlaceName").value(nullValue()));

        verify(vacationDayService).getVacationDayById(VACATION_ID, DAY_ID);
    }
}
