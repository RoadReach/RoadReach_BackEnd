package com.roadreach.roadreach_backend.service;

import com.roadreach.roadreach_backend.model.Vehicle;
import com.roadreach.roadreach_backend.repository.VehicleRepository;
import org.springframework.stereotype.Service;

import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VehicleService {
    @Autowired
    private VehicleRepository vehicleRepository;

    public List<Vehicle> search(Integer minPrice, Integer maxPrice, List<String> types) {
        return vehicleRepository.findAll().stream()
                .filter(v -> minPrice == null || v.getPrice() >= minPrice)
                .filter(v -> maxPrice == null || v.getPrice() <= maxPrice)
                .filter(v -> types == null || types.isEmpty() || types.contains(v.getType()))
                .collect(Collectors.toList());
    }

    public int getMinPrice() {
        return vehicleRepository.findAll().stream().mapToInt(Vehicle::getPrice).min().orElse(0);
    }

    public int getMaxPrice() {
        return vehicleRepository.findAll().stream().mapToInt(Vehicle::getPrice).max().orElse(500);
    }
}