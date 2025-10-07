# RoadReach Backend

RoadReach is a vehicle rental platform backend built with Spring Boot. This repository contains the monolithic backend application and resources for migrating to a microservices architecture.

## 🏗️ Architecture

### Current Architecture: Monolithic
The application currently runs as a single Spring Boot application with the following components:
- User Management (authentication, profiles, password reset)
- Vehicle Management (inventory, search)
- Location Management (countries, states, cities, airports)
- Support Request Management

### Microservices Architecture
We provide comprehensive guidance and examples for migrating to microservices. See documentation below.

## 📚 Documentation

### For Microservices Implementation
- **[MICROSERVICES_DECISION_GUIDE.md](./MICROSERVICES_DECISION_GUIDE.md)** - Should you use microservices? Decision framework and recommendations
- **[MICROSERVICES_GUIDE.md](./MICROSERVICES_GUIDE.md)** - Complete implementation guide for microservices architecture
- **[microservices-example/](./microservices-example/)** - Example code and configurations for microservices

## 🚀 Quick Start

### Prerequisites
- Java 21 or later
- Gradle 8.14 or later
- PostgreSQL 15 or later

### Running the Monolithic Application

1. **Clone the repository**
   ```bash
   git clone https://github.com/RoadReach/RoadReach_BackEnd.git
   cd RoadReach_BackEnd
   ```

2. **Set up PostgreSQL**
   ```bash
   # Create database
   createdb roadreach
   ```

3. **Configure application**
   Update `src/main/resources/application.properties` with your database credentials:
   ```properties
   spring.datasource.url=jdbc:postgresql://localhost:5432/roadreach
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

4. **Build and run**
   ```bash
   ./gradlew clean build
   ./gradlew bootRun
   ```

The application will start on `http://localhost:8080`

## 🔑 API Endpoints

### User Management (`/api/users`)
- `POST /api/users/create` - Create new user
- `POST /api/users/login` - User login
- `GET /api/users/profile/{userid}` - Get user profile
- `PUT /api/users/userData` - Update user profile
- `DELETE /api/users/profile/{userid}` - Delete user
- `POST /api/users/send-reset-code` - Send password reset code
- `POST /api/users/verify-reset-code` - Verify reset code
- `POST /api/users/reset-password` - Reset password

### Vehicle Management (`/api/vehicles`)
- `GET /api/vehicles` - Search vehicles with filters
- `GET /api/vehicles/price-range` - Get price range

### Location Management (`/api/countries`)
- `GET /api/countries/{countryCode}/states` - Get states by country
- `GET /api/locations/suggest` - Get location suggestions

### Support (`/api/support`)
- `POST /api/support/requests` - Create support request

## 🎯 Considering Microservices?

### Step 1: Assess Your Needs
Read the [MICROSERVICES_DECISION_GUIDE.md](./MICROSERVICES_DECISION_GUIDE.md) to understand:
- When microservices make sense
- Cost-benefit analysis
- Specific recommendations based on team size
- Risk assessment

### Step 2: Review Implementation Guide
If you decide to proceed, read [MICROSERVICES_GUIDE.md](./MICROSERVICES_GUIDE.md) for:
- Architecture overview
- Service breakdown
- Step-by-step implementation
- Best practices

### Step 3: Try the Example
Explore the `microservices-example/` directory:
```bash
cd microservices-example
./start-services.sh
```

This starts:
- Eureka Server (Service Discovery) on port 8761
- API Gateway on port 8080
- User Service on port 8081
- Vehicle Service on port 8082
- Location Service on port 8083

## 🛠️ Technology Stack

### Monolith
- **Framework**: Spring Boot 3.5.4
- **Language**: Java 21
- **Database**: PostgreSQL 15
- **Build Tool**: Gradle 8.14
- **ORM**: Spring Data JPA / Hibernate
- **Email**: Spring Mail (Gmail SMTP)

### Microservices (Optional)
- **Service Discovery**: Netflix Eureka
- **API Gateway**: Spring Cloud Gateway
- **Client-side Load Balancing**: Spring Cloud LoadBalancer
- **Inter-service Communication**: OpenFeign
- **Configuration**: Spring Cloud Config (optional)
- **Distributed Tracing**: Spring Cloud Sleuth + Zipkin (optional)

## 📁 Project Structure

```
RoadReach_BackEnd/
├── src/
│   ├── main/
│   │   ├── java/com/roadreach/roadreach_backend/
│   │   │   ├── controller/          # REST controllers
│   │   │   ├── model/               # Entity models
│   │   │   ├── repository/          # Data repositories
│   │   │   ├── service/             # Business logic
│   │   │   └── RoadreachBackendApplication.java
│   │   └── resources/
│   │       ├── application.properties
│   │       ├── canada.json          # Location data
│   │       ├── united states.json   # Location data
│   │       └── vehicle_inventory.json
│   └── test/                        # Unit tests
├── microservices-example/           # Example microservices implementation
├── build.gradle                     # Gradle configuration
├── MICROSERVICES_GUIDE.md          # Detailed microservices guide
└── MICROSERVICES_DECISION_GUIDE.md # Decision framework
```

## 🧪 Testing

Run tests:
```bash
./gradlew test
```

## 🚢 Deployment

### Monolith Deployment
Build JAR:
```bash
./gradlew bootJar
```

Run JAR:
```bash
java -jar build/libs/roadreach_backend-0.0.1-SNAPSHOT.jar
```

### Microservices Deployment
See [MICROSERVICES_GUIDE.md](./MICROSERVICES_GUIDE.md) for:
- Docker deployment
- Docker Compose setup
- Kubernetes deployment (advanced)

## 🔒 Security Notes

⚠️ **Important**: Before deploying to production:
- Change database credentials
- Use environment variables for sensitive data
- Enable HTTPS/TLS
- Implement proper authentication (OAuth2/JWT)
- Remove/secure actuator endpoints
- Use proper CORS configuration
- Secure mail server credentials

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 License

This project is part of the RoadReach platform.

## 👥 Team

RoadReach Development Team

## 📧 Support

For questions or issues, please create a support request through the application or contact the development team.

---

## 🎓 Learning Resources

### For Microservices
- [Spring Cloud Documentation](https://spring.io/projects/spring-cloud)
- [Microservices Patterns](https://microservices.io/patterns/index.html)
- [Building Microservices Book](https://www.oreilly.com/library/view/building-microservices-2nd/9781492034018/)

### For Spring Boot
- [Spring Boot Documentation](https://spring.io/projects/spring-boot)
- [Spring Data JPA](https://spring.io/projects/spring-data-jpa)
- [Spring REST Docs](https://spring.io/guides/gs/rest-service)

## ❓ FAQ

### Q: Should I use microservices for this project?
A: Read [MICROSERVICES_DECISION_GUIDE.md](./MICROSERVICES_DECISION_GUIDE.md) for a detailed assessment. Short answer: It depends on your team size, scaling needs, and complexity requirements.

### Q: Can I migrate gradually from monolith to microservices?
A: Yes! The [MICROSERVICES_GUIDE.md](./MICROSERVICES_GUIDE.md) explains the Strangler Fig pattern for gradual migration.

### Q: How do I choose which service to extract first?
A: Start with the most independent service with the least dependencies. For RoadReach, Vehicle Service or Location Service are good candidates.

### Q: What if I'm not sure about microservices?
A: Start with a modular monolith. Organize your code by feature/domain, and you can extract services later if needed.

---

**Made with ❤️ by the RoadReach Team**
