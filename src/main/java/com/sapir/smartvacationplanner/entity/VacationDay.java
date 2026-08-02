package com.sapir.smartvacationplanner.entity;
import java.time.LocalDate;
import jakarta.persistence.*;
import com.sapir.smartvacationplanner.entity.enums.DayType;
import java.util.List;
import java.util.ArrayList;
import com.sapir.smartvacationplanner.common.place.Place;

/**
 * VacationDay entity represents a day of a vacation.
 * It is a nested resource of Vacation.
 * It is used to store the day details such as date, day number, and day type.
 */

@Entity
@Table(name = "vacation_days",
    uniqueConstraints = { @UniqueConstraint(columnNames = {"vacation_id", "date"}, name = "unique_vacation_id_date"),
                          @UniqueConstraint(columnNames = {"vacation_id", "day_number"}, name = "unique_vacation_id_day_number") })
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

@Embedded
@AttributeOverrides({
        @AttributeOverride(name = "placeName", column = @Column(name = "hotel_place_name")),
        @AttributeOverride(name = "placeId", column = @Column(name = "hotel_place_id")),
        @AttributeOverride(name = "formattedAddress", column = @Column(name = "hotel_formatted_address")),
        @AttributeOverride(name = "city", column = @Column(name = "hotel_city")),
        @AttributeOverride(name = "country", column = @Column(name = "hotel_country")),
        @AttributeOverride(name = "latitude", column = @Column(name = "hotel_latitude")),
        @AttributeOverride(name = "longitude", column = @Column(name = "hotel_longitude"))      
})
private Place hotelPlace;

@OneToMany(mappedBy = "vacationDay", cascade = CascadeType.ALL, orphanRemoval = true)
private List<VacationDayActivity> vacationDayActivities = new ArrayList<>();

public VacationDay() {
}

public VacationDay(Vacation vacation, LocalDate date, int dayNumber, DayType dayType, Place hotelPlace) {
    
    this.vacation = vacation;
    this.date = date;
    this.dayNumber = dayNumber;
    this.dayType = dayType;
    this.hotelPlace = hotelPlace;
}     

public VacationDay(Vacation vacation, LocalDate date, int dayNumber, DayType dayType, Place hotelPlace, List<VacationDayActivity> vacationDayActivities) {
    this.vacation = vacation;
    this.date = date;
    this.dayNumber = dayNumber;
    this.dayType = dayType;
    this.hotelPlace = hotelPlace;
    this.vacationDayActivities = vacationDayActivities;
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
public Place getHotelPlace() {
    return hotelPlace;
}
public List<VacationDayActivity> getVacationDayActivities() {
    return vacationDayActivities;
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
public void setHotelPlace(Place hotelPlace) {
    this.hotelPlace = hotelPlace;
}
public void setVacationDayActivities(List<VacationDayActivity> vacationDayActivities) {
    this.vacationDayActivities = vacationDayActivities;
}

@Override
public String toString() {
    return "VacationDay{" +
        "id=" + id +
        ", vacation=" + vacation +
        ", date=" + date +
        ", dayNumber=" + dayNumber +
        ", dayType='" + dayType + '\'' +
        ", hotelPlace=" + hotelPlace +
        ", vacationDayActivities=" + vacationDayActivities +
        '}';
}

public void addActivity(VacationDayActivity activity) {
    this.vacationDayActivities.add(activity);
    activity.setVacationDay(this);
}

public void removeActivity(VacationDayActivity activity) {
    this.vacationDayActivities.remove(activity);
}

}
