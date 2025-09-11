package com.roadreach.roadreach_backend.repository;

import com.roadreach.roadreach_backend.model.Airport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AirportRepository extends JpaRepository<Airport, Long> {
}
