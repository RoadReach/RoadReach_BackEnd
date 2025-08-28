package com.roadreach.roadreach_backend.model;

import java.util.List;
import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

public class GeoCountry {
    private final String countryCode;
    private final String locale;
    private final String currencyCode;
    private final String currencySymbol;
    private final String currencyFormat;
    private final String dateFormat;
    private final List<GeoState> states;

    public GeoCountry(String countryCode, String locale, String currencyCode, String currencySymbol,
            String currencyFormat, String dateFormat, List<GeoState> states) {
        this.countryCode = countryCode;
        this.locale = locale;
        this.currencyCode = currencyCode;
        this.currencySymbol = currencySymbol;
        this.currencyFormat = currencyFormat;
        this.dateFormat = dateFormat;
        this.states = states;
    }

    public String getCountryCode() {
        return countryCode;
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

    public static final GeoCountry US = new GeoCountry(
            "US", "en-US", "USD", "$", "$#,##0.00", "MM/dd/yyyy",
            Arrays.asList(
                    new GeoState("Alabama", Arrays.asList("Birmingham", "Montgomery", "Mobile")),
                    new GeoState("Alaska", Arrays.asList("Anchorage", "Juneau", "Fairbanks")),
                    new GeoState("Arizona", Arrays.asList("Phoenix", "Tucson", "Mesa")),
                    new GeoState("Arkansas", Arrays.asList("Little Rock", "Fort Smith", "Fayetteville")),
                    new GeoState("California", Arrays.asList("Los Angeles", "San Francisco", "San Diego")),
                    new GeoState("Colorado", Arrays.asList("Denver", "Colorado Springs", "Aurora")),
                    new GeoState("Connecticut", Arrays.asList("Bridgeport", "Hartford", "New Haven")),
                    new GeoState("Delaware", Arrays.asList("Wilmington", "Dover", "Newark")),
                    new GeoState("Florida", Arrays.asList("Miami", "Orlando", "Tampa")),
                    new GeoState("Georgia", Arrays.asList("Atlanta", "Augusta", "Savannah")),
                    new GeoState("Hawaii", Arrays.asList("Honolulu", "Hilo", "Kailua")),
                    new GeoState("Idaho", Arrays.asList("Boise", "Idaho Falls", "Nampa")),
                    new GeoState("Illinois", Arrays.asList("Chicago", "Springfield", "Naperville")),
                    new GeoState("Indiana", Arrays.asList("Indianapolis", "Fort Wayne", "Evansville")),
                    new GeoState("Iowa", Arrays.asList("Des Moines", "Cedar Rapids", "Davenport")),
                    new GeoState("Kansas", Arrays.asList("Wichita", "Overland Park", "Kansas City")),
                    new GeoState("Kentucky", Arrays.asList("Louisville", "Lexington", "Bowling Green")),
                    new GeoState("Louisiana", Arrays.asList("New Orleans", "Baton Rouge", "Shreveport")),
                    new GeoState("Maine", Arrays.asList("Portland", "Lewiston", "Bangor")),
                    new GeoState("Maryland", Arrays.asList("Baltimore", "Frederick", "Rockville")),
                    new GeoState("Massachusetts", Arrays.asList("Boston", "Worcester", "Springfield")),
                    new GeoState("Michigan", Arrays.asList("Detroit", "Grand Rapids", "Warren")),
                    new GeoState("Minnesota", Arrays.asList("Minneapolis", "Saint Paul", "Rochester")),
                    new GeoState("Mississippi", Arrays.asList("Jackson", "Gulfport", "Southaven")),
                    new GeoState("Missouri", Arrays.asList("Kansas City", "Saint Louis", "Springfield")),
                    new GeoState("Montana", Arrays.asList("Billings", "Missoula", "Great Falls")),
                    new GeoState("Nebraska", Arrays.asList("Omaha", "Lincoln", "Bellevue")),
                    new GeoState("Nevada", Arrays.asList("Las Vegas", "Henderson", "Reno")),
                    new GeoState("New Hampshire", Arrays.asList("Manchester", "Nashua", "Concord")),
                    new GeoState("New Jersey", Arrays.asList("Newark", "Jersey City", "Paterson")),
                    new GeoState("New Mexico", Arrays.asList("Albuquerque", "Las Cruces", "Rio Rancho")),
                    new GeoState("New York", Arrays.asList("New York City", "Buffalo", "Rochester")),
                    new GeoState("North Carolina", Arrays.asList("Charlotte", "Raleigh", "Greensboro")),
                    new GeoState("North Dakota", Arrays.asList("Fargo", "Bismarck", "Grand Forks")),
                    new GeoState("Ohio", Arrays.asList("Columbus", "Cleveland", "Cincinnati")),
                    new GeoState("Oklahoma", Arrays.asList("Oklahoma City", "Tulsa", "Norman")),
                    new GeoState("Oregon", Arrays.asList("Portland", "Eugene", "Salem")),
                    new GeoState("Pennsylvania", Arrays.asList("Philadelphia", "Pittsburgh", "Allentown")),
                    new GeoState("Rhode Island", Arrays.asList("Providence", "Warwick", "Cranston")),
                    new GeoState("South Carolina", Arrays.asList("Charleston", "Columbia", "North Charleston")),
                    new GeoState("South Dakota", Arrays.asList("Sioux Falls", "Rapid City", "Aberdeen")),
                    new GeoState("Tennessee", Arrays.asList("Nashville", "Memphis", "Knoxville")),
                    new GeoState("Texas", Arrays.asList("Houston", "Dallas", "Austin")),
                    new GeoState("Utah", Arrays.asList("Salt Lake City", "West Valley City", "Provo")),
                    new GeoState("Vermont", Arrays.asList("Burlington", "South Burlington", "Rutland")),
                    new GeoState("Virginia", Arrays.asList("Virginia Beach", "Norfolk", "Chesapeake")),
                    new GeoState("Washington", Arrays.asList("Seattle", "Spokane", "Tacoma")),
                    new GeoState("West Virginia", Arrays.asList("Charleston", "Huntington", "Morgantown")),
                    new GeoState("Wisconsin", Arrays.asList("Milwaukee", "Madison", "Green Bay")),
                    new GeoState("Wyoming", Arrays.asList("Cheyenne", "Casper", "Laramie"))));

    public static final GeoCountry CANADA = new GeoCountry(
            "CA", "en-CA", "CAD", "C$", "C$#,##0.00", "yyyy-MM-dd",
            Arrays.asList(
                    new GeoState("Alberta", Arrays.asList("Calgary", "Edmonton", "Red Deer")),
                    new GeoState("British Columbia", Arrays.asList("Vancouver", "Victoria", "Surrey")),
                    new GeoState("Manitoba", Arrays.asList("Winnipeg", "Brandon", "Steinbach")),
                    new GeoState("New Brunswick", Arrays.asList("Moncton", "Saint John", "Fredericton")),
                    new GeoState("Newfoundland and Labrador", Arrays.asList("St. John's", "Corner Brook", "Gander")),
                    new GeoState("Nova Scotia", Arrays.asList("Halifax", "Sydney", "Truro")),
                    new GeoState("Ontario", Arrays.asList("Toronto", "Ottawa", "Hamilton")),
                    new GeoState("Prince Edward Island", Arrays.asList("Charlottetown", "Summerside")),
                    new GeoState("Quebec", Arrays.asList("Montreal", "Quebec City", "Laval")),
                    new GeoState("Saskatchewan", Arrays.asList("Saskatoon", "Regina", "Prince Albert")),
                    new GeoState("Northwest Territories", Arrays.asList("Yellowknife")),
                    new GeoState("Nunavut", Arrays.asList("Iqaluit")),
                    new GeoState("Yukon", Arrays.asList("Whitehorse"))));

    public static List<GeoCountry> getAllCountries() {
        return Arrays.asList(US, CANADA);
    }

    public static GeoCountry getCountryByCode(String code) {
        if ("US".equalsIgnoreCase(code))
            return US;
        if ("CA".equalsIgnoreCase(code))
            return CANADA;
        return null;
    }

    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                registry.addMapping("/api/**")
                        .allowedOrigins("http://localhost:8080")
                        .allowedMethods("GET", "POST", "PUT", "DELETE");
            }
        };
    }
}