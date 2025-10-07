#!/bin/bash

# RoadReach Microservices Startup Script
# This script helps you start the microservices in the correct order

set -e

echo "=========================================="
echo "RoadReach Microservices Startup"
echo "=========================================="
echo ""

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
RED='\033[0;31m'
NC='\033[0m' # No Color

# Function to check if a port is in use
check_port() {
    if lsof -Pi :$1 -sTCP:LISTEN -t >/dev/null 2>&1; then
        echo -e "${YELLOW}Warning: Port $1 is already in use${NC}"
        return 1
    else
        return 0
    fi
}

# Function to wait for service to be ready
wait_for_service() {
    local url=$1
    local service_name=$2
    local max_attempts=30
    local attempt=0

    echo -e "${YELLOW}Waiting for $service_name to be ready...${NC}"
    
    while [ $attempt -lt $max_attempts ]; do
        if curl -s "$url" > /dev/null 2>&1; then
            echo -e "${GREEN}$service_name is ready!${NC}"
            return 0
        fi
        attempt=$((attempt + 1))
        sleep 2
    done
    
    echo -e "${RED}$service_name failed to start${NC}"
    return 1
}

# Check prerequisites
echo "Checking prerequisites..."

if ! command -v java &> /dev/null; then
    echo -e "${RED}Java is not installed. Please install Java 21 or later.${NC}"
    exit 1
fi

if ! command -v gradle &> /dev/null; then
    echo -e "${RED}Gradle is not installed. Please install Gradle.${NC}"
    exit 1
fi

if ! command -v docker &> /dev/null; then
    echo -e "${YELLOW}Docker is not installed. You'll need to start PostgreSQL manually.${NC}"
fi

echo -e "${GREEN}Prerequisites OK${NC}"
echo ""

# Check ports
echo "Checking if ports are available..."
ports=(8761 8080 8081 8082 8083 8084 5432)
port_names=("Eureka" "API Gateway" "User Service" "Vehicle Service" "Location Service" "Support Service" "PostgreSQL")

for i in "${!ports[@]}"; do
    if ! check_port ${ports[$i]}; then
        echo -e "${YELLOW}${port_names[$i]} port (${ports[$i]}) is in use. Please stop the service or choose a different port.${NC}"
    fi
done
echo ""

# Start PostgreSQL if Docker is available
echo "Starting PostgreSQL..."
if command -v docker &> /dev/null; then
    if ! docker ps | grep -q roadreach-postgres; then
        docker run -d \
            --name roadreach-postgres \
            -e POSTGRES_USER=postgres \
            -e POSTGRES_PASSWORD=root \
            -p 5432:5432 \
            postgres:15
        
        echo -e "${GREEN}PostgreSQL started in Docker${NC}"
        sleep 5
        
        # Create databases
        docker exec roadreach-postgres psql -U postgres -c "CREATE DATABASE user_service_db;" 2>/dev/null || true
        docker exec roadreach-postgres psql -U postgres -c "CREATE DATABASE vehicle_service_db;" 2>/dev/null || true
        docker exec roadreach-postgres psql -U postgres -c "CREATE DATABASE location_service_db;" 2>/dev/null || true
        docker exec roadreach-postgres psql -U postgres -c "CREATE DATABASE support_service_db;" 2>/dev/null || true
        
        echo -e "${GREEN}Databases created${NC}"
    else
        echo -e "${GREEN}PostgreSQL already running${NC}"
    fi
else
    echo -e "${YELLOW}Please ensure PostgreSQL is running on localhost:5432${NC}"
fi
echo ""

# Function to start a service
start_service() {
    local service_name=$1
    local service_port=$2
    local service_dir=$3
    
    echo "Starting $service_name on port $service_port..."
    
    cd "$service_dir"
    
    # Build the service
    echo "Building $service_name..."
    gradle build -x test > /dev/null 2>&1
    
    # Start the service in background
    gradle bootRun > "../logs/${service_name}.log" 2>&1 &
    
    cd - > /dev/null
    
    echo -e "${GREEN}$service_name started${NC}"
}

# Create logs directory
mkdir -p logs

# Start services in order
echo "=========================================="
echo "Starting services in order..."
echo "=========================================="
echo ""

# 1. Start Eureka Server
if [ -d "eureka-server" ]; then
    start_service "Eureka Server" 8761 "eureka-server"
    wait_for_service "http://localhost:8761" "Eureka Server"
    echo ""
else
    echo -e "${YELLOW}eureka-server directory not found, skipping${NC}"
fi

# 2. Start User Service
if [ -d "user-service" ]; then
    start_service "User Service" 8081 "user-service"
    sleep 10
    echo ""
else
    echo -e "${YELLOW}user-service directory not found, skipping${NC}"
fi

# 3. Start Vehicle Service
if [ -d "vehicle-service" ]; then
    start_service "Vehicle Service" 8082 "vehicle-service"
    sleep 10
    echo ""
else
    echo -e "${YELLOW}vehicle-service directory not found, skipping${NC}"
fi

# 4. Start Location Service
if [ -d "location-service" ]; then
    start_service "Location Service" 8083 "location-service"
    sleep 10
    echo ""
else
    echo -e "${YELLOW}location-service directory not found, skipping${NC}"
fi

# 5. Start API Gateway
if [ -d "api-gateway" ]; then
    start_service "API Gateway" 8080 "api-gateway"
    wait_for_service "http://localhost:8080/actuator/health" "API Gateway"
    echo ""
else
    echo -e "${YELLOW}api-gateway directory not found, skipping${NC}"
fi

echo "=========================================="
echo -e "${GREEN}All services started!${NC}"
echo "=========================================="
echo ""
echo "Service URLs:"
echo "  Eureka Dashboard:  http://localhost:8761"
echo "  API Gateway:       http://localhost:8080"
echo "  User Service:      http://localhost:8081"
echo "  Vehicle Service:   http://localhost:8082"
echo "  Location Service:  http://localhost:8083"
echo ""
echo "Logs are available in the 'logs' directory"
echo ""
echo "To stop all services, run: ./stop-services.sh"
echo ""
