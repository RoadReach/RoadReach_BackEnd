package com.roadreach.roadreach_backend.repository;

import com.roadreach.roadreach_backend.model.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface VehicleRepository extends JpaRepository<Vehicle, String> {
}