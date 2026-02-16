package com.roadreach.roadreach_backend.repository;

import com.roadreach.roadreach_backend.model.Airport;
import org.springframework.data.jpa.repository.JpaRepository;

import com.roadreach.roadreach_backend.model.State;
import java.util.Optional;

public interface AirportRepository extends JpaRepository<Airport, Long> {
	Optional<Airport> findByCodeAndState(String code, State state);
}
