package com.roadreach.roadreach_backend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.roadreach.roadreach_backend.model.Vehicle;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VehicleService {
    private final List<Vehicle> vehicles = new ArrayList<>();

    @PostConstruct
    public void init() {
        loadVehiclesFromJson();
    }

    private void loadVehiclesFromJson() {
        ObjectMapper mapper = new ObjectMapper();
        try (InputStream is = getClass().getResourceAsStream("/vehicle_inventory.json")) {
            if (is == null) {
                throw new RuntimeException("vehicle_inventory.json not found in resources");
            }
            List<Vehicle> loaded = mapper.readValue(is, new TypeReference<List<Vehicle>>() {});
            vehicles.clear();
            vehicles.addAll(loaded);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load vehicle inventory from JSON", e);
        }
    }

    public List<Vehicle> search(Integer minPrice, Integer maxPrice, List<String> types) {
        return vehicles.stream()
                .filter(v -> minPrice == null || v.getPrice() >= minPrice)
                .filter(v -> maxPrice == null || v.getPrice() <= maxPrice)
                .filter(v -> types == null || types.isEmpty() || types.contains(v.getType()))
                .collect(Collectors.toList());
    }

    public int getMinPrice() {
        return vehicles.isEmpty() ? 0 : vehicles.stream().mapToInt(Vehicle::getPrice).min().orElse(0);
    }

    public int getMaxPrice() {
        return vehicles.isEmpty() ? 500 : vehicles.stream().mapToInt(Vehicle::getPrice).max().orElse(500);
    }

    public List<Vehicle> getAllVehicles() {
        return Collections.unmodifiableList(vehicles);
    }
}