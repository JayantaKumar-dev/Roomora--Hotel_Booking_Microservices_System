package com.bookingservice.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.bookingservice.entity.BookingDate;

public interface BookingDateRepository extends JpaRepository<BookingDate, Long> {

	@Query("SELECT bd.date FROM BookingDate bd WHERE bd.booking.id = :bookingId")
    List<LocalDate> findDatesByBookingId(@Param("bookingId") Long bookingId);


}