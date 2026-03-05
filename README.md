# JWT Authentication & Authorization API

A production-ready REST API built with Spring Boot featuring JWT authentication, role-based access control (RBAC), and comprehensive task management system.

[![Java](https://img.shields.io/badge/Java-17+-orange.svg)](https://www.oracle.com/java/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.x-brightgreen.svg)](https://spring.io/projects/spring-boot)

## 📋 Table of Contents

- [Features](#features)
- [Architecture](#architecture)
- [Technologies Used](#technologies-used)
- [Getting Started](#getting-started)
- [Environment Variables](#environment-variables)
- [API Documentation](#api-documentation)
- [Authentication Flow](#authentication-flow)
- [Project Structure](#project-structure)
- [Testing](#testing)
- [Security Best Practices](#security-best-practices)
- [Contributing](#contributing)
- [License](#license)

---

## ✨ Features

### Authentication & Authorization
- ✅ JWT-based stateless authentication
- ✅ Role-based access control (ADMIN, USER)
- ✅ Secure password encryption with BCrypt
- ✅ Token expiration (24 hours default)
- ✅ Auto-login after registration

### User Management (Admin)
- ✅ View all registered users
- ✅ Get user by ID
- ✅ Filter users by role
- ✅ Update user details
- ✅ Delete users

### Task Management (User)
- ✅ CRUD operations for personal tasks
- ✅ Task status tracking (PENDING, IN_PROGRESS, COMPLETED, CANCELLED)
- ✅ Priority levels (LOW, MEDIUM, HIGH, URGENT)
- ✅ Filter tasks by status and priority
- ✅ Task count analytics
- ✅ Timestamps for creation and updates

### Additional Features
- ✅ Global exception handling with custom error responses
- ✅ Input validation with detailed error messages
- ✅ RESTful API design principles
- ✅ Three-layer architecture (Controller → Service → Repository)
- ✅ DTO pattern for data transfer
- ✅ Comprehensive logging
- ✅ H2 in-memory database for development
- ✅ Automatic database initialization with default users

---

## 🏗️ Architecture

### Three-Layer Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    CLIENT LAYER                         │
│              (Postman, Browser, Mobile App)             │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│                 CONTROLLER LAYER                        │
│  • Handles HTTP requests/responses                      │
│  • Input validation (@Valid)                            │
│  • URL mapping (@GetMapping, @PostMapping)              │
│  • Returns ResponseEntity with HTTP status              │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│                  SERVICE LAYER                          │
│  • Business logic implementation                        │
│  • Transaction management (@Transactional)              │
│  • DTO ↔ Entity conversion                              │
│  • Custom exception handling                            │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│                REPOSITORY LAYER                         │
│  • Data access with JPA/Hibernate                       │
│  • Database operations (CRUD)                           │
│  • Custom queries with @Query                           │
└─────────────────────────────────────────────────────────┘
                          ↓
┌─────────────────────────────────────────────────────────┐
│                    DATABASE (H2)                        │
└─────────────────────────────────────────────────────────┘
```

### Request Processing Flow

```
Request JSON → DTO → Validation → Service → Entity → Repository → Database
                ↓                                                    ↓
         Validation Errors                                    Database Operations
                ↓                                                    ↓
           400 Response ←─────────────────────────── Response DTO ← Entity
```

### JWT Authentication Flow

```
1. User Registration/Login (POST /api/auth/register or /api/auth/login)
   ↓
2. AuthService validates credentials
   ↓
3. JwtUtil generates JWT token (valid for 24 hours)
   ↓
4. Client receives token in response
   ↓
5. Client includes token in subsequent requests:
   Authorization: Bearer <token>
   ↓
6. JwtAuthenticationFilter intercepts request
   ↓
7. Validates token and extracts user information
   ↓
8. Sets authentication in SecurityContext
   ↓
9. Controller accesses authenticated user via @AuthenticationPrincipal
   ↓
10. Business logic executes with user context
```

---

## 🛠️ Technologies Used

### Backend
- **Java 17+** - Programming language
- **Spring Boot 3.x** - Application framework
- **Spring Security** - Authentication & authorization
- **Spring Data JPA** - Data persistence
- **Hibernate** - ORM framework

### Security
- **JWT (JSON Web Token)** - Stateless authentication
- **BCrypt** - Password hashing
- **JJWT (io.jsonwebtoken)** - JWT implementation

### Database
- **H2 Database** - In-memory database for development
- Can be easily switched to PostgreSQL, MySQL, etc.

### Validation & Utils
- **Bean Validation (JSR-380)** - Input validation
- **Lombok** - Reduce boilerplate code

### Build Tool
- **Maven** - Dependency management and build

---

## 🚀 Getting Started

### Prerequisites

- Java 17 or higher
- Maven 3.6+
- IDE (IntelliJ IDEA, Eclipse, or VS Code)
- Postman (for API testing)

### Installation

1. **Clone the repository**
   ```bash
   git clone https://github.com/yourusername/jwt-auth-api.git
   cd jwt-auth-api
   ```

2. **Configure environment variables**
   ```bash
   # Copy the example file
   cp .env.example .env
   
   # Edit .env with your values
   nano .env
   ```

3. **Build the project**
   ```bash
   mvn clean install
   ```

4. **Run the application**
   ```bash
   mvn spring-boot:run
   ```

5. **Access the application**
    - API Base URL: `http://localhost:8080`
    - H2 Console: `http://localhost:8080/h2-console`
        - JDBC URL: `jdbc:h2:mem:jwtauthdb`
        - Username: `sa`
        - Password: (empty or as configured)

### Default Users

The application creates two default users on startup:

| Username | Password | Role | Description |
|----------|----------|------|-------------|
| `admin` | `admin123` | ADMIN | Full system access |
| `user` | `user123` | USER | Task management access |

**⚠️ Important:** Change these credentials in production!

---

## 🔐 Environment Variables

Create a `.env` file in the project root (copy from `.env.example`):

```bash
# Server Configuration
SERVER_PORT=8080

# Database Configuration
DB_USERNAME=sa
DB_PASSWORD=your_secure_password

# JWT Configuration (CRITICAL: Change in production!)
JWT_SECRET=YourVeryLongAndSecureSecretKeyHere123456789
JWT_EXPIRATION=86400000

# Logging
LOG_LEVEL=INFO
SHOW_SQL=true
```

### Setting Environment Variables

**Windows (CMD):**
```cmd
set JWT_SECRET=your_secret_key
set DB_PASSWORD=your_password
mvn spring-boot:run
```

**Linux/Mac (Bash):**
```bash
export JWT_SECRET=your_secret_key
export DB_PASSWORD=your_password
mvn spring-boot:run
```

**IntelliJ IDEA:**
1. Run → Edit Configurations
2. Environment variables → Add variables
3. Click OK and run

---

## 📚 API Documentation

### Base URL
```
http://localhost:8080/api
```

### Authentication Endpoints

#### Register New User
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "password": "secure123",
  "email": "john@example.com",
  "fullName": "John Doe"
}

Response: 201 Created
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "id": 3,
  "username": "john_doe",
  "email": "john@example.com",
  "fullName": "John Doe",
  "role": "USER"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "user",
  "password": "user123"
}

Response: 200 OK
{
  "token": "eyJhbGciOiJIUzUxMiJ9...",
  "type": "Bearer",
  "id": 2,
  "username": "user",
  "email": "user@example.com",
  "fullName": "Regular User",
  "role": "USER"
}
```

### Admin Endpoints (Requires ADMIN role)

#### Get All Users
```http
GET /api/admin/users
Authorization: Bearer <admin_token>

Response: 200 OK
[
  {
    "id": 1,
    "username": "admin",
    "email": "admin@example.com",
    "fullName": "System Administrator",
    "role": "ADMIN",
    "enabled": true,
    "createdAt": "2026-01-28T10:00:00",
    "updatedAt": "2026-01-28T10:00:00"
  }
]
```

#### Get User By ID
```http
GET /api/admin/users/{id}
Authorization: Bearer <admin_token>

Response: 200 OK
```

#### Get Users By Role
```http
GET /api/admin/users/role/{role}
Authorization: Bearer <admin_token>

Response: 200 OK
```

#### Update User
```http
PUT /api/admin/users/{id}
Authorization: Bearer <admin_token>
Content-Type: application/json

{
  "email": "newemail@example.com",
  "fullName": "Updated Name",
  "password": "newpassword123"
}

Response: 200 OK
```

#### Delete User
```http
DELETE /api/admin/users/{id}
Authorization: Bearer <admin_token>

Response: 200 OK
{
  "success": true,
  "message": "User deleted successfully",
  "timestamp": "2026-01-28T10:30:00"
}
```

### User Endpoints (Requires USER role)

#### Get All My Tasks
```http
GET /api/user/tasks
Authorization: Bearer <user_token>

Response: 200 OK
[
  {
    "id": 1,
    "title": "Complete project",
    "description": "Finish JWT implementation",
    "status": "IN_PROGRESS",
    "priority": "HIGH",
    "userId": 2,
    "username": "user",
    "createdAt": "2026-01-28T10:00:00",
    "updatedAt": "2026-01-28T10:00:00"
  }
]
```

#### Get Task By ID
```http
GET /api/user/tasks/{id}
Authorization: Bearer <user_token>

Response: 200 OK
```

#### Filter Tasks By Status
```http
GET /api/user/tasks/status/{status}
Authorization: Bearer <user_token>

Valid statuses: PENDING, IN_PROGRESS, COMPLETED, CANCELLED
Response: 200 OK
```

#### Filter Tasks By Priority
```http
GET /api/user/tasks/priority/{priority}
Authorization: Bearer <user_token>

Valid priorities: LOW, MEDIUM, HIGH, URGENT
Response: 200 OK
```

#### Get Task Count
```http
GET /api/user/tasks/count
Authorization: Bearer <user_token>

Response: 200 OK
5
```

#### Create Task
```http
POST /api/user/tasks
Authorization: Bearer <user_token>
Content-Type: application/json

{
  "title": "New Task",
  "description": "Task description",
  "status": "PENDING",
  "priority": "HIGH"
}

Response: 201 Created
```

#### Update Task
```http
PUT /api/user/tasks/{id}
Authorization: Bearer <user_token>
Content-Type: application/json

{
  "title": "Updated Task",
  "status": "COMPLETED",
  "priority": "MEDIUM"
}

Response: 200 OK
```

#### Delete Task
```http
DELETE /api/user/tasks/{id}
Authorization: Bearer <user_token>

Response: 200 OK
{
  "success": true,
  "message": "Task deleted successfully",
  "timestamp": "2026-01-28T10:30:00"
}
```

### Error Responses

#### 400 Bad Request (Validation Error)
```json
{
  "timestamp": "2026-01-28T10:30:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Validation failed",
  "path": "/api/auth/register",
  "details": [
    "username: Username must be between 3 and 50 characters",
    "email: Email must be valid"
  ]
}
```

#### 401 Unauthorized
```json
{
  "timestamp": "2026-01-28T10:30:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Invalid username or password",
  "path": "/api/auth/login"
}
```

#### 403 Forbidden
```json
{
  "timestamp": "2026-01-28T10:30:00",
  "status": 403,
  "error": "Forbidden",
  "message": "Access denied",
  "path": "/api/admin/users"
}
```

#### 404 Not Found
```json
{
  "timestamp": "2026-01-28T10:30:00",
  "status": 404,
  "error": "Not Found",
  "message": "Task not found with id: 999",
  "path": "/api/user/tasks/999"
}
```

#### 409 Conflict
```json
{
  "timestamp": "2026-01-28T10:30:00",
  "status": 409,
  "error": "Conflict",
  "message": "Username already exists: john_doe",
  "path": "/api/auth/register"
}
```

---

## 🔒 Authentication Flow

### Step-by-Step Process

1. **User Registration/Login**
    - User sends credentials to `/api/auth/register` or `/api/auth/login`
    - Server validates credentials

2. **Token Generation**
    - If valid, server generates JWT token using secret key
    - Token contains user information and expiration time
    - Token is signed with HS512 algorithm

3. **Token Response**
    - Server returns token to client
    - Client stores token (localStorage, sessionStorage, or memory)

4. **Authenticated Requests**
    - Client includes token in Authorization header: `Bearer <token>`
    - Server intercepts request via `JwtAuthenticationFilter`

5. **Token Validation**
    - Filter extracts and validates token
    - Checks signature, expiration, and format
    - Extracts username from token claims

6. **User Loading**
    - Loads user details from database
    - Creates authentication object with roles/authorities

7. **Security Context**
    - Sets authentication in `SecurityContextHolder`
    - Spring Security uses this for authorization decisions

8. **Controller Access**
    - Controller accesses authenticated user via `@AuthenticationPrincipal`
    - Executes business logic with user context

### Token Structure

```
eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ1c2VyIiwiaWF0IjoxNjQzNzIwNDAwLCJleHAiOjE2NDM4MDY4MDB9.signature
│                     │                                                                      │
│                     │                                                                      │
Header (Algorithm)    Payload (Claims)                                                    Signature
```

**Header:**
```json
{
  "alg": "HS512"
}
```

**Payload:**
```json
{
  "sub": "user",
  "iat": 1643720400,
  "exp": 1643806800
}
```

---

## 📁 Project Structure

```
jwt-auth-api/
├── src/
│   ├── main/
│   │   ├── java/com/example/jwtauth/
│   │   │   ├── config/                  # Configuration classes
│   │   │   │   ├── DataInitializer.java
│   │   │   │   └── SecurityConfig.java
│   │   │   ├── controller/              # REST Controllers
│   │   │   │   ├── AuthController.java
│   │   │   │   ├── AdminController.java
│   │   │   │   └── UserController.java
│   │   │   ├── dto/                     # Data Transfer Objects
│   │   │   │   ├── request/
│   │   │   │   │   ├── LoginRequestDTO.java
│   │   │   │   │   ├── RegisterRequestDTO.java
│   │   │   │   │   ├── TaskRequestDTO.java
│   │   │   │   │   └── UpdateUserRequestDTO.java
│   │   │   │   └── response/
│   │   │   │       ├── AuthResponseDTO.java
│   │   │   │       ├── UserResponseDTO.java
│   │   │   │       ├── TaskResponseDTO.java
│   │   │   │       └── ApiResponseDTO.java
│   │   │   ├── entity/                  # JPA Entities
│   │   │   │   ├── User.java
│   │   │   │   ├── Task.java
│   │   │   │   ├── Role.java (enum)
│   │   │   │   ├── TaskStatus.java (enum)
│   │   │   │   └── Priority.java (enum)
│   │   │   ├── exception/               # Custom Exceptions
│   │   │   │   ├── GlobalExceptionHandler.java
│   │   │   │   ├── ResourceNotFoundException.java
│   │   │   │   ├── BadRequestException.java
│   │   │   │   ├── UnauthorizedException.java
│   │   │   │   ├── DuplicateResourceException.java
│   │   │   │   └── ErrorResponse.java
│   │   │   ├── repository/              # Data Access Layer
│   │   │   │   ├── UserRepository.java
│   │   │   │   └── TaskRepository.java
│   │   │   ├── security/                # Security Components
│   │   │   │   ├── JwtUtil.java
│   │   │   │   ├── JwtAuthenticationFilter.java
│   │   │   │   ├── CustomUserDetails.java
│   │   │   │   └── CustomUserDetailsService.java
│   │   │   ├── service/                 # Business Logic
│   │   │   │   ├── AuthService.java
│   │   │   │   ├── UserService.java
│   │   │   │   └── TaskService.java
│   │   │   └── JwtAuthApplication.java  # Main class
│   │   └── resources/
│   │       ├── application.properties
│   │       └── application-prod.properties
│   └── test/                            # Test classes
├── .env.example                         # Environment variables template
├── .gitignore
├── pom.xml
└── README.md
```

---

## 🧪 Testing

### Using Postman

1. **Import the Postman collection**
    - Download the collection JSON from the repository
    - Import into Postman

2. **Set up environment variables**
    - `baseUrl`: `http://localhost:8080`
    - `jwt_token`: (auto-saved after login)
    - `admin_token`: (auto-saved after admin login)
    - `user_token`: (auto-saved after user login)

3. **Test workflow**
    - Login as user → Token auto-saved
    - Create tasks → Test CRUD operations
    - Login as admin → Test user management
    - Test security scenarios (401, 403 errors)

### Manual Testing with cURL

**Login:**
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"user","password":"user123"}'
```

**Create Task:**
```bash
curl -X POST http://localhost:8080/api/user/tasks \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN_HERE" \
  -d '{
    "title":"Test Task",
    "description":"Testing API",
    "priority":"HIGH"
  }'
```

**Get Tasks:**
```bash
curl -X GET http://localhost:8080/api/user/tasks \
  -H "Authorization: Bearer YOUR_TOKEN_HERE"
```

### Unit Testing

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=UserServiceTest

# Generate coverage report
mvn test jacoco:report
```

---

## 🔒 Security Best Practices

### Implemented Security Features

1. **Password Security**
    - ✅ BCrypt hashing (cost factor: 10)
    - ✅ Minimum 6 characters requirement
    - ✅ Password never exposed in API responses

2. **JWT Token Security**
    - ✅ HS512 algorithm (strong encryption)
    - ✅ Token expiration (24 hours)
    - ✅ Signature verification on every request
    - ✅ Stateless authentication

3. **Input Validation**
    - ✅ Bean Validation (JSR-380)
    - ✅ Email format validation
    - ✅ Username pattern validation (alphanumeric + underscore)
    - ✅ Length constraints on all fields

4. **Error Handling**
    - ✅ No stack traces in production
    - ✅ Consistent error response format
    - ✅ No sensitive information leakage

5. **Role-Based Access Control**
    - ✅ Method-level security (@PreAuthorize)
    - ✅ URL-based authorization
    - ✅ Principle of least privilege

### Production Recommendations

⚠️ **Before deploying to production:**

1. **Change Default Credentials**
   ```java
   // Remove or change in DataInitializer.java
   ```

2. **Use Strong JWT Secret**
   ```bash
   # Generate random secret (Linux/Mac)
   openssl rand -base64 64
   
   # Use this in .env
   JWT_SECRET=<generated_secret>
   ```

3. **Use Production Database**
    - Replace H2 with PostgreSQL/MySQL
    - Update `application-prod.properties`

4. **Enable HTTPS**
   ```properties
   server.ssl.enabled=true
   server.ssl.key-store=classpath:keystore.p12
   server.ssl.key-store-password=${SSL_PASSWORD}
   ```

5. **Implement Rate Limiting**
   ```xml
   <!-- Add dependency -->
   <dependency>
       <groupId>com.github.vladimir-bukhtoyarov</groupId>
       <artifactId>bucket4j-core</artifactId>
   </dependency>
   ```

6. **Enable CORS Properly**
   ```java
   @Configuration
   public class CorsConfig implements WebMvcConfigurer {
       @Override
       public void addCorsMappings(CorsRegistry registry) {
           registry.addMapping("/api/**")
                   .allowedOrigins("https://yourdomain.com")
                   .allowedMethods("GET", "POST", "PUT", "DELETE");
       }
   }
   ```

7. **Use Refresh Tokens**
    - Implement refresh token mechanism
    - Short-lived access tokens (15 min)
    - Long-lived refresh tokens (7 days)

8. **Add API Documentation**
   ```xml
   <!-- Swagger/OpenAPI -->
   <dependency>
       <groupId>org.springdoc</groupId>
       <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
   </dependency>
   ```

---

## 🤝 Contributing

Contributions are welcome! Please follow these steps:

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

### Coding Standards

- Follow Java naming conventions
- Write meaningful commit messages
- Add JavaDoc comments for public methods
- Include unit tests for new features
- Update README.md for API changes

---

## 🙏 Acknowledgments

- Spring Boot Documentation
- JWT.io for token debugging
- Baeldung Spring Security tutorials
- Stack Overflow community

---

## 🗺️ Roadmap

- [ ] Implement refresh tokens
- [ ] Add email verification
- [ ] Implement forgot password functionality
- [ ] Add pagination and sorting
- [ ] Create API versioning
- [ ] Add Swagger/OpenAPI documentation
- [ ] Implement rate limiting
- [ ] Add integration tests
- [ ] Create Docker containerization
- [ ] Add CI/CD pipeline
- [ ] Implement logging with ELK stack
- [ ] Add performance monitoring

---

**Made with ❤️ using Spring Boot**