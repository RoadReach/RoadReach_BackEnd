package com.roadreach.roadreach_backend.repository;

import com.roadreach.roadreach_backend.model.Country;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CountryRepository extends JpaRepository<Country, Long> {
    Country findByCode(String code);
}
