package com.roadreach.roadreach_backend.controller;

import com.roadreach.roadreach_backend.model.GeoCountry;
import com.roadreach.roadreach_backend.model.GeoState;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class CountryController {
    @GetMapping("/countries/{countryCode}/states")
    public ResponseEntity<List<String>> getStatesByCountry(@PathVariable String countryCode) {
        GeoCountry country = GeoCountry.getCountryByCode(countryCode);
        if (country == null) {
            return ResponseEntity.notFound().build();
        }
        List<String> stateNames = country.getStates().stream()
                .map(GeoState::getName)
                .collect(Collectors.toList());
        return ResponseEntity.ok(stateNames);
    }

    @GetMapping("/countries/{countryCode}/states/{stateName}/cities")
    public ResponseEntity<List<String>> getCitiesByState(@PathVariable String countryCode,
            @PathVariable String stateName) {
        GeoCountry country = GeoCountry.getCountryByCode(countryCode);
        if (country == null) {
            return ResponseEntity.notFound().build();
        }
        for (GeoState state : country.getStates()) {
            if (state.getName().equalsIgnoreCase(stateName)) {
                return ResponseEntity.ok(state.getCities());
            }
        }
        return ResponseEntity.notFound().build();
    }
}
