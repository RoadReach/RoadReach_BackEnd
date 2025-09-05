package com.roadreach.roadreach_backend.model;

import com.roadreach.roadreach_backend.model.GeoCity;

import java.util.List;

public class GeoState {
    private String name;
    private String abbreviation;
    private List<GeoCity> cities;
    private List<GeoCity> airports;

    // No-arg constructor for Jackson
    public GeoState() {
    }

    public GeoState(String name, String abbreviation, List<GeoCity> cities, List<GeoCity> airports) {
        this.name = name;
        this.abbreviation = abbreviation;
        this.cities = cities;
        this.airports = airports;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAbbreviation() {
        return abbreviation;
    }

    public void setAbbreviation(String abbreviation) {
        this.abbreviation = abbreviation;
    }

    public List<GeoCity> getCities() {
        return cities;
    }

    public void setCities(List<GeoCity> cities) {
        this.cities = cities;
    }

    public List<GeoCity> getAirports() {
        return airports;
    }

    public void setAirports(List<GeoCity> airports) {
        this.airports = airports;
    }
}
