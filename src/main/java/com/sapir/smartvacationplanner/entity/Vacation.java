package com.sapir.smartvacationplanner.entity;
import java.time.LocalDate;
import jakarta.persistence.*;
import com.sapir.smartvacationplanner.entity.enums.TravelerType;
import com.sapir.smartvacationplanner.entity.enums.Pace;
import java.math.BigDecimal;
import java.util.List;
import java.util.ArrayList;

/**
 * Vacation entity represents a vacation.
 * It is used to store the vacation details such as name, country, city, start date, end date, traveler type, budget, pace, and vacation days.
 */ 

@Entity
@Table(name = "vacations")
public class Vacation {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "id")
private int id;

@Column(name = "name", nullable = false, unique = true)
private String name; 

@Column(name = "country", nullable = false)   
private String country;

@Column(name = "city", nullable = false)
private String city;

@Column(name = "start_date")
private LocalDate startDate;

@Column(name = "end_date")
private LocalDate endDate;

@Column(name = "traveler_type")
@Enumerated(EnumType.STRING)
private TravelerType travelerType;

@Column(name = "budget")
private BigDecimal budget;

@Column(name = "pace")
@Enumerated(EnumType.STRING)
private Pace pace;

@OneToMany(mappedBy = "vacation")
private List<VacationDay> vacationDays = new ArrayList<>();

public Vacation() { 
}

public Vacation( String name, String country, String city, LocalDate startDate,
    LocalDate endDate, TravelerType travelerType, BigDecimal budget, Pace pace) {

   this.name = name;
   this.country = country;
   this.city = city;
   this.startDate = startDate;
   this.endDate = endDate;
   this.travelerType = travelerType;
   this.budget = budget;
   this.pace = pace;
} 

public Vacation( String name, String country, String city, LocalDate startDate,
     LocalDate endDate, TravelerType travelerType, BigDecimal budget, Pace pace, List<VacationDay> vacationDays) {

    this.name = name;
    this.country = country;
    this.city = city;
    this.startDate = startDate;
    this.endDate = endDate;
    this.travelerType = travelerType;
    this.budget = budget;
    this.pace = pace;
    this.vacationDays = vacationDays;
}   

public int getId() {
    return id;
}

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
public List<VacationDay> getVacationDays() {
    return vacationDays;
}

public void setId(int id) {
    this.id = id;
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
public void setVacationDays(List<VacationDay> vacationDays) {
    this.vacationDays = vacationDays;
}

@Override
public String toString() {
    return "Vacation{" +
        "id=" + id +
        ", name='" + name + '\'' +
        ", country='" + country + '\'' +
        ", city='" + city + '\'' +
        ", startDate=" + startDate +
        ", endDate=" + endDate +
        ", travelerType='" + travelerType + '\'' +
        ", budget=" + budget +
        ", pace=" + pace +
        ", vacationDays=" + vacationDays +
        '}';
}

}