package com.roadreach.roadreach_backend.repository;

import com.roadreach.roadreach_backend.model.City;
import org.springframework.data.jpa.repository.JpaRepository;

import com.roadreach.roadreach_backend.model.State;
import java.util.Optional;

public interface CityRepository extends JpaRepository<City, Long> {
	Optional<City> findByNameAndTypeAndState(String name, String type, State state);
}
