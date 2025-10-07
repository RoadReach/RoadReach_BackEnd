package com.roadreach.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

/**
 * API Gateway Application
 * Routes incoming requests to appropriate microservices
 * 
 * Provides:
 * - Centralized routing
 * - Load balancing
 * - CORS configuration
 * - Rate limiting (can be added)
 * - Authentication/Authorization (can be added)
 */
@SpringBootApplication
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            // Route requests to User Service
            .route("user-service", r -> r.path("/api/users/**")
                .uri("lb://user-service"))
            
            // Route requests to Vehicle Service
            .route("vehicle-service", r -> r.path("/api/vehicles/**")
                .uri("lb://vehicle-service"))
            
            // Route requests to Location Service
            .route("location-service", r -> r.path("/api/countries/**", "/api/locations/**")
                .uri("lb://location-service"))
            
            // Route requests to Support Service
            .route("support-service", r -> r.path("/api/support/**")
                .uri("lb://support-service"))
            
            .build();
    }
}
