package com.sapir.smartvacationplanner.dto.vacation;
import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;      
import com.sapir.smartvacationplanner.entity.enums.TravelerType;
import com.sapir.smartvacationplanner.entity.enums.Pace;
import java.math.BigDecimal;

public class UpdateVacationRequest {

    @NotBlank(message = "Name is required")
    private String name;
   
    @NotBlank(message = "Country is required")
    private String country;
   
    @NotBlank(message = "City is required")
    private String city;
   
    @NotNull(message = "Start date is required")
    private LocalDate startDate;
   
    @NotNull(message = "End date is required")
    private LocalDate endDate;
   
    @NotNull(message = "Traveler type is required")
    private TravelerType travelerType;

    @NotNull(message = "Budget is required")
    private BigDecimal budget;

    @NotNull(message = "Pace is required")
    private Pace pace;

    public String getName() {
        return name;
    }
    public String getCountry() {
        return country;
    }
    public String getCity() {
        return city;
    }
    public LocalDate getStartDate() {
        return startDate;
    }  
    public LocalDate getEndDate() {
        return endDate;
    }
    public TravelerType getTravelerType() {
        return travelerType;
    }
    public BigDecimal getBudget() {
        return budget;
    }
    public Pace getPace() {
        return pace;
    }


    public void setName(String name) {
        this.name = name;
    }
    public void setCountry(String country) {
        this.country = country;
    }
    public void setCity(String city) {
        this.city = city;
    }
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }
    public void setTravelerType(TravelerType travelerType) {
        this.travelerType = travelerType;
    }  
    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }
    public void setPace(Pace pace) {
        this.pace = pace;
    }

}//UpdateVacationRequest
