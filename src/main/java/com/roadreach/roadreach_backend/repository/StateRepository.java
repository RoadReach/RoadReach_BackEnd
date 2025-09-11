package com.roadreach.roadreach_backend.repository;

import com.roadreach.roadreach_backend.model.State;
import org.springframework.data.jpa.repository.JpaRepository;

import com.roadreach.roadreach_backend.model.Country;
import java.util.Optional;

public interface StateRepository extends JpaRepository<State, Long> {
	Optional<State> findByAbbreviationAndCountry(String abbreviation, Country country);
}
