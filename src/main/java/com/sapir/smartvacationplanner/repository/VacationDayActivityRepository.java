package com.sapir.smartvacationplanner.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import com.sapir.smartvacationplanner.entity.VacationDayActivity;
import com.sapir.smartvacationplanner.entity.VacationDay;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import java.util.Optional;

public interface VacationDayActivityRepository extends JpaRepository<VacationDayActivity, Integer> {

    List<VacationDayActivity> findByVacationDay(VacationDay vacationDay);

    Page<VacationDayActivity> findByVacationDay(VacationDay vacationDay, Pageable pageable);
    
    List<VacationDayActivity> findByVacationDay(VacationDay vacationDay, Sort sort);

    List<VacationDayActivity> findByVacationDay_Id(Integer vacationDayId);

    Optional<VacationDayActivity> findByVacationDayAndId(VacationDay vacationDay, Integer vacationDayActivityId);

    boolean existsByVacationDay_IdAndPointOfInterest_Id(Integer vacationDayId, Integer pointOfInterestId);

    boolean existsByVacationDay_IdAndPointOfInterest_IdAndIdNot(
            Integer vacationDayId, Integer pointOfInterestId, Integer activityId);

    @Modifying(flushAutomatically = true, clearAutomatically = true)
    @Query("""
        UPDATE VacationDayActivity a
        SET a.plannedStartTime = null,
            a.plannedEndTime = null,
            a.travelMinutesFromPrevious = 0,
            a.distanceKmFromPrevious = 0.0
        WHERE a.vacationDay.id = :vacationDayId
        """)
    int clearPlanningDataByVacationDayId(@Param("vacationDayId") Integer vacationDayId);

}
