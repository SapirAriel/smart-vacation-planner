package com.sapir.smartvacationplanner.entity;
import java.time.LocalDate;
import jakarta.persistence.*;
import com.sapir.smartvacationplanner.entity.enums.DayType;

@Entity
@Table(name = "vacation_days")
public class VacationDay {

@Id
@GeneratedValue(strategy = GenerationType.IDENTITY)
@Column(name = "id")
private int id;

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

public VacationDay() {
}

public VacationDay(Vacation vacation, LocalDate date, int dayNumber, DayType dayType) {
    this.vacation = vacation;
    this.date = date;
    this.dayNumber = dayNumber;
    this.dayType = dayType;
}           

public int getId() {
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

public void setId(int id) {
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

@Override
public String toString() {
    return "VacationDay{" +
        "id=" + id +
        ", vacation=" + vacation +
        ", date=" + date +
        ", dayNumber=" + dayNumber +
        ", dayType='" + dayType + '\'' +
        '}';
}

}