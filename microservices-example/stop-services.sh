#!/bin/bash

# RoadReach Microservices Stop Script
# This script stops all running microservices

echo "=========================================="
echo "Stopping RoadReach Microservices"
echo "=========================================="
echo ""

# Kill processes on specific ports
kill_port() {
    local port=$1
    local service=$2
    
    pid=$(lsof -ti:$port)
    if [ ! -z "$pid" ]; then
        echo "Stopping $service (port $port)..."
        kill -9 $pid
        echo "$service stopped"
    else
        echo "$service (port $port) is not running"
    fi
}

# Stop all services
kill_port 8761 "Eureka Server"
kill_port 8080 "API Gateway"
kill_port 8081 "User Service"
kill_port 8082 "Vehicle Service"
kill_port 8083 "Location Service"
kill_port 8084 "Support Service"

# Stop PostgreSQL Docker container if running
if docker ps | grep -q roadreach-postgres; then
    echo "Stopping PostgreSQL Docker container..."
    docker stop roadreach-postgres
    docker rm roadreach-postgres
    echo "PostgreSQL stopped"
fi

echo ""
echo "All services stopped!"
echo ""
