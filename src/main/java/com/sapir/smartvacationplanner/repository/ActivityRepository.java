package com.sapir.smartvacationplanner.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.sapir.smartvacationplanner.entity.Activity;
import com.sapir.smartvacationplanner.entity.VacationDay;
import java.util.List;

public interface ActivityRepository extends JpaRepository<Activity, Integer> {

    List<Activity> findByVacationDay(VacationDay vacationDay);

    List<Activity> findByVacationDay_Id(Integer vacationDayId);
}
