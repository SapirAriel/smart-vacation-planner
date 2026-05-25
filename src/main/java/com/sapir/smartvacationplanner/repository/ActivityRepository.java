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
import java.util.Optional;
import java.time.LocalTime;
import org.springframework.data.domain.Sort;

public interface ActivityRepository extends JpaRepository<Activity, Integer> {

    List<Activity> findByVacationDay(VacationDay vacationDay);

    Page<Activity> findByVacationDay(VacationDay vacationDay, Pageable pageable);
    
    List<Activity> findByVacationDay(VacationDay vacationDay, Sort sort);

    List<Activity> findByVacationDay_Id(Integer vacationDayId);

    @Query("""
        select a 
        from Activity a
        where a.vacationDay = :vacationDay
          and (:name is null or lower(a.name) like lower(concat('%', :name, '%')))
          and (:activityType is null or a.activityType = :activityType)
          and (:location is null or lower(a.location) like lower(concat('%', :location, '%')))
          and (:durationMinutes is null or a.durationMinutes <= :durationMinutes)
          and (:openingTime is null or a.openingTime = :openingTime)
          and (:closingTime is null or a.closingTime = :closingTime)
          and (:minimumAge is null or a.minimumAge <= :minimumAge)
          and (:notes is null or lower(a.notes) like lower(concat('%', :notes, '%')))
    """)
    Page<Activity> searchActivities(
        @Param("vacationDay") VacationDay vacationDay,
        @Param("name") String name,
        @Param("activityType") ActivityType activityType,
        @Param("location") String location,
        @Param("durationMinutes") Integer durationMinutes,
        @Param("openingTime") LocalTime openingTime,
        @Param("closingTime") LocalTime closingTime,
        @Param("minimumAge") Integer minimumAge,
        @Param("notes") String notes, Pageable pageable);


        Optional<Activity> findByVacationDayAndId(VacationDay vacationDay, Integer id);

}
