package com.roadreach.roadreach_backend.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.roadreach.roadreach_backend.model.*;
import com.roadreach.roadreach_backend.repository.CountryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.persistence.EntityManager;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Component
public class CountryDataLoader implements CommandLineRunner {
    private static final Logger logger = LoggerFactory.getLogger(CountryDataLoader.class);
    private final CountryRepository countryRepository;
    private final ObjectMapper objectMapper;

    @Autowired
    private EntityManager entityManager;

    public CountryDataLoader(CountryRepository countryRepository, ObjectMapper objectMapper) {
        this.countryRepository = countryRepository;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional
    public void run(String... args) {
        try {
            logger.info("Starting country data load...");
            loadCountryFromJson("canada.json");
            loadCountryFromJson("united states.json");
            logger.info("Country data load complete.");
        } catch (Exception e) {
            logger.error("Error loading country data: ", e);
        }
    }

    private void loadCountryFromJson(String fileName) {
        try {
            String code = fileName.toLowerCase().contains("canada") ? "CA" : "US";
            Country country = countryRepository.findByCode(code);
            boolean countryExists = (country != null);
            if (!countryExists) {
                logger.info("Country {} does not exist, creating new entry.", code);
                country = new Country();
                InputStream is = new ClassPathResource(fileName).getInputStream();
                JsonNode root = objectMapper.readTree(is);
                country.setCode(root.path("code").asText());
                country.setName(root.path("name").asText());
                country.setLocale(root.path("locale").asText());
                country.setCurrencyCode(root.path("currencyCode").asText());
                country.setCurrencySymbol(root.path("currencySymbol").asText());
                country.setCurrencyFormat(root.path("currencyFormat").asText());
                country.setDateFormat(root.path("dateFormat").asText());
                countryRepository.save(country);
                entityManager.flush();
            } else {
                logger.info("Country {} already exists in DB, updating child data.", code);
            }

            // Always process states, cities, airports
            InputStream is = new ClassPathResource(fileName).getInputStream();
            JsonNode root = objectMapper.readTree(is);
            List<State> newStates = new ArrayList<>();
            for (JsonNode stateNode : root.path("states")) {
                State state = new State();
                state.setName(stateNode.path("name").asText());
                state.setAbbreviation(stateNode.path("abbreviation").asText());
                state.setCountry(country);

                List<City> cities = new ArrayList<>();
                for (JsonNode cityNode : stateNode.path("cities")) {
                    City city = new City();
                    city.setName(cityNode.path("name").asText());
                    city.setType(cityNode.path("type").asText());
                    city.setState(state);
                    cities.add(city);
                }
                state.setCities(cities);
                for (City city : cities) {
                    city.setState(state);
                }

                List<Airport> airports = new ArrayList<>();
                for (JsonNode airportNode : stateNode.path("airports")) {
                    Airport airport = new Airport();
                    airport.setName(airportNode.path("name").asText());
                    airport.setType(airportNode.path("type").asText());
                    airport.setCode(airportNode.path("code").asText());
                    airport.setState(state);
                    airports.add(airport);
                }
                state.setAirports(airports);
                for (Airport airport : airports) {
                    airport.setState(state);
                }

                newStates.add(state);
            }
            if (countryExists) {
                // Clear and add to the existing collection to avoid orphanRemoval error
                if (country.getStates() != null) {
                    country.getStates().clear();
                    country.getStates().addAll(newStates);
                } else {
                    country.setStates(newStates);
                }
            } else {
                country.setStates(newStates);
            }
            for (State state : newStates) {
                state.setCountry(country);
            }
            countryRepository.save(country);
            entityManager.flush();
            logger.info("Saved/updated country {} with {} states.", country.getCode(), newStates.size());
        } catch (Exception e) {
            logger.error("Error loading country from {}: {}", fileName, e.getMessage(), e);
        }
    }
}
