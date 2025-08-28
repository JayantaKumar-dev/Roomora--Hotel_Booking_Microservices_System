package com.propertyservice.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.propertyservice.entity.State;

public interface StateRepository extends JpaRepository<State, Long> {
	Optional<State> findByName(String name);
}
