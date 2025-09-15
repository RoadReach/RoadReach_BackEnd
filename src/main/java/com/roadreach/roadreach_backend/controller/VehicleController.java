package com.roadreach.roadreach_backend.controller;

import com.roadreach.roadreach_backend.model.Vehicle;
import com.roadreach.roadreach_backend.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/vehicles")
@CrossOrigin(origins = "*")
public class VehicleController {

    @Autowired
    private VehicleService vehicleService;

    @GetMapping
    public List<Vehicle> getVehicles(
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            @RequestParam(required = false) String types, // CSV
            @RequestParam(required = false) String pickuplocation // City
    ) {
        List<String> typeList = (types != null && !types.isEmpty())
                ? Arrays.asList(types.split(","))
                : null;
        return vehicleService.searchByCity(pickuplocation);
    }

    @GetMapping("/price-range")
    public Map<String, Integer> getPriceRange() {
        return Map.of(
                "min", vehicleService.getMinPrice(),
                "max", vehicleService.getMaxPrice()
        );
    }
}
