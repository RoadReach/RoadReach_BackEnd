# RoadReach Architecture Diagrams

## Current Monolithic Architecture

```
┌─────────────────────────────────────────────────────┐
│                                                     │
│              Frontend Application                   │
│              (React on port 5173)                   │
│                                                     │
└────────────────────┬────────────────────────────────┘
                     │
                     │ HTTP Requests
                     ▼
┌─────────────────────────────────────────────────────┐
│                                                     │
│         RoadReach Backend (port 8080)              │
│              Monolithic Spring Boot                 │
│                                                     │
│  ┌────────────┐  ┌────────────┐  ┌──────────────┐ │
│  │   User     │  │  Vehicle   │  │   Location   │ │
│  │ Controller │  │ Controller │  │  Controller  │ │
│  └────────────┘  └────────────┘  └──────────────┘ │
│  ┌────────────┐  ┌────────────┐  ┌──────────────┐ │
│  │   User     │  │  Vehicle   │  │   Location   │ │
│  │  Service   │  │  Service   │  │   Service    │ │
│  └────────────┘  └────────────┘  └──────────────┘ │
│  ┌────────────┐  ┌────────────┐  ┌──────────────┐ │
│  │   User     │  │  Vehicle   │  │   Location   │ │
│  │ Repository │  │ Repository │  │  Repository  │ │
│  └────────────┘  └────────────┘  └──────────────┘ │
│                                                     │
└────────────────────┬────────────────────────────────┘
                     │
                     │ JDBC
                     ▼
┌─────────────────────────────────────────────────────┐
│                                                     │
│           PostgreSQL Database                       │
│              (port 5432)                            │
│                                                     │
│  ┌──────────┐ ┌──────────┐ ┌────────────────────┐ │
│  │  users   │ │ vehicles │ │  countries/states   │ │
│  │user_data │ │          │ │    cities/airports  │ │
│  └──────────┘ └──────────┘ └────────────────────┘ │
│                                                     │
└─────────────────────────────────────────────────────┘
```

## Proposed Microservices Architecture

```
┌─────────────────────────────────────────────────────┐
│                                                     │
│              Frontend Application                   │
│              (React on port 5173)                   │
│                                                     │
└────────────────────┬────────────────────────────────┘
                     │
                     │ All HTTP Requests
                     ▼
┌─────────────────────────────────────────────────────┐
│                                                     │
│            API Gateway (port 8080)                  │
│          Spring Cloud Gateway                       │
│                                                     │
│  • Request Routing                                  │
│  • CORS Configuration                               │
│  • Load Balancing                                   │
│  • Authentication (future)                          │
│                                                     │
└────────────────────┬────────────────────────────────┘
                     │
                     │ Discovers services via
                     ▼
┌─────────────────────────────────────────────────────┐
│                                                     │
│         Eureka Server (port 8761)                   │
│           Service Discovery                         │
│                                                     │
│  • Service Registration                             │
│  • Health Monitoring                                │
│  • Load Balancing Information                       │
│                                                     │
└─────────────────────────────────────────────────────┘
                     │
                     │ Services register here
                     │
      ┌──────────────┼──────────────┬─────────────┐
      │              │              │             │
      ▼              ▼              ▼             ▼
┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐
│   User   │  │ Vehicle  │  │ Location │  │ Support  │
│ Service  │  │ Service  │  │ Service  │  │ Service  │
│          │  │          │  │          │  │          │
│ Port     │  │ Port     │  │ Port     │  │ Port     │
│ 8081     │  │ 8082     │  │ 8083     │  │ 8084     │
│          │  │          │  │          │  │          │
│ ┌──────┐ │  │ ┌──────┐ │  │ ┌──────┐ │  │ ┌──────┐ │
│ │Ctrler│ │  │ │Ctrler│ │  │ │Ctrler│ │  │ │Ctrler│ │
│ └──────┘ │  │ └──────┘ │  │ └──────┘ │  │ └──────┘ │
│ ┌──────┐ │  │ ┌──────┐ │  │ ┌──────┐ │  │ ┌──────┐ │
│ │ Svc  │ │  │ │ Svc  │ │  │ │ Svc  │ │  │ │ Svc  │ │
│ └──────┘ │  │ └──────┘ │  │ └──────┘ │  │ └──────┘ │
│ ┌──────┐ │  │ ┌──────┐ │  │ ┌──────┐ │  │ ┌──────┐ │
│ │ Repo │ │  │ │ Repo │ │  │ │ Repo │ │  │ │ Repo │ │
│ └──────┘ │  │ └──────┘ │  │ └──────┘ │  │ └──────┘ │
└────┬─────┘  └────┬─────┘  └────┬─────┘  └────┬─────┘
     │             │             │             │
     │ JDBC        │ JDBC        │ JDBC        │ JDBC
     ▼             ▼             ▼             ▼
┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────┐
│  User    │  │ Vehicle  │  │ Location │  │ Support  │
│ Service  │  │ Service  │  │ Service  │  │ Service  │
│ Database │  │ Database │  │ Database │  │ Database │
│          │  │          │  │          │  │          │
│ • users  │  │• vehicles│  │•countries│  │•requests │
│ •userData│  │          │  │• states  │  │          │
│ •pwdReset│  │          │  │• cities  │  │          │
└──────────┘  └──────────┘  └──────────┘  └──────────┘

     PostgreSQL Server (port 5432)
```

## Request Flow - Monolithic Architecture

```
1. User Login Request

   Frontend                Monolith                Database
      │                       │                       │
      │  POST /api/users/login│                       │
      ├──────────────────────>│                       │
      │                       │                       │
      │                       │  SELECT * FROM users  │
      │                       │──────────────────────>│
      │                       │                       │
      │                       │<──────────────────────│
      │                       │   User data           │
      │                       │                       │
      │  200 OK {user data}   │                       │
      │<──────────────────────│                       │
      │                       │                       │
```

## Request Flow - Microservices Architecture

```
1. User Login Request

   Frontend        API Gateway    Eureka     User Service    Database
      │                │             │            │             │
      │ POST           │             │            │             │
      │ /api/users/    │             │            │             │
      │ login          │             │            │             │
      ├───────────────>│             │            │             │
      │                │             │            │             │
      │                │  Where is   │            │             │
      │                │  user-      │            │             │
      │                │  service?   │            │             │
      │                ├────────────>│            │             │
      │                │             │            │             │
      │                │  user-      │            │             │
      │                │  service    │            │             │
      │                │  @ 8081     │            │             │
      │                │<────────────│            │             │
      │                │             │            │             │
      │                │  POST /api/users/login   │             │
      │                ├─────────────────────────>│             │
      │                │             │            │             │
      │                │             │            │  SELECT *   │
      │                │             │            │  FROM users │
      │                │             │            ├────────────>│
      │                │             │            │             │
      │                │             │            │<────────────│
      │                │             │            │  User data  │
      │                │             │            │             │
      │                │  200 OK {user data}      │             │
      │                │<─────────────────────────│             │
      │                │             │            │             │
      │  200 OK        │             │            │             │
      │  {user data}   │             │            │             │
      │<───────────────│             │            │             │
      │                │             │            │             │
```

## Inter-Service Communication

```
Scenario: User Service needs country data from Location Service

┌──────────────┐                              ┌──────────────┐
│              │                              │              │
│     User     │                              │   Location   │
│   Service    │                              │   Service    │
│              │                              │              │
│              │                              │              │
│  ┌────────┐  │     1. Discover service     │  ┌────────┐  │
│  │ Feign  │  │    ┌──────────────────┐     │  │        │  │
│  │ Client │  ├───>│ Eureka Server    │     │  │ REST   │  │
│  │        │  │    │                  │     │  │ API    │  │
│  └────────┘  │    │ Returns:         │     │  │        │  │
│              │    │ location-service │     │  └────────┘  │
│              │    │ @ 8083           │     │              │
│              │    └──────────────────┘     │              │
│              │                              │              │
│              │   2. Make HTTP request       │              │
│              │   GET /api/countries         │              │
│              ├─────────────────────────────>│              │
│              │                              │              │
│              │   3. Return data             │              │
│              │<─────────────────────────────│              │
│              │                              │              │
└──────────────┘                              └──────────────┘
```

## Deployment Architecture

```
                    Docker Host / Kubernetes Cluster
┌─────────────────────────────────────────────────────────────┐
│                                                             │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │   Eureka     │  │ API Gateway  │  │  PostgreSQL  │     │
│  │  Container   │  │  Container   │  │  Container   │     │
│  │  :8761       │  │  :8080       │  │  :5432       │     │
│  └──────────────┘  └──────────────┘  └──────────────┘     │
│                                                             │
│  ┌──────────────┐  ┌──────────────┐  ┌──────────────┐     │
│  │    User      │  │   Vehicle    │  │  Location    │     │
│  │   Service    │  │   Service    │  │   Service    │     │
│  │  Container   │  │  Container   │  │  Container   │     │
│  │  :8081       │  │  :8082       │  │  :8083       │     │
│  └──────────────┘  └──────────────┘  └──────────────┘     │
│                                                             │
│                 roadreach-network (Bridge)                  │
└─────────────────────────────────────────────────────────────┘
```

## Service Boundaries

```
┌─────────────────────────────────────────────────────────────┐
│                     User Service                            │
│                                                             │
│  Owns:                        Endpoints:                    │
│  • User credentials           • POST /api/users/create      │
│  • User profiles              • POST /api/users/login       │
│  • Password resets            • GET  /api/users/profile/:id │
│  • Email notifications        • PUT  /api/users/profile     │
│                               • DELETE /api/users/profile   │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                   Vehicle Service                           │
│                                                             │
│  Owns:                        Endpoints:                    │
│  • Vehicle inventory          • GET /api/vehicles           │
│  • Vehicle search             • GET /api/vehicles/price-    │
│  • Price information          •     range                   │
│                                                             │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                  Location Service                           │
│                                                             │
│  Owns:                        Endpoints:                    │
│  • Countries & States         • GET /api/countries/:code/   │
│  • Cities & Airports          •     states                  │
│  • Geographic data            • GET /api/locations/suggest  │
│                                                             │
└─────────────────────────────────────────────────────────────┘

┌─────────────────────────────────────────────────────────────┐
│                   Support Service                           │
│                                                             │
│  Owns:                        Endpoints:                    │
│  • Support tickets            • POST /api/support/requests  │
│  • Customer inquiries         • GET  /api/support/requests  │
│                                                             │
└─────────────────────────────────────────────────────────────┘
```

## Migration Strategy: Strangler Fig Pattern

```
Phase 1: Monolith with Gateway
┌──────────┐
│ Frontend │
└────┬─────┘
     │
     ▼
┌──────────────┐
│ API Gateway  │
└────┬─────────┘
     │
     ▼
┌──────────────┐
│  Monolith    │
│  All Logic   │
└──────────────┘


Phase 2: Extract Vehicle Service
┌──────────┐
│ Frontend │
└────┬─────┘
     │
     ▼
┌──────────────┐
│ API Gateway  │
└────┬─────────┘
     │
     ├─────────────────┐
     │                 │
     ▼                 ▼
┌──────────┐    ┌──────────────┐
│ Vehicle  │    │  Monolith    │
│ Service  │    │  User        │
│          │    │  Location    │
└──────────┘    │  Support     │
                └──────────────┘


Phase 3: Extract Location Service
┌──────────┐
│ Frontend │
└────┬─────┘
     │
     ▼
┌──────────────┐
│ API Gateway  │
└────┬─────────┘
     │
     ├─────────────┬─────────────┐
     │             │             │
     ▼             ▼             ▼
┌──────────┐ ┌──────────┐ ┌──────────────┐
│ Vehicle  │ │ Location │ │  Monolith    │
│ Service  │ │ Service  │ │  User        │
│          │ │          │ │  Support     │
└──────────┘ └──────────┘ └──────────────┘


Phase 4: Complete Migration
┌──────────┐
│ Frontend │
└────┬─────┘
     │
     ▼
┌──────────────┐
│ API Gateway  │
└────┬─────────┘
     │
     ├──────┬──────┬──────┐
     │      │      │      │
     ▼      ▼      ▼      ▼
┌─────┐┌─────┐┌─────┐┌─────┐
│User ││Veh. ││Loc. ││Supp.│
│Svc  ││Svc  ││Svc  ││Svc  │
└─────┘└─────┘└─────┘└─────┘
```

## Scaling Comparison

### Monolith Scaling (Horizontal)
```
Load Balancer
     │
     ├────────────┬────────────┬────────────┐
     │            │            │            │
     ▼            ▼            ▼            ▼
┌─────────┐  ┌─────────┐  ┌─────────┐  ┌─────────┐
│Instance │  │Instance │  │Instance │  │Instance │
│   1     │  │   2     │  │   3     │  │   4     │
│         │  │         │  │         │  │         │
│ Full    │  │ Full    │  │ Full    │  │ Full    │
│ App     │  │ App     │  │ App     │  │ App     │
└─────────┘  └─────────┘  └─────────┘  └─────────┘

Note: Must scale entire application even if only
      one feature needs more resources
```

### Microservices Scaling (Service-specific)
```
                Load Balancer
                     │
     ┌───────────────┼───────────────┐
     │               │               │
     ▼               ▼               ▼
┌─────────┐     ┌─────────┐     ┌─────────┐
│  User   │     │ Vehicle │     │Location │
│ Service │     │ Service │     │ Service │
│   x1    │     │         │     │   x1    │
└─────────┘     └────┬────┘     └─────────┘
                     │
          ┌──────────┼──────────┬──────────┐
          │          │          │          │
          ▼          ▼          ▼          ▼
     ┌─────────┐┌─────────┐┌─────────┐┌─────────┐
     │ Vehicle ││ Vehicle ││ Vehicle ││ Vehicle │
     │   x2    ││   x3    ││   x4    ││   x5    │
     └─────────┘└─────────┘└─────────┘└─────────┘

Note: Scale only the Vehicle Service which
      handles the most traffic
```

---

## Key Takeaways

1. **Monolith**: Simple, single deployment, shared database
2. **Microservices**: Complex, independent deployments, separate databases
3. **Gateway**: Single entry point for all requests
4. **Eureka**: Dynamic service discovery and health monitoring
5. **Strangler Pattern**: Gradual migration reduces risk
6. **Independent Scaling**: Scale only what needs scaling

Choose the architecture that fits your current needs, not future hypothetical scenarios.
