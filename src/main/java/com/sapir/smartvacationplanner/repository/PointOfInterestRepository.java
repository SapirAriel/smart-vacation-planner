package com.sapir.smartvacationplanner.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import com.sapir.smartvacationplanner.entity.PointOfInterest;
import com.sapir.smartvacationplanner.entity.enums.PointOfInterestCategory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.time.LocalTime;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
public interface PointOfInterestRepository extends JpaRepository<PointOfInterest, Integer> {


    @Query("""
        select p 
        from PointOfInterest p
          where (:name is null or lower(p.name) like lower(concat('%', :name, '%')))
          and (:pointOfInterestCategory is null or p.pointOfInterestCategory = :pointOfInterestCategory)
          and (:placeName is null or lower(p.place.placeName) like lower(concat('%', :placeName, '%')))
          and (:durationMinutes is null or p.durationMinutes = :durationMinutes)
          and (:openingTime is null or p.openingTime = :openingTime)
          and (:closingTime is null or p.closingTime = :closingTime)
          and (:minimumAge is null or p.minimumAge = :minimumAge)
          and (:notes is null or lower(p.notes) like lower(concat('%', :notes, '%')))
    """)
    Page<PointOfInterest> searchPointOfInterests(
        @Param("name") String name, 
        @Param("pointOfInterestCategory") PointOfInterestCategory pointOfInterestCategory, 
        @Param("placeName") String placeName, 
        @Param("durationMinutes") Integer durationMinutes, 
        @Param("openingTime") LocalTime openingTime, 
        @Param("closingTime") LocalTime closingTime, 
        @Param("minimumAge") Integer minimumAge, 
        @Param("notes") String notes, 
        Pageable pageable);
}