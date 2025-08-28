package com.propertyservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.propertyservice.entity.Area;

public interface AreaRepository extends JpaRepository<Area, Long> {
	Optional<Area> findByName(String name);
}
