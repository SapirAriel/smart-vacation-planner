package com.sapir.smartvacationplanner.dto.vacationDay;
import java.time.LocalDate;
import jakarta.validation.constraints.NotNull;
import com.sapir.smartvacationplanner.entity.enums.DayType;
import jakarta.validation.constraints.Positive;

/**
 * CreateVacationDayRequest is a DTO for creating a new vacation day.
 * It is used to validate the request body for the create vacation day endpoint.
 */

public class CreateVacationDayRequest {

    @NotNull(message = "Date is required")
    private LocalDate date;

    @Positive(message = "Day number must be greater than 0")
    @NotNull(message = "Day number is required")
    private Integer dayNumber;

    @NotNull(message = "Day type is required")
    private DayType dayType;

    @NotNull(message = "Hotel place name is required")
    private String hotelPlaceName;

    public LocalDate getDate() {
        return date;
    }
    public Integer getDayNumber() {
        return dayNumber;
    }

    public DayType getDayType() {
        return dayType;
    }
    public String getHotelPlaceName() {
        return hotelPlaceName;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }
    public void setDayNumber(Integer dayNumber) {
        this.dayNumber = dayNumber;
    }
    public void setDayType(DayType dayType) {
        this.dayType = dayType;
    }
    public void setHotelPlaceName(String hotelPlaceName) {
        this.hotelPlaceName = hotelPlaceName;
    }

} //CreateVacationDayRequest