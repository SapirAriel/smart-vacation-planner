package com.sapir.smartvacationplanner.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.sapir.smartvacationplanner.entity.VacationDay;
import com.sapir.smartvacationplanner.entity.Vacation;

public interface VacationDayRepository extends JpaRepository<VacationDay, Integer> {

    List<VacationDay> findByVacation(Vacation vacation);

    List<VacationDay> findByVacation_Id(Integer vacationId);
    
    }