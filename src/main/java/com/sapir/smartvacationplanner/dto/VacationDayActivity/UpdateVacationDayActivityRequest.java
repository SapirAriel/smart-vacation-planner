package com.sapir.smartvacationplanner.dto.VacationDayActivity;

import jakarta.validation.constraints.NotNull;

public class UpdateVacationDayActivityRequest {

    @NotNull(message = "Point of interest id is required")
    private Integer pointOfInterestId;

    public Integer getPointOfInterestId() {
        return pointOfInterestId;
    }

    public void setPointOfInterestId(Integer pointOfInterestId) {
        this.pointOfInterestId = pointOfInterestId;
    }

}
