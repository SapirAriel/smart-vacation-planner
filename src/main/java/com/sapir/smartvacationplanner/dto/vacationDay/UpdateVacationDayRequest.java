package com.sapir.smartvacationplanner.dto.vacationDay;
import java.time.LocalDate;
import jakarta.validation.constraints.NotNull;
import com.sapir.smartvacationplanner.entity.enums.DayType;
import jakarta.validation.constraints.Positive;

/**
 * UpdateVacationDayRequest is a DTO for updating a vacation day.
 * It is used to validate the request body for the update vacation day endpoint.
 */


public class UpdateVacationDayRequest {

    @NotNull(message = "Date is required")
    private LocalDate date;

    @Positive(message = "Day number must be greater than 0")
    @NotNull(message = "Day number is required")
    private Integer dayNumber;

    @NotNull(message = "Day type is required")
    private DayType dayType;

    public LocalDate getDate() {
        return date;
    }
    public Integer getDayNumber() {
        return dayNumber;
    }

    public DayType getDayType() {
        return dayType;
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

} //UpdateVacationDayRequest