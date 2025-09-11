package com.roadreach.roadreach_backend.repository;

import com.roadreach.roadreach_backend.model.City;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CityRepository extends JpaRepository<City, Long> {
}
