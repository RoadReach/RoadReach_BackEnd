# RoadReach Microservices Architecture Guide

## Overview

This guide explains how to migrate the RoadReach backend from a monolithic architecture to a microservices architecture using Spring Boot and Spring Cloud.

## Current Architecture

The application currently runs as a **monolithic Spring Boot application** with the following components:
- User management (authentication, profile, password reset)
- Vehicle management (inventory, search)
- Location management (countries, states, cities, airports)
- Support request management

All components share:
- Single PostgreSQL database
- Single deployment unit
- Shared dependencies

## Proposed Microservices Architecture

### Service Breakdown

#### 1. User Service
**Responsibilities:**
- User authentication and authorization
- User registration and profile management
- Password reset functionality
- Email notifications

**Endpoints:**
- `POST /api/users/create` - Create new user
- `POST /api/users/login` - User login
- `GET /api/users/profile/{userid}` - Get user profile
- `PUT /api/users/profile` - Update user profile
- `DELETE /api/users/profile/{userid}` - Delete user
- `POST /api/users/send-reset-code` - Send password reset code
- `POST /api/users/verify-reset-code` - Verify reset code
- `POST /api/users/reset-password` - Reset password

**Database:** user_service_db (PostgreSQL)
- Tables: users, user_data, password_reset_codes

#### 2. Vehicle Service
**Responsibilities:**
- Vehicle inventory management
- Vehicle search and filtering
- Price range queries

**Endpoints:**
- `GET /api/vehicles` - Search vehicles
- `GET /api/vehicles/price-range` - Get price range

**Database:** vehicle_service_db (PostgreSQL)
- Tables: vehicles

#### 3. Location Service
**Responsibilities:**
- Country, state, city, and airport data management
- Location search and suggestions
- Geographic data loading

**Endpoints:**
- `GET /api/countries/{countryCode}/states` - Get states by country
- `GET /api/countries/{countryCode}/cities` - Get cities by state
- `GET /api/locations/suggest` - Location suggestions

**Database:** location_service_db (PostgreSQL)
- Tables: countries, states, cities, airports

#### 4. Support Service
**Responsibilities:**
- Support request management
- Ticket creation and tracking

**Endpoints:**
- `POST /api/support/requests` - Create support request
- `GET /api/support/requests` - Get support requests

**Database:** support_service_db (PostgreSQL)
- Tables: support_requests

### Architecture Components

#### 1. API Gateway (Spring Cloud Gateway)
- Single entry point for all client requests
- Request routing to appropriate microservices
- Load balancing
- Authentication and authorization
- Rate limiting
- CORS configuration

**Port:** 8080

#### 2. Service Discovery (Eureka Server)
- Service registration and discovery
- Health checking
- Load balancing support

**Port:** 8761

#### 3. Config Server (Spring Cloud Config)
- Centralized configuration management
- Environment-specific configurations
- Dynamic configuration updates

**Port:** 8888

#### 4. Individual Microservices
- **User Service:** Port 8081
- **Vehicle Service:** Port 8082
- **Location Service:** Port 8083
- **Support Service:** Port 8084

## Implementation Steps

### Phase 1: Preparation and Infrastructure Setup

#### Step 1.1: Update Dependencies
Add Spring Cloud dependencies to `build.gradle`:

```gradle
ext {
    springCloudVersion = '2023.0.4'
}

dependencies {
    // Existing dependencies...
    
    // Spring Cloud dependencies
    implementation 'org.springframework.cloud:spring-cloud-starter-netflix-eureka-client'
    implementation 'org.springframework.cloud:spring-cloud-starter-gateway'
    implementation 'org.springframework.cloud:spring-cloud-starter-config'
    implementation 'org.springframework.cloud:spring-cloud-starter-loadbalancer'
    implementation 'org.springframework.cloud:spring-cloud-starter-openfeign'
}

dependencyManagement {
    imports {
        mavenBom "org.springframework.cloud:spring-cloud-dependencies:${springCloudVersion}"
    }
}
```

#### Step 1.2: Create Multi-Module Project Structure

Restructure the project into multiple modules:

```
roadreach-backend/
├── eureka-server/
│   ├── src/main/java/
│   └── src/main/resources/
├── api-gateway/
│   ├── src/main/java/
│   └── src/main/resources/
├── config-server/
│   ├── src/main/java/
│   └── src/main/resources/
├── user-service/
│   ├── src/main/java/
│   └── src/main/resources/
├── vehicle-service/
│   ├── src/main/java/
│   └── src/main/resources/
├── location-service/
│   ├── src/main/java/
│   └── src/main/resources/
├── support-service/
│   ├── src/main/java/
│   └── src/main/resources/
└── build.gradle (root)
```

### Phase 2: Create Infrastructure Services

#### Step 2.1: Eureka Server

**EurekaServerApplication.java:**
```java
package com.roadreach.eurekaserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;

@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication {
    public static void main(String[] args) {
        SpringApplication.run(EurekaServerApplication.class, args);
    }
}
```

**application.properties:**
```properties
spring.application.name=eureka-server
server.port=8761

eureka.client.register-with-eureka=false
eureka.client.fetch-registry=false
eureka.server.enable-self-preservation=false
```

#### Step 2.2: API Gateway

**ApiGatewayApplication.java:**
```java
package com.roadreach.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class ApiGatewayApplication {
    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
            .route("user-service", r -> r.path("/api/users/**")
                .uri("lb://user-service"))
            .route("vehicle-service", r -> r.path("/api/vehicles/**")
                .uri("lb://vehicle-service"))
            .route("location-service", r -> r.path("/api/countries/**", "/api/locations/**")
                .uri("lb://location-service"))
            .route("support-service", r -> r.path("/api/support/**")
                .uri("lb://support-service"))
            .build();
    }
}
```

**application.properties:**
```properties
spring.application.name=api-gateway
server.port=8080

eureka.client.service-url.defaultZone=http://localhost:8761/eureka/

# CORS Configuration
spring.cloud.gateway.globalcors.corsConfigurations.[/**].allowedOrigins=http://localhost:5173
spring.cloud.gateway.globalcors.corsConfigurations.[/**].allowedMethods=GET,POST,PUT,DELETE,OPTIONS
spring.cloud.gateway.globalcors.corsConfigurations.[/**].allowedHeaders=*
spring.cloud.gateway.globalcors.corsConfigurations.[/**].allowCredentials=true
```

### Phase 3: Create Microservices

#### Step 3.1: User Service

**UserServiceApplication.java:**
```java
package com.roadreach.userservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class UserServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserServiceApplication.class, args);
    }
}
```

**application.properties:**
```properties
spring.application.name=user-service
server.port=8081

# Eureka Configuration
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
eureka.instance.prefer-ip-address=true

# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/user_service_db
spring.datasource.username=postgres
spring.datasource.password=root
spring.jpa.hibernate.ddl-auto=update

# Mail Configuration
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=roadreach25@gmail.com
spring.mail.password=tnkizvzhtcwnvrek
spring.mail.properties.mail.smtp.auth=true
spring.mail.properties.mail.smtp.starttls.enable=true
```

Move the following files to user-service:
- `UserController.java`
- `User.java`, `UserData.java`, `PasswordResetCode.java`
- `UserRepository.java`, `UserDataRepository.java`, `PasswordResetCodeRepository.java`

#### Step 3.2: Vehicle Service

**VehicleServiceApplication.java:**
```java
package com.roadreach.vehicleservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class VehicleServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(VehicleServiceApplication.class, args);
    }
}
```

**application.properties:**
```properties
spring.application.name=vehicle-service
server.port=8082

# Eureka Configuration
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
eureka.instance.prefer-ip-address=true

# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/vehicle_service_db
spring.datasource.username=postgres
spring.datasource.password=root
spring.jpa.hibernate.ddl-auto=update
```

Move the following files to vehicle-service:
- `VehicleController.java`
- `Vehicle.java`
- `VehicleRepository.java`, `VehicleService.java`, `VehicleDataLoader.java`
- `vehicle_inventory.json`

#### Step 3.3: Location Service

**LocationServiceApplication.java:**
```java
package com.roadreach.locationservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class LocationServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(LocationServiceApplication.class, args);
    }
}
```

**application.properties:**
```properties
spring.application.name=location-service
server.port=8083

# Eureka Configuration
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
eureka.instance.prefer-ip-address=true

# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/location_service_db
spring.datasource.username=postgres
spring.datasource.password=root
spring.jpa.hibernate.ddl-auto=update
```

Move the following files to location-service:
- `CountryController.java`
- `Country.java`, `State.java`, `City.java`, `Airport.java`
- `GeoCountry.java`, `GeoState.java`, `GeoCity.java`
- `CountryRepository.java`, `StateRepository.java`, `CityRepository.java`, `AirportRepository.java`
- `CountryDataLoader.java`
- `canada.json`, `united states.json`

#### Step 3.4: Support Service

**SupportServiceApplication.java:**
```java
package com.roadreach.supportservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class SupportServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(SupportServiceApplication.class, args);
    }
}
```

**application.properties:**
```properties
spring.application.name=support-service
server.port=8084

# Eureka Configuration
eureka.client.service-url.defaultZone=http://localhost:8761/eureka/
eureka.instance.prefer-ip-address=true

# Database Configuration
spring.datasource.url=jdbc:postgresql://localhost:5432/support_service_db
spring.datasource.username=postgres
spring.datasource.password=root
spring.jpa.hibernate.ddl-auto=update
```

Move the following files to support-service:
- `SupportRequestController.java`
- `SupportRequest.java`
- `SupportRequestRepository.java`

### Phase 4: Inter-Service Communication

For services that need to communicate with each other, use OpenFeign clients:

#### Example: User Service calling Location Service

**LocationServiceClient.java (in user-service):**
```java
package com.roadreach.userservice.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "location-service")
public interface LocationServiceClient {
    
    @GetMapping("/api/countries")
    List<Country> getAllCountries();
    
    @GetMapping("/api/countries/{code}/states")
    List<State> getStates(@PathVariable String code);
}
```

Enable Feign in the application:
```java
@EnableFeignClients
@SpringBootApplication
public class UserServiceApplication {
    // ...
}
```

### Phase 5: Database Migration

Create separate databases for each service:

```sql
-- User Service Database
CREATE DATABASE user_service_db;
\c user_service_db;
-- Tables: users, user_data, password_reset_codes

-- Vehicle Service Database
CREATE DATABASE vehicle_service_db;
\c vehicle_service_db;
-- Tables: vehicles

-- Location Service Database
CREATE DATABASE location_service_db;
\c location_service_db;
-- Tables: countries, states, cities, airports

-- Support Service Database
CREATE DATABASE support_service_db;
\c support_service_db;
-- Tables: support_requests
```

### Phase 6: Deployment

#### Local Development
Start services in order:
1. Eureka Server (port 8761)
2. Config Server (port 8888) - optional
3. User Service (port 8081)
4. Vehicle Service (port 8082)
5. Location Service (port 8083)
6. Support Service (port 8084)
7. API Gateway (port 8080)

#### Docker Deployment

Create a `docker-compose.yml`:

```yaml
version: '3.8'
services:
  postgres:
    image: postgres:15
    environment:
      POSTGRES_USER: postgres
      POSTGRES_PASSWORD: root
    ports:
      - "5432:5432"
    volumes:
      - postgres-data:/var/lib/postgresql/data

  eureka-server:
    build: ./eureka-server
    ports:
      - "8761:8761"
    environment:
      SPRING_PROFILES_ACTIVE: docker

  api-gateway:
    build: ./api-gateway
    ports:
      - "8080:8080"
    depends_on:
      - eureka-server
    environment:
      EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE: http://eureka-server:8761/eureka/

  user-service:
    build: ./user-service
    ports:
      - "8081:8081"
    depends_on:
      - eureka-server
      - postgres
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/user_service_db
      EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE: http://eureka-server:8761/eureka/

  vehicle-service:
    build: ./vehicle-service
    ports:
      - "8082:8082"
    depends_on:
      - eureka-server
      - postgres
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/vehicle_service_db
      EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE: http://eureka-server:8761/eureka/

  location-service:
    build: ./location-service
    ports:
      - "8083:8083"
    depends_on:
      - eureka-server
      - postgres
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/location_service_db
      EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE: http://eureka-server:8761/eureka/

  support-service:
    build: ./support-service
    ports:
      - "8084:8084"
    depends_on:
      - eureka-server
      - postgres
    environment:
      SPRING_DATASOURCE_URL: jdbc:postgresql://postgres:5432/support_service_db
      EUREKA_CLIENT_SERVICE_URL_DEFAULTZONE: http://eureka-server:8761/eureka/

volumes:
  postgres-data:
```

## Benefits of Microservices Architecture

### Advantages

1. **Independent Deployment**: Each service can be deployed independently without affecting others
2. **Technology Flexibility**: Different services can use different technologies/frameworks
3. **Scalability**: Scale individual services based on demand
4. **Fault Isolation**: Failure in one service doesn't bring down the entire application
5. **Team Autonomy**: Different teams can work on different services independently
6. **Better Resource Utilization**: Allocate resources based on service needs

### Challenges

1. **Increased Complexity**: Managing multiple services is more complex than a monolith
2. **Distributed System Issues**: Network latency, service discovery, load balancing
3. **Data Consistency**: Maintaining consistency across distributed databases
4. **Testing Complexity**: Integration testing becomes more challenging
5. **Deployment Overhead**: Need to deploy and monitor multiple services
6. **Debugging Difficulty**: Tracing requests across multiple services

## Migration Strategy

### Strangler Fig Pattern

Instead of migrating everything at once, use the Strangler Fig pattern:

1. **Phase 1**: Set up API Gateway and keep monolith running
2. **Phase 2**: Extract one service (e.g., Vehicle Service) and route traffic through gateway
3. **Phase 3**: Extract another service (e.g., Location Service)
4. **Phase 4**: Continue until all services are extracted
5. **Phase 5**: Decommission the monolith

### Gradual Migration Steps

1. **Identify Service Boundaries**: Map existing code to logical services
2. **Extract One Service**: Start with the least dependent service (e.g., Vehicle Service)
3. **Set Up Infrastructure**: Implement Eureka, API Gateway
4. **Migrate Database**: Create separate database for the extracted service
5. **Update API Gateway**: Route requests to the new service
6. **Test Thoroughly**: Ensure the new service works correctly
7. **Monitor and Optimize**: Use logging, metrics, and tracing
8. **Repeat**: Extract the next service

## Best Practices

1. **Service Size**: Keep services small and focused on a single business capability
2. **API Design**: Design clear, versioned APIs with proper documentation
3. **Error Handling**: Implement circuit breakers (Resilience4j) for fault tolerance
4. **Monitoring**: Use distributed tracing (Sleuth + Zipkin) and centralized logging (ELK stack)
5. **Security**: Implement OAuth2/JWT for authentication and authorization
6. **Configuration**: Use Spring Cloud Config for centralized configuration
7. **Testing**: Implement comprehensive unit, integration, and contract tests
8. **Documentation**: Maintain up-to-date documentation for each service

## Monitoring and Observability

### Recommended Tools

1. **Distributed Tracing**: Spring Cloud Sleuth + Zipkin
2. **Logging**: ELK Stack (Elasticsearch, Logstash, Kibana)
3. **Metrics**: Micrometer + Prometheus + Grafana
4. **Health Checks**: Spring Boot Actuator
5. **API Documentation**: Swagger/OpenAPI

### Implementation Example

Add to each service:

```gradle
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-actuator'
    implementation 'io.micrometer:micrometer-registry-prometheus'
    implementation 'org.springframework.cloud:spring-cloud-starter-sleuth'
    implementation 'org.springframework.cloud:spring-cloud-sleuth-zipkin'
}
```

```properties
# Actuator endpoints
management.endpoints.web.exposure.include=*
management.endpoint.health.show-details=always

# Sleuth
spring.sleuth.sampler.probability=1.0

# Zipkin
spring.zipkin.base-url=http://localhost:9411
```

## Conclusion

Migrating to microservices is a significant architectural change that brings both benefits and challenges. For the RoadReach application, microservices can provide better scalability, independent deployment, and team autonomy. However, it requires careful planning, proper infrastructure, and ongoing maintenance.

Start with the Strangler Fig pattern and migrate gradually to minimize risks. Focus on getting the infrastructure right (Eureka, API Gateway) before extracting services. Monitor performance and adjust the architecture as needed.

## Additional Resources

- [Spring Cloud Documentation](https://spring.io/projects/spring-cloud)
- [Microservices Patterns](https://microservices.io/patterns/index.html)
- [Building Microservices by Sam Newman](https://www.oreilly.com/library/view/building-microservices-2nd/9781492034018/)
- [Spring Cloud Gateway Documentation](https://spring.io/projects/spring-cloud-gateway)
- [Netflix Eureka Documentation](https://spring.io/guides/gs/service-registration-and-discovery/)
