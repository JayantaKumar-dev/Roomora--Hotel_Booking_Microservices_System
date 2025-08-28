package com.propertyservice.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.propertyservice.entity.RoomAvailability;
import com.propertyservice.entity.Rooms;

public interface RoomAvailabilityRepository extends JpaRepository<RoomAvailability, Long> {

	public List<RoomAvailability> findByRoomId(long id);
	
	@Modifying
    @Transactional
    @Query("DELETE FROM RoomAvailability ra WHERE ra.room = :room AND ra.availableDate < :today")
    void deletePastAvailabilities(@Param("room") Rooms room, @Param("today") LocalDate today);
	
    Optional<RoomAvailability> findByRoomAndAvailableDate(Rooms room, LocalDate date);
    
    @Query("""
    	    SELECT ra FROM RoomAvailability ra 
    	    WHERE ra.room.id = :roomId 
    	    AND ra.availableDate BETWEEN :startDate AND :endDate
    	""")
    	List<RoomAvailability> findAvailabilityForDateRange(
    	    @Param("roomId") Long roomId,
    	    @Param("startDate") LocalDate startDate,
    	    @Param("endDate") LocalDate endDate
    	);

	public List<RoomAvailability> findByRoomIdAndAvailableDateIn(Long roomId, List<LocalDate> dates);

	public Optional<RoomAvailability> findByRoomIdAndAvailableDate(Long roomId, LocalDate date);
	
//	Optional<RoomAvailability> findFirstByRoomOrderByAvailableDateDesc(Rooms room);
//	
//	@Query("SELECT MAX(ra.availableDate) FROM RoomAvailability ra")
//    Optional<LocalDate> findMaxAvailableDate();
//    
//    void deleteByAvailableDateBefore(LocalDate date);
//    
//    @Modifying
//    @Query("UPDATE RoomAvailability ra SET ra.availableCount = ra.availableCount - 1 " +
//           "WHERE ra.room.id = :roomId AND ra.availableDate IN :dates")
//    void decrementAvailability(@Param("roomId") Long roomId, 
//                              @Param("dates") List<LocalDate> dates);
}
