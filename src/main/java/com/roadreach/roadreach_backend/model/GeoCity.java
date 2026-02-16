package com.roadreach.roadreach_backend.model;

import java.util.List;

public class GeoCity {
    private String name;
    private String type; // "city" or "airport"
    private String code; // airport code, optional

    // No-arg constructor for Jackson
    public GeoCity() {
    }

    public GeoCity(String name, String type, String code) {
        this.name = name;
        this.type = type;
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
