# User Service

## Overview
User Service handles all user-related operations including authentication, registration, profile management, and password reset.

## Endpoints

### User Management
- `POST /api/users/create` - Create new user
- `POST /api/users/login` - User login
- `GET /api/users/profile/{userid}` - Get user profile
- `PUT /api/users/userData` - Update user profile data
- `DELETE /api/users/profile/{userid}` - Delete user account

### Password Management
- `POST /api/users/send-reset-code` - Send password reset code
- `POST /api/users/verify-reset-code` - Verify reset code
- `POST /api/users/reset-password` - Reset password
- `PUT /api/users/updatePassword` - Update password

### Profile Updates
- `PUT /api/users/updateEmail` - Update email
- `PUT /api/users/updatePhone` - Update phone number

### Geographic Data
- `GET /api/users/geo/countries` - Get list of countries

## Database Tables
- `users` - User credentials and basic info
- `user_data` - User profile and address information
- `password_reset_codes` - Temporary password reset codes

## Configuration
See `application.properties` for:
- Database connection (PostgreSQL)
- Mail server settings
- Eureka server location
- Server port (default: 8081)

## Moving Controllers from Monolith

To move `UserController` from the monolith:
1. Copy the controller class to this service
2. Copy all related models (User, UserData, PasswordResetCode)
3. Copy all related repositories
4. Ensure all dependencies are in build.gradle
5. Update any inter-service communication to use Feign clients

## Inter-Service Communication

If this service needs to call other services (e.g., Location Service for country data), use Feign clients:

```java
@FeignClient(name = "location-service")
public interface LocationServiceClient {
    @GetMapping("/api/countries")
    List<Country> getAllCountries();
}
```
