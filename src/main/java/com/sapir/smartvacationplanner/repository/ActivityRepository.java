package com.sapir.smartvacationplanner.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.sapir.smartvacationplanner.entity.Activity;
import com.sapir.smartvacationplanner.entity.VacationDay;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.sapir.smartvacationplanner.entity.enums.ActivityType;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ActivityRepository extends JpaRepository<Activity, Integer> {

    List<Activity> findByVacationDay(VacationDay vacationDay);

    Page<Activity> findByVacationDay(VacationDay vacationDay, Pageable pageable);

    List<Activity> findByVacationDay_Id(Integer vacationDayId);

    Page<Activity> findByVacationDay_IdAndVacationDay_Vacation_IdAndActivityType(Integer vacationDayId, Integer vacationId, ActivityType activityType, Pageable pageable);

    @Query("""
        select a 
        from Activity a
        where a.vacationDay.id = :vacationDayId
          and a.vacationDay.vacation.id = :vacationId
          and (:name is null or lower(a.name) like lower(concat('%', :name, '%')))
          and (:activityType is null or a.activityType = :activityType)
          and (:location is null or lower(a.location) like lower(concat('%', :location, '%')))
          and (:durationMinutes is null or a.durationMinutes <= :durationMinutes)
          and (:openingHours is null or lower(a.openingHours) like lower(concat('%', :openingHours, '%')))
          and (:minimumAge is null or a.minimumAge <= :minimumAge)
          and (:notes is null or lower(a.notes) like lower(concat('%', :notes, '%')))
    """)
    Page<Activity> searchActivities(
        @Param("vacationDayId") Integer vacationDayId,
        @Param("vacationId") Integer vacationId,
        @Param("name") String name,
        @Param("activityType") ActivityType activityType,
        @Param("location") String location,
        @Param("durationMinutes") Integer durationMinutes,
        @Param("openingHours") String openingHours,
        @Param("minimumAge") Integer minimumAge,
        @Param("notes") String notes, Pageable pageable);

}
