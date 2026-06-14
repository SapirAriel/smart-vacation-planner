package com.sapir.smartvacationplanner.repository;
import org.springframework.data.jpa.repository.JpaRepository;
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

}
