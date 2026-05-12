package com.sapir.smartvacationplanner.dto.vacationDay;
import java.time.LocalDate;
import com.sapir.smartvacationplanner.entity.enums.DayType;


public class PatchVacationDayRequest {

    private LocalDate date;

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

}