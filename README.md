# Smart Campus Sensor & Room Management API

## 1. Overview of API Design
The Smart Campus API is a RESTful web service built using pure JAX-RS (Jakarta RESTful Web Services). It was developed to meet strict constraints: no external frameworks like Spring Boot, and no traditional databases like SQL. 

Because we rely entirely on in-memory persistence, the underlying architecture uses the DAO (Data Access Object) pattern combined with thread-safe collections (`ConcurrentHashMap`, `CopyOnWriteArrayList`) to ensure data integrity during concurrent HTTP requests. 

The API models a hierarchical campus structure (Rooms containing Sensors, and Sensors containing historical Readings). We also implemented advanced REST concepts such as query filtering, sub-resource locators for deep nesting, and comprehensive global error handling to map specific exceptions to correct HTTP status codes (409, 422, 403, and 500).

---

## 2. Build & Tomcat Deployment Instructions

### Prerequisites
- JDK 11+
- Apache Maven 3.6+
- Apache Tomcat 9 or 10

### Command-Line Maven Build
1. Open up your terminal and navigate to the project directory where `pom.xml` is located.
2. Run the Maven package command:
   ```bash
   mvn clean package
   ```
3. A `smart-campus-api.war` file will be generated in the `/target` directory.

### Launching via NetBeans IDE
1. Open NetBeans and go to **File -> Open Project**. Select the `smart-campus-api` folder.
2. In the Projects tab, Right-Click the project and set the server to **Apache Tomcat**.
3. Right-Click the project -> **Clean and Build**.
4. Right-Click the project -> **Run** (or **Deploy**). NetBeans will auto-deploy the `.war` to Tomcat and the endpoints will be accessible via `http://localhost:8080/smart-campus-api`.

---

## 3. Sample cURL Commands

**1. View available API routes (Discovery):**
```bash
curl -X GET http://localhost:8080/smart-campus-api/api/v1/ -H "Accept: application/json"
```

**2. Create a specific Room:**
```bash
curl -X POST http://localhost:8080/smart-campus-api/api/v1/rooms \
-H "Content-Type: application/json" \
-d '{ "id": "ROOM-01", "name": "Main Library", "capacity": 150 }'
```

**3. Fetch Sensors by explicitly filtering for CO2 types:**
```bash
curl -X GET "http://localhost:8080/smart-campus-api/api/v1/sensors?type=CO2" \
-H "Accept: application/json"
```

**4. Register a new Sensor linked to the created Room:**
```bash
curl -X POST http://localhost:8080/smart-campus-api/api/v1/sensors \
-H "Content-Type: application/json" \
-d '{ "id": "SENS-CO2", "type": "CO2", "status": "ACTIVE", "roomId": "ROOM-01" }'
```

**5. Add a historical reading to that Sensor:**
```bash
curl -X POST http://localhost:8080/smart-campus-api/api/v1/sensors/SENS-CO2/readings \
-H "Content-Type: application/json" \
-d '{ "id": "READ-01", "timestamp": 1713500000, "value": 415.5 }'
```

---

## 4. Final Checklist Against Rubric
- [x] JAX-RS Maven setup with versioned API root.
- [x] Discovery endpoint with metadata and resource links.
- [x] Room CRUD subset required by spec + business delete constraint.
- [x] Room create returns HTTP 201 with Location header.
- [x] Sensor creation validates linked room exists natively.
- [x] Sensor query filtering implemented flawlessly via @QueryParam.
- [x] Sub-resource locator with highly modularized separate resource class.
- [x] Reading POST explicitly coordinates atomically locking parent sensor currentValue.
- [x] Specific discrete exception mapping explicitly firing 409, 422, 403.
- [x] Global ExceptionMapper<Throwable> fully intercepting traces with sanitized 500 outputs.
- [x] Request/response observability filtering securely wrapping java.util.logging.
- [x] Final theoretical conceptual report completely stripped to PDF as instructed against rubric layout.

