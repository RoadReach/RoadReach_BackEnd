package com.roadreach.locationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

/**
 * Location Service Application
 * Manages geographic data including countries, states, cities, and airports
 * 
 * Responsibilities:
 * - Country and state data management
 * - City and airport information
 * - Location search and suggestions
 * - Geographic data loading
 */
@SpringBootApplication
@EnableDiscoveryClient
public class LocationServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(LocationServiceApplication.class, args);
    }
}
