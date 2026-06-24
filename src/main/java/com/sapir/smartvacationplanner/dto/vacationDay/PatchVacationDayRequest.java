package com.sapir.smartvacationplanner.dto.vacationDay;
import com.sapir.smartvacationplanner.entity.enums.DayType;

/**
 * PatchVacationDayRequest is a DTO for patching a vacation day.
 * It is used to validate the request body for the patch vacation day endpoint.
 */


public class PatchVacationDayRequest {

    private DayType dayType;

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

} //PatchVacationDayRequest