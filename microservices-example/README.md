# Microservices Example Structure

This directory contains example configurations for converting the RoadReach backend to a microservices architecture.

## Directory Structure

```
microservices-example/
├── eureka-server/          # Service Discovery Server
├── api-gateway/            # API Gateway for routing
├── user-service/           # User management microservice
├── vehicle-service/        # Vehicle management microservice
└── location-service/       # Location management microservice
```

## How to Use

This is a reference implementation showing how to structure the microservices. To implement:

1. Review the MICROSERVICES_GUIDE.md in the root directory
2. Study the example configurations in each service directory
3. Follow the migration steps in the guide
4. Adapt the configurations to your needs

## Quick Start

If you want to try the microservices architecture:

1. Copy the example structure to your project root
2. Update the build.gradle files
3. Move your controllers and models to appropriate services
4. Set up separate databases
5. Start services in order (Eureka → Services → Gateway)

## Notes

- These are example configurations only
- You'll need to move actual code from the monolith to these services
- Database connection strings need to be configured
- Each service should have its own database
