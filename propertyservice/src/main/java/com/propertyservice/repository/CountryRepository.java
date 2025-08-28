package com.propertyservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.propertyservice.entity.Country;

public interface CountryRepository extends JpaRepository<Country, Long> {
	Optional<Country> findByName(String name);
}
