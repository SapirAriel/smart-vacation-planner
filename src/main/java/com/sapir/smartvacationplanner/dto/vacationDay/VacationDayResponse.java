package com.sapir.smartvacationplanner.dto.vacationDay;
import java.time.LocalDate;
import com.sapir.smartvacationplanner.entity.enums.DayType;

/**
 * VacationDayResponse is a DTO for returning a vacation day.
 * It is used to return the vacation day details to the client.
 */

public class VacationDayResponse {
    private Integer id;
    private Integer vacationId;
    private LocalDate date;
    private Integer dayNumber;
    private DayType dayType;
    private String hotelPlaceName;

    public Integer getId() {
        return id;
    }
    public Integer getVacationId() {
        return vacationId;
    }
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
    public void setId(Integer id) {
        this.id = id;
    }
    public void setVacationId(Integer vacationId) {
        this.vacationId = vacationId;
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

    public String toString() {
        return "VacationDayResponse{" +
            "id=" + id +
            ", vacationId=" + vacationId +
            ", date=" + date +
            ", dayNumber=" + dayNumber +
            ", dayType=" + dayType +
            ", hotelPlaceName=" + hotelPlaceName +
            '}';
    }
} //VacationDayResponse
