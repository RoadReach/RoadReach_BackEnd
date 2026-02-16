package com.roadreach.roadreach_backend.model;

import java.util.ArrayList;
import java.util.List;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.InputStream;

public class GeoCountry {
    private String code;
    private String name;
    private String locale;
    private String currencyCode;
    private String currencySymbol;
    private String currencyFormat;
    private String dateFormat;
    private List<GeoState> states;

    public GeoCountry(String code, String name, List<GeoState> states) {
        this.code = code;
        this.name = name;
        this.states = states;
    }

    public GeoCountry() {
    }

    public String getCode() {
        return code;
    }

    public String getName() {
        return name;
    }

    public String getLocale() {
        return locale;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public String getCurrencySymbol() {
        return currencySymbol;
    }

    public String getCurrencyFormat() {
        return currencyFormat;
    }

    public String getDateFormat() {
        return dateFormat;
    }

    public List<GeoState> getStates() {
        return states;
    }

    // Load countries from JSON file
    public static GeoCountry fromJson(String resourcePath) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = GeoCountry.class.getClassLoader().getResourceAsStream(resourcePath);
            return mapper.readValue(is, GeoCountry.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to load country data from JSON: " + resourcePath, e);
        }
    }

    // Example: Load US and Canada
    public static GeoCountry getUS() {
        return fromJson("united states.json");
    }

    public static GeoCountry getCanada() {
        return fromJson("canada.json");
    }

    public static GeoCountry getCountryByCode(String code) {
        if ("US".equalsIgnoreCase(code)) {
            return getUS();
        }
        if ("CA".equalsIgnoreCase(code)) {
            return getCanada();
        }
        // Add more countries if needed
        return null;
    }

    public static List<GeoCountry> getAllCountries() {
        List<GeoCountry> countries = new ArrayList<>();
        countries.add(getUS());
        countries.add(getCanada());
        // Add more countries if you have more JSON files
        return countries;
    }
}