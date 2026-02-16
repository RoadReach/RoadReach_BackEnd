
package com.roadreach.roadreach_backend.controller;

import org.springframework.web.bind.annotation.RequestParam;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import com.roadreach.roadreach_backend.model.GeoCity;
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
            if (state.getName().equalsIgnoreCase(stateName) || state.getAbbreviation().equalsIgnoreCase(stateName)) {
                List<String> cityNames = state.getCities().stream()
                        .map(GeoCity::getName)
                        .collect(Collectors.toList());
                return ResponseEntity.ok(cityNames);
            }
        }
        return ResponseEntity.notFound().build();
    }

    // Location suggestion DTO
    public static class LocationSuggestion {
        private String id;
        private String name;
        private String state;
        private String stateAbbr;
        private String country;
        private String countryCode;
        private String type; // "city" or "airport"
        private String icon; // "building" or "plane"

        public LocationSuggestion(String id, String name, String state, String stateAbbr, String country,
                String countryCode, String type, String icon) {
            this.id = id;
            this.name = name;
            this.state = state;
            this.stateAbbr = stateAbbr;
            this.country = country;
            this.countryCode = countryCode;
            this.type = type;
            this.icon = icon;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public String getState() {
            return state;
        }

        public String getStateAbbr() {
            return stateAbbr;
        }

        public String getCountry() {
            return country;
        }

        public String getCountryCode() {
            return countryCode;
        }

        public String getType() {
            return type;
        }

        public String getIcon() {
            return icon;
        }
    }

    // Auto-suggest endpoint for US cities and airports
    @GetMapping("/countries/us/locations/suggest")
    public List<LocationSuggestion> suggestLocationsUS(@RequestParam("query") String query) {
        return suggestLocationsByCountry("US", "United States", query);
    }

    // Auto-suggest endpoint for CA cities and airports
    @GetMapping("/countries/ca/locations/suggest")
    public List<LocationSuggestion> suggestLocationsCA(@RequestParam("query") String query) {
        return suggestLocationsByCountry("CA", "Canada", query);
    }

    // Shared logic for auto-suggest by country
    private List<LocationSuggestion> suggestLocationsByCountry(String countryCode, String countryName, String query) {
        System.out.println("Suggesting locations for country: " + countryCode + ", query: " + query);
        List<LocationSuggestion> suggestions = new ArrayList<>();
        GeoCountry country = GeoCountry.getCountryByCode(countryCode);
        if (country == null)
            return suggestions;
        String q = query.toLowerCase(Locale.ROOT);
        for (GeoState state : country.getStates()) {
            String stateName = state.getName();
            String stateAbbr = state.getAbbreviation();
            // Cities
            for (GeoCity city : state.getCities()) {
                if (city.getName().toLowerCase(Locale.ROOT).contains(q)) {
                    String id = city.getName().replaceAll("\\s+", "_") + "_" + stateAbbr + "_city";
                    suggestions.add(new LocationSuggestion(
                            id,
                            city.getName(), stateName, stateAbbr, countryName, countryCode, "city", "building"));
                }
            }
            // Airports (if airports are present in GeoState)
            if (state.getAirports() != null) {
                for (GeoCity airport : state.getAirports()) {
                    if (airport.getName().toLowerCase(Locale.ROOT).contains(q)) {
                        String code = airport.getCode() != null ? airport.getCode()
                                : airport.getName().replaceAll("\\s+", "_");
                        String id = code + "_" + stateAbbr + "_airport";
                        suggestions.add(new LocationSuggestion(
                                id,
                                airport.getName(), stateName, stateAbbr, countryName, countryCode, "airport", "plane"));
                    }
                }
            }
        }
        // Limit to top 10 results
        return suggestions.stream().limit(10).collect(Collectors.toList());
    }
}