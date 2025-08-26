package com.roadreach.roadreach_backend.model;

public class GeoCountry {
    private final String countryCode;
    private final String locale;
    private final String currencyCode;
    private final String currencySymbol;
    private final String currencyFormat;
    private final String dateFormat;

    public GeoCountry(String countryCode, String locale, String currencyCode, String currencySymbol, String currencyFormat, String dateFormat) {
        this.countryCode = countryCode;
        this.locale = locale;
        this.currencyCode = currencyCode;
        this.currencySymbol = currencySymbol;
        this.currencyFormat = currencyFormat;
        this.dateFormat = dateFormat;
    }

    public String getCountryCode() { return countryCode; }
    public String getLocale() { return locale; }
    public String getCurrencyCode() { return currencyCode; }
    public String getCurrencySymbol() { return currencySymbol; }
    public String getCurrencyFormat() { return currencyFormat; }
    public String getDateFormat() { return dateFormat; }

    public static final GeoCountry US = new GeoCountry(
        "US", "en-US", "USD", "$", "$#,##0.00", "MM/dd/yyyy"
    );
    public static final GeoCountry CANADA = new GeoCountry(
        "CA", "en-CA", "CAD", "C$", "C$#,##0.00", "yyyy-MM-dd"
    );

    public static java.util.List<GeoCountry> getAllCountries() {
        return java.util.Arrays.asList(US, CANADA);
    }
}