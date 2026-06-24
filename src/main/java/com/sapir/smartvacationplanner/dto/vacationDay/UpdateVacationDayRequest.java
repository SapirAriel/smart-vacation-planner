package com.sapir.smartvacationplanner.dto.vacationDay;
import jakarta.validation.constraints.NotNull;
import com.sapir.smartvacationplanner.entity.enums.DayType;

/**
 * UpdateVacationDayRequest is a DTO for updating a vacation day.
 * It is used to validate the request body for the update vacation day endpoint.
 */


public class UpdateVacationDayRequest {

    @NotNull(message = "Day type is required")
    private DayType dayType;

    @NotNull(message = "Hotel place name is required")
    private String hotelPlaceName;

    public DayType getDayType() {
        return dayType;
    }
    public String getHotelPlaceName() {
        return hotelPlaceName;
    }

    public void setDayType(DayType dayType) {
        this.dayType = dayType;
    }
    public void setHotelPlaceName(String hotelPlaceName) {
        this.hotelPlaceName = hotelPlaceName;
    }

} //UpdateVacationDayRequest