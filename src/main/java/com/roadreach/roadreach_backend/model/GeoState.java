package com.roadreach.roadreach_backend.model;

import java.util.List;

public class GeoState {
    private final String name;
    private final List<String> cities;

    public GeoState(String name, List<String> cities) {
        this.name = name;
        this.cities = cities;
    }

    public String getName() {
        return name;
    }

    public List<String> getCities() {
        return cities;
    }
}
