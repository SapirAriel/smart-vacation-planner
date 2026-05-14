package com.sapir.smartvacationplanner.dto.vacationDay;
import java.time.LocalDate;
import com.sapir.smartvacationplanner.entity.enums.DayType;
import jakarta.validation.constraints.Positive;

/**
 * PatchVacationDayRequest is a DTO for patching a vacation day.
 * It is used to validate the request body for the patch vacation day endpoint.
 */


public class PatchVacationDayRequest {

    private LocalDate date;

    @Positive(message = "Day number must be greater than 0")
    private Integer dayNumber;

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

} //PatchVacationDayRequest