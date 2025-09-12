package com.roadreach.roadreach_backend.service;


import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.roadreach.roadreach_backend.model.*;
import com.roadreach.roadreach_backend.repository.CountryRepository;
import com.roadreach.roadreach_backend.repository.StateRepository;
import com.roadreach.roadreach_backend.repository.CityRepository;
import com.roadreach.roadreach_backend.repository.AirportRepository;
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
    private final StateRepository stateRepository;
    private final CityRepository cityRepository;
    private final AirportRepository airportRepository;

    @Autowired
    private EntityManager entityManager;

    public CountryDataLoader(CountryRepository countryRepository, ObjectMapper objectMapper,
                            StateRepository stateRepository, CityRepository cityRepository, AirportRepository airportRepository) {
        this.countryRepository = countryRepository;
        this.objectMapper = objectMapper;
        this.stateRepository = stateRepository;
        this.cityRepository = cityRepository;
        this.airportRepository = airportRepository;
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
            InputStream is = new ClassPathResource(fileName).getInputStream();
            JsonNode root = objectMapper.readTree(is);
            Country country = countryRepository.findByCode(code);
            if (country == null) {
                country = new Country();
                country.setCode(root.path("code").asText());
            }
            country.setName(root.path("name").asText());
            country.setLocale(root.path("locale").asText());
            country.setCurrencyCode(root.path("currencyCode").asText());
            country.setCurrencySymbol(root.path("currencySymbol").asText());
            country.setCurrencyFormat(root.path("currencyFormat").asText());
            country.setDateFormat(root.path("dateFormat").asText());
            country = countryRepository.save(country);
            entityManager.flush();

            for (JsonNode stateNode : root.path("states")) {
                String abbr = stateNode.path("abbreviation").asText();
                State state = stateRepository.findByAbbreviationAndCountry(abbr, country).orElse(null);
                boolean stateExists = (state != null);
                if (!stateExists) {
                    state = new State();
                    state.setAbbreviation(abbr);
                    state.setCountry(country);
                }
                state.setName(stateNode.path("name").asText());

                // Cities
                List<City> cityList = new ArrayList<>();
                for (JsonNode cityNode : stateNode.path("cities")) {
                    String cityName = cityNode.path("name").asText();
                    String cityType = cityNode.path("type").asText();
                    City city = cityRepository.findByNameAndTypeAndState(cityName, cityType, state).orElse(null);
                    if (city == null) {
                        city = new City();
                        city.setName(cityName);
                        city.setType(cityType);
                        city.setState(state);
                    }
                    city.setName(cityName);
                    city.setType(cityType);
                    city.setState(state);
                    cityList.add(city);
                }
                if (stateExists && state.getCities() != null) {
                    state.getCities().clear();
                    state.getCities().addAll(cityList);
                } else {
                    state.setCities(cityList);
                }

                // Airports
                List<Airport> airportList = new ArrayList<>();
                for (JsonNode airportNode : stateNode.path("airports")) {
                    String airportCode = airportNode.path("code").asText();
                    Airport airport = airportRepository.findByCodeAndState(airportCode, state).orElse(null);
                    if (airport == null) {
                        airport = new Airport();
                        airport.setCode(airportCode);
                        airport.setState(state);
                    }
                    airport.setName(airportNode.path("name").asText());
                    airport.setType(airportNode.path("type").asText());
                    airport.setCode(airportCode);
                    airport.setState(state);
                    airportList.add(airport);
                }
                if (stateExists && state.getAirports() != null) {
                    state.getAirports().clear();
                    state.getAirports().addAll(airportList);
                } else {
                    state.setAirports(airportList);
                }
                state.setCountry(country);
                state = stateRepository.save(state);
            }
            logger.info("Upserted country {} and all child data.", country.getCode());
        } catch (Exception e) {
            logger.error("Error loading country from {}: {}", fileName, e.getMessage(), e);
        }
    }
}
