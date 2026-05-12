package com.sapir.smartvacationplanner.dto.vacation;
import java.time.LocalDate;
import com.sapir.smartvacationplanner.entity.enums.TravelerType;
import com.sapir.smartvacationplanner.entity.enums.Pace;
import java.math.BigDecimal;

public class PatchVacationRequest {
   
    private String name;
   
    private String country;
   
    private String city;
   
    private LocalDate startDate;
   
    private LocalDate endDate;
   
    private TravelerType travelerType;

    private BigDecimal budget;

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

} //PatchVacationRequest    