package com.propertyservice.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.propertyservice.entity.Property;


public interface PropertyRepository extends JpaRepository<Property, Long> {

	@Query("SELECT COUNT(p) > 0 FROM Property p WHERE LOWER(TRIM(p.name)) = LOWER(TRIM(:name))")
	boolean existsByName(@Param("name") String name);

	
	@Query("""
		    SELECT DISTINCT p 
		    FROM Property p
		    JOIN p.rooms r
		    JOIN RoomAvailability ra ON ra.room = r
		    WHERE (
		        LOWER(p.city.name) LIKE LOWER(CONCAT('%', :name, '%')) OR
		        LOWER(p.area.name) LIKE LOWER(CONCAT('%', :name, '%')) OR
		        LOWER(p.state.name) LIKE LOWER(CONCAT('%', :name, '%')) OR
		        LOWER(p.country.name) LIKE LOWER(CONCAT('%', :name, '%'))
		    )
		    AND ra.availableDate = :date
		""")
	List<Property> searchProperty(@Param("name") String name, @Param("date") LocalDate date);


	 @Query("""
		        SELECT DISTINCT p 
		        FROM Property p
		        JOIN FETCH p.rooms r
		        JOIN FETCH p.city
		        JOIN FETCH p.area
		        JOIN FETCH p.state
		        JOIN FETCH p.country
		        WHERE (
		            LOWER(p.city.name) LIKE LOWER(CONCAT('%', :name, '%')) OR
		            LOWER(p.area.name) LIKE LOWER(CONCAT('%', :name, '%')) OR
		            LOWER(p.state.name) LIKE LOWER(CONCAT('%', :name, '%')) OR
		            LOWER(p.country.name) LIKE LOWER(CONCAT('%', :name, '%'))
		        )
		        AND NOT EXISTS (
		            SELECT ra FROM RoomAvailability ra
		            WHERE ra.room = r
		            AND ra.availableDate BETWEEN :checkIn AND :checkOut
		            AND ra.availableCount <= 0
		        )
		        AND EXISTS (
		            SELECT ra FROM RoomAvailability ra
		            WHERE ra.room = r
		            AND ra.availableDate = :checkIn
		        )
		        """)
	List<Property> searchPropertyWithAvailability(@Param("name") String name, @Param("checkIn") LocalDate checkIn, @Param("checkOut") LocalDate checkOut);



}
