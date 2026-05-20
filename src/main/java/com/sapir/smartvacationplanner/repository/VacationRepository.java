package com.sapir.smartvacationplanner.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.sapir.smartvacationplanner.entity.Vacation;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalDate;
import com.sapir.smartvacationplanner.entity.enums.TravelerType;
import com.sapir.smartvacationplanner.entity.enums.Pace;
import java.math.BigDecimal;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;

public interface VacationRepository extends JpaRepository<Vacation, Integer> {

    List<Vacation> findByUserId(Integer userId);   
    

    @Query("""
        select v 
        from Vacation v
          where (:country is null or lower(v.country) like lower(concat('%', :country, '%')))
          and (:city is null or lower(v.city) like lower(concat('%', :city, '%')))
          and (:startDate is null or v.startDate >= :startDate)
          and (:endDate is null or v.endDate <= :endDate)
          and (:travelerType is null or v.travelerType = :travelerType)
          and (:budget is null or v.budget >= :budget)
          and (:pace is null or v.pace = :pace)
    """)
   
    Page<Vacation> searchVacations(
        @Param("country") String country, 
        @Param("city") String city, 
        @Param("startDate") LocalDate startDate, 
        @Param("endDate") LocalDate endDate, 
        @Param("travelerType") TravelerType travelerType, 
        @Param("budget") BigDecimal budget, 
        @Param("pace") Pace pace, 
        Pageable pageable);

    @Query("""
        select v 
        from Vacation v
        where v.user.id = :userId
          and (:country is null or lower(v.country) like lower(concat('%', :country, '%')))
          and (:city is null or lower(v.city) like lower(concat('%', :city, '%')))
          and (:startDate is null or v.startDate >= :startDate)
          and (:endDate is null or v.endDate <= :endDate)
          and (:travelerType is null or v.travelerType = :travelerType)
          and (:budget is null or v.budget >= :budget)
          and (:pace is null or v.pace = :pace)
    """)
   
    Page<Vacation> searchVacationsByUserId(
        @Param("userId") Integer userId,
        @Param("country") String country, 
        @Param("city") String city, 
        @Param("startDate") LocalDate startDate, 
        @Param("endDate") LocalDate endDate, 
        @Param("travelerType") TravelerType travelerType, 
        @Param("budget") BigDecimal budget, 
        @Param("pace") Pace pace, 
        Pageable pageable);

    }