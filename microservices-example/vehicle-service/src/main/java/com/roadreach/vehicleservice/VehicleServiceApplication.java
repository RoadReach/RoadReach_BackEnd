package com.roadreach.vehicleservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Vehicle Service Application
 * Manages vehicle inventory and search operations
 * 
 * Responsibilities:
 * - Vehicle data management
 * - Vehicle search and filtering
 * - Price range queries
 * - Vehicle availability
 */
@SpringBootApplication
@EnableDiscoveryClient
public class VehicleServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(VehicleServiceApplication.class, args);
    }
}
