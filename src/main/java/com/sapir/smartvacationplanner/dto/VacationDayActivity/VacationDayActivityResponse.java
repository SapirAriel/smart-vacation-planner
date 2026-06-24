package com.sapir.smartvacationplanner.dto.VacationDayActivity;

public class VacationDayActivityResponse {

    private int id;
    private int vacationDayId;
    private int pointOfInterestId;
    private String pointOfInterestPlaceName;

    public int getId() {
        return id;
    }
    public int getVacationDayId() {
        return vacationDayId;
    }
    public int getPointOfInterestId() {
        return pointOfInterestId;
    }
    public String getPointOfInterestPlaceName() {
        return pointOfInterestPlaceName;
    }

    public void setId(int id) {
        this.id = id;
    }
    public void setVacationDayId(int vacationDayId) {
        this.vacationDayId = vacationDayId;
    }
    public void setPointOfInterestId(int pointOfInterestId) {
        this.pointOfInterestId = pointOfInterestId;
    }
    public void setPointOfInterestPlaceName(String pointOfInterestPlaceName) {
        this.pointOfInterestPlaceName = pointOfInterestPlaceName;
    }

    public String toString() {
        return "VacationDayActivityResponse{" +
            "id=" + id +
            ", vacationDayId=" + vacationDayId +
            ", pointOfInterestId=" + pointOfInterestId +
            ", pointOfInterestPlaceName=" + pointOfInterestPlaceName +
            '}';
    }
}
