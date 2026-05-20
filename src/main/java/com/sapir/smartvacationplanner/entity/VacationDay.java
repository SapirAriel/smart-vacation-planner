package com.sapir.smartvacationplanner.entity;
import java.time.LocalDate;
import jakarta.persistence.*;
import com.sapir.smartvacationplanner.entity.enums.DayType;
import java.util.List;
import java.util.ArrayList;

/**
 * VacationDay entity represents a day of a vacation.
 * It is a nested resource of Vacation.
 * It is used to store the day details such as date, day number, and day type.
 */

@Entity
@Table(name = "vacation_days")
public class VacationDay {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "id")
private Integer id;

//foreign key to the Vacation entity
@ManyToOne
@JoinColumn(name = "vacation_id", nullable = false)
private Vacation vacation;

@Column(name = "date", nullable = false)
private LocalDate date;

@Column(name = "day_number", nullable = false)
private int dayNumber;

@Column(name = "day_type", nullable = false)
@Enumerated(EnumType.STRING)
private DayType dayType;

@OneToMany(mappedBy = "vacationDay")
private List<Activity> activities = new ArrayList<>();

public VacationDay() {
}

public VacationDay(Vacation vacation, LocalDate date, int dayNumber, DayType dayType) {
    
    this.vacation = vacation;
    this.date = date;
    this.dayNumber = dayNumber;
    this.dayType = dayType;
}     

public VacationDay(Vacation vacation, LocalDate date, int dayNumber, DayType dayType, List<Activity> activities) {
    this.vacation = vacation;
    this.date = date;
    this.dayNumber = dayNumber;
    this.dayType = dayType;
    this.activities = activities;
}

public Integer getId() {
    return id;
}
public Vacation getVacation() {
    return vacation;
}

public LocalDate getDate() {
    return date;
}
public int getDayNumber() {
    return dayNumber;
}
public DayType getDayType() {
    return dayType;
}
public List<Activity> getActivities() {
    return activities;
}

public void setId(Integer id) {
    this.id = id;
}
public void setVacation(Vacation vacation) {
    this.vacation = vacation;
}

public void setDate(LocalDate date) {
    this.date = date;
}
public void setDayNumber(int dayNumber) {
    this.dayNumber = dayNumber;
}
public void setDayType(DayType dayType) {
    this.dayType = dayType;
}
public void setActivities(List<Activity> activities) {
    this.activities = activities;
}

@Override
public String toString() {
    return "VacationDay{" +
        "id=" + id +
        ", vacation=" + vacation +
        ", date=" + date +
        ", dayNumber=" + dayNumber +
        ", dayType='" + dayType + '\'' +
        ", activities=" + activities +
        '}';
}

}