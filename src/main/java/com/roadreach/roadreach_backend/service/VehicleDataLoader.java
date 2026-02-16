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
        ObjectMapper mapper = new ObjectMapper();
        InputStream is = getClass().getResourceAsStream("/vehicle_inventory.json");
        List<Vehicle> vehicles = mapper.readValue(is, new TypeReference<List<Vehicle>>() {});
        int updated = 0, inserted = 0;
        for (Vehicle v : vehicles) {
            Vehicle existing = vehicleRepository.findById(v.getId()).orElse(null);
            if (existing != null) {
                // Update all fields
                existing.setType(v.getType());
                existing.setCompany(v.getCompany());
                existing.setPrice(v.getPrice());
                existing.setPassengers(v.getPassengers());
                existing.setBags(v.getBags());
                existing.setTransmission(v.getTransmission());
                existing.setEco(v.isEco());
                existing.setLuxury(v.isLuxury());
                existing.setImageUrl(v.getImageUrl());
                vehicleRepository.save(existing);
                updated++;
            } else {
                vehicleRepository.save(v);
                inserted++;
            }
        }
        System.out.println("Upserted vehicles from JSON to PostgreSQL: " + vehicles.size() + " (" + inserted + " inserted, " + updated + " updated)");
    }
}