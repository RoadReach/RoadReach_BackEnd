package com.roadreach.roadreach_backend.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.roadreach.roadreach_backend.model.Vehicle;
import com.roadreach.roadreach_backend.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.List;

@Component
public class VehicleDataLoader implements CommandLineRunner {

    @Autowired
    private VehicleRepository vehicleRepository;

    @Override
    public void run(String... args) throws Exception {
        if (vehicleRepository.count() == 0) { // Only load if DB is empty
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = getClass().getResourceAsStream("/vehicle_inventory.json");
            List<Vehicle> vehicles = mapper.readValue(is, new TypeReference<List<Vehicle>>() {});
            vehicleRepository.saveAll(vehicles);
            System.out.println("Loaded vehicles from JSON to PostgreSQL: " + vehicles.size());
        }
    }
}