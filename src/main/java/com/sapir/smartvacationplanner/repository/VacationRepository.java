package com.sapir.smartvacationplanner.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.sapir.smartvacationplanner.entity.Vacation;

public interface VacationRepository extends JpaRepository<Vacation, Integer> {
    
    }