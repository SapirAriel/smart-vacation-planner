package com.sapir.smartvacationplanner.dto.vacationDay;
import java.time.LocalDate;
import jakarta.validation.constraints.NotNull;
import com.sapir.smartvacationplanner.entity.enums.DayType;

public class CreateVacationDayRequest {

    @NotNull(message = "Date is required")
    private LocalDate date;

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

}