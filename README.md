# Rideshare Application

A Spring Boot-based rideshare application that enables users to request rides and drivers to accept and complete them. The application features JWT-based authentication, role-based access control, and MongoDB for data persistence.

## 🚀 Features

- **User Authentication**: Secure registration and login with JWT tokens
- **Role-Based Access Control**: Two user roles - `ROLE_USER` (passengers) and `ROLE_DRIVER` (drivers)
- **Ride Management**: 
  - Users can request rides with pickup and drop locations
  - Drivers can view pending ride requests and accept them
  - Rides can be completed by either drivers or users
- **Ride Status Tracking**: Three statuses - `REQUESTED`, `ACCEPTED`, `COMPLETED`
- **Secure API**: All endpoints are protected with JWT authentication
- **MongoDB Integration**: NoSQL database for flexible data storage

## 🛠️ Technologies Used

- **Java 17**
- **Spring Boot 3.2.0**
- **Spring Security** - Authentication and authorization
- **Spring Data MongoDB** - Database integration
- **JWT (JJWT 0.11.5)** - Token-based authentication
- **Maven** - Dependency management
- **MongoDB** - NoSQL database

## 📋 Prerequisites

Before running the application, ensure you have:

- Java 17 or higher
- Maven 3.6+ 
- MongoDB installed and running (default: `localhost:27017`)
- Git (for cloning the repository)

## 🔧 Installation & Setup

### 1. Clone the Repository

```bash
git clone git@github.com:LakshyaBagani/SpringBoot-Project.git
cd SpringBoot-Project/demo
```

### 2. Configure MongoDB

Update the MongoDB connection string in `src/main/resources/application.yaml`:

```yaml
spring:
  data:
    mongodb:
      uri: mongodb://admin:admin@localhost:27017/smart_storage?authSource=admin
```

**Note**: Update the connection string with your MongoDB credentials and database name.

### 3. Build the Project

```bash
mvn clean install
```

### 4. Run the Application

```bash
mvn spring-boot:run
```

Or run the JAR file:

```bash
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

The application will start on `http://localhost:8080` by default.

## 📁 Project Structure

```
demo/
├── src/
│   ├── main/
│   │   ├── java/org/example/rideshare/
│   │   │   ├── config/              # Configuration classes
│   │   │   │   ├── MongoConnectionLogger.java
│   │   │   │   ├── PasswordConfig.java
│   │   │   │   └── SecurityConfig.java
│   │   │   ├── controller/          # REST Controllers
│   │   │   ├── api/v1/
│   │   │   │   ├── driver/          # Driver endpoints
│   │   │   │   ├── rides/           # Ride endpoints
│   │   │   │   └── user/            # User endpoints
│   │   │   └── AuthController.java  # Authentication endpoints
│   │   │   ├── dto/                 # Data Transfer Objects
│   │   │   ├── exception/           # Exception handling
│   │   │   ├── model/               # Entity models
│   │   │   ├── repository/          # MongoDB repositories
│   │   │   ├── security/            # JWT and security
│   │   │   └── service/             # Business logic
│   │   └── resources/
│   │       ├── application.yaml     # Application configuration
│   │       └── application.properties
│   └── test/                        # Test files
└── pom.xml                          # Maven dependencies
```

## 🔐 API Endpoints

### Authentication Endpoints

#### Register User
```http
POST /api/auth/register
Content-Type: application/json

{
  "username": "john_doe",
  "password": "password123",
  "role": "ROLE_USER"  // or "ROLE_DRIVER"
}
```

#### Login
```http
POST /api/auth/login
Content-Type: application/json

{
  "username": "john_doe",
  "password": "password123"
}
```

**Response:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "username": "john_doe",
  "role": "ROLE_USER"
}
```

### Ride Endpoints

#### Request a Ride (User)
```http
POST /api/v1/rides
Authorization: Bearer <JWT_TOKEN>
Content-Type: application/json

{
  "pickupLocation": "123 Main St, City",
  "dropLocation": "456 Oak Ave, City"
}
```

#### Complete a Ride
```http
POST /api/v1/rides/{rideId}/complete
Authorization: Bearer <JWT_TOKEN>
```

### User Endpoints (Requires ROLE_USER)

#### Get My Rides
```http
GET /api/v1/user/rides
Authorization: Bearer <JWT_TOKEN>
```

### Driver Endpoints (Requires ROLE_DRIVER)

#### View Pending Ride Requests
```http
GET /api/v1/driver/rides/requests
Authorization: Bearer <JWT_TOKEN>
```

#### Accept a Ride
```http
POST /api/v1/driver/rides/{rideId}/accept
Authorization: Bearer <JWT_TOKEN>
```

## 🔒 Security Features

- **JWT Authentication**: All API endpoints (except `/api/auth/register` and `/api/auth/login`) require a valid JWT token
- **Password Encryption**: Passwords are encrypted using BCrypt
- **Role-Based Authorization**: 
  - `/api/v1/user/*` endpoints require `ROLE_USER`
  - `/api/v1/driver/*` endpoints require `ROLE_DRIVER`
- **Custom JWT Filter**: Validates JWT tokens on each request

## 📝 Usage Example

### 1. Register a User
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "passenger1",
    "password": "password123",
    "role": "ROLE_USER"
  }'
```

### 2. Register a Driver
```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "driver1",
    "password": "password123",
    "role": "ROLE_DRIVER"
  }'
```

### 3. Login
```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "passenger1",
    "password": "password123"
  }'
```

Save the token from the response.

### 4. Request a Ride
```bash
curl -X POST http://localhost:8080/api/v1/rides \
  -H "Authorization: Bearer <YOUR_JWT_TOKEN>" \
  -H "Content-Type: application/json" \
  -d '{
    "pickupLocation": "Airport Terminal 1",
    "dropLocation": "Downtown Hotel"
  }'
```

### 5. Driver Views Pending Rides
```bash
curl -X GET http://localhost:8080/api/v1/driver/rides/requests \
  -H "Authorization: Bearer <DRIVER_JWT_TOKEN>"
```

### 6. Driver Accepts a Ride
```bash
curl -X POST http://localhost:8080/api/v1/driver/rides/{rideId}/accept \
  -H "Authorization: Bearer <DRIVER_JWT_TOKEN>"
```

## 🗄️ Database Schema

### User Collection
```json
{
  "_id": "ObjectId",
  "username": "string",
  "password": "string (BCrypt hashed)",
  "role": "ROLE_USER | ROLE_DRIVER"
}
```

### Ride Collection
```json
{
  "_id": "ObjectId",
  "userId": "string (User ID)",
  "driverId": "string (Driver ID, nullable)",
  "pickupLocation": "string",
  "dropLocation": "string",
  "status": "REQUESTED | ACCEPTED | COMPLETED",
  "createdAt": "Date"
}
```

## 🧪 Testing

Run the test suite:

```bash
mvn test
```

## 📄 License

This project is open source and available for use.

## 👤 Author

**Varshitha sai kolupuri**

## 🤝 Contributing

Contributions, issues, and feature requests are welcome!

## 📞 Support

For support, please open an issue in the GitHub repository.

---

**Note**: Make sure MongoDB is running before starting the application. Update the MongoDB connection string in `application.yaml` according to your MongoDB setup.

