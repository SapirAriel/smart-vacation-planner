package com.sapir.smartvacationplanner.dto.itinerary;
import java.util.List;

public class ItineraryResponse {

    private Integer vacationId;
    private String vacationName;
    private List<DayScheduleResponse> days;

    public ItineraryResponse() {
    }

    public ItineraryResponse(Integer vacationId, String vacationName, List<DayScheduleResponse> days) {
        this.vacationId = vacationId;
        this.vacationName = vacationName;
        this.days = days;
    }
    public Integer getVacationId() {
        return vacationId;
    }
    public String getVacationName() {
        return vacationName;
    }
    public List<DayScheduleResponse> getDays() {
        return days;
    }

    public void setVacationId(Integer vacationId) {
        this.vacationId = vacationId;
    }
    public void setVacationName(String vacationName) {
        this.vacationName = vacationName;
    }
    public void setDays(List<DayScheduleResponse> days) {
        this.days = days;
    }

    public void addDay(DayScheduleResponse day) {
        this.days.add(day);
    }

    @Override
    public String toString() {
        return "ItineraryResponse{" +
            "vacationId=" + vacationId +
            ", vacationName=" + vacationName +
            ", days=" + days +
            '}';
    }

}
