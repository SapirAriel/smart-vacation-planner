package com.sapir.smartvacationplanner.dto.itinerary;
import java.time.LocalDate;
import java.util.List;

public class DayScheduleResponse {

    private Integer dayNumber;
    private LocalDate date;
    private List<ScheduledActivityResponse> activities;

    public DayScheduleResponse() {
    }

    public DayScheduleResponse(Integer dayNumber, LocalDate date, List<ScheduledActivityResponse> activities) {
        this.dayNumber = dayNumber;
        this.date = date;
        this.activities = activities;
    }

    public Integer getDayNumber() {
        return dayNumber;
    }
    public LocalDate getDate() {
        return date;
    }
    public List<ScheduledActivityResponse> getActivities() {
        return activities;
    }

    public void setDayNumber(Integer dayNumber) {
        this.dayNumber = dayNumber;
    }
    public void setDate(LocalDate date) {
        this.date = date;
    }
    public void setActivities(List<ScheduledActivityResponse> activities) {
        this.activities = activities;
    }   
    
    public String toString() {
        return "DayScheduleResponse{" +
            "dayNumber=" + dayNumber +
            ", date=" + date +
            ", activities=" + activities +
            '}';
    }
}
