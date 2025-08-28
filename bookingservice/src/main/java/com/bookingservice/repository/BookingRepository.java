package com.bookingservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bookingservice.entity.Bookings;

public interface BookingRepository extends JpaRepository<Bookings, Long> {

	Optional<Bookings> findByTransactionId(String sessionId);

}