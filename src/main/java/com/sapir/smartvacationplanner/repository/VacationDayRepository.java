package com.sapir.smartvacationplanner.repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import com.sapir.smartvacationplanner.entity.VacationDay;
import com.sapir.smartvacationplanner.entity.Vacation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import com.sapir.smartvacationplanner.entity.enums.DayType;
import java.time.LocalDate;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface VacationDayRepository extends JpaRepository<VacationDay, Integer> {

    List<VacationDay> findByVacation(Vacation vacation);

    Page<VacationDay> findByVacation(Vacation vacation, Pageable pageable);

    List<VacationDay> findByVacation_Id(Integer vacationId);

    Page<VacationDay> findByVacation_IdAndDayType(Integer vacationId, DayType dayType, Pageable pageable);

    @Query("""
        select vd 
        from VacationDay vd
        where vd.vacation.id = :vacationId
          and (:dayType is null or vd.dayType = :dayType)
          and (:date is null or vd.date = :date)
          and (:dayNumber is null or vd.dayNumber = :dayNumber)
    """)
   
    Page<VacationDay> searchVacationDays(
        @Param("vacationId") Integer vacationId,
        @Param("dayType") DayType dayType, 
        @Param("date") LocalDate date, 
        @Param("dayNumber") Integer dayNumber, 
        Pageable pageable);
    
    }