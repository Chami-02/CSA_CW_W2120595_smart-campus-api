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

## 4. Conceptual Questions

### Part 1 Q1: JAX-RS Resource Lifecycle & In-Memory Data
By default, JAX-RS resource classes operate on a **per-request** lifecycle. The framework creates a brand new instance of the class for every incoming HTTP request. This means we cannot store state in instance variables because the data would be wiped immediately after the response is sent. To counter this, any data that needs to persist across requests (like our simulated database) has to be managed in external singletons, such as DAOs. And because multiple requests can hit the server at the exact same time, those DAOs must use thread-safe data structures like `ConcurrentHashMap` to avoid race conditions.

### Part 1 Q2: Hypermedia (HATEOAS)
HATEOAS (Hypermedia As The Engine Of Application State) is an advanced REST principle where the server embeds navigation links directly inside the API responses. It benefits clients immensely because they don't have to hardcode specific URL paths in their frontend logic; instead, they can dynamically discover the API's capabilities by following the links. This decouples the client from the server's specific routing structure, meaning we can change URLs later without breaking the client-side code.

### Part 2 Q1: Returning IDs vs Full Objects in Lists
Returning full objects consumes significantly more network bandwidth and increases the server's serialization workload. However, it saves the client from having to make secondary requests to get missing details it might need for rendering. Returning only IDs saves a lot of bandwidth on the initial fetch, but forces the client to make repetitive $N+1$ requests to get the exact data it actually cares about later on in the process.

### Part 2 Q2: DELETE Idempotency
Yes, our `DELETE` operation is idempotent. Idempotency simply means that making identical requests repeatedly will leave the system in the exact same state as making the request just once. If we delete `ROOM-01`, it succeeds with a `204 No Content`. If we send that exact request again, the server returns a `404 Not Found` because the room is already gone. Even though the HTTP status code changed on the second try, the system state remains fundamentally identical: the room still doesn't exist.

### Part 3 Q1: Consequences of `@Consumes` Format Mismatches
If a client sends an XML payload but our endpoint requires JSON (designated by `@Consumes(MediaType.APPLICATION_JSON)`), JAX-RS intercepts the request before it even reaches our Java methods. The framework realizes the `Content-Type` header doesn't match our routing rules and automatically aborts the request, returning a `415 Unsupported Media Type` error perfectly protecting our logic from having to deal with the wrong formatting.

### Part 3 Q2: Query Parameters vs URL Path Elements
Query parameters (like `?type=CO2`) are typically used to represent optional metadata filtering on an entire collection (like all `/sensors`). URL path elements, on the other hand, identify specific, hard resources in a structural hierarchy. Using query parameters for filtering is much more flexible because they can easily be combined (e.g., `?type=CO2&status=ACTIVE`) without bloating our controllers or creating confusing endpoint routes.

### Part 4 Q1: Benefits of Sub-Resource Locators
Sub-Resource Locators help us follow the Single Responsibility Principle. Instead of forcing all of the specific `/readings` logic into a massive, unreadable `SensorResource` class, we use a locator to dynamically hand off all traffic for `{sensorId}/readings` into a separate, dedicated `SensorReadingResource`. This distributes the API logic, making the codebase substantially easier to read, test, and maintain over time.

### Part 5 Q1: HTTP 422 vs 404 for Missing References
`404 Not Found` implies that the endpoint URL itself doesn't exist. If a client targets a valid endpoint like `POST /sensors`, but the JSON payload contains a `roomId` that doesn't exist in our records, returning a 404 would be incredibly confusing. The client might think their URL has a typo. `422 Unprocessable Entity` is semantically correct because it tells the client: "The syntax of your request was correct, and you hit the right endpoint, but the business logic inside your payload is invalid."

### Part 5 Q2: Security Risks of Exposing Java Stack Traces
Exposing raw Java stack traces breaks the concept of "Security by Obscurity." Stack traces often leak sensitive server information, including directory paths, library versions, and the precise underlying frameworks acting within Tomcat. Attackers can easily take those version numbers and cross-reference them against known CVE databases (Common Vulnerabilities and Exposures) to exploit weaknesses remotely.

### Part 5 Q3: JAX-RS Filters vs Manual Logging
Using a JAX-RS filter guarantees consistent observability without cluttering up our business logic. If we manually added `Logger.info()` to the top of every endpoint method, it would cause excessive code duplication. Furthermore, if a request fails parameter validation and never makes it into the method block, our manual log would be completely bypassed. A global `ContainerRequestFilter` safely traps all connections at the outermost edge of the application lifecycle, regardless of whether the business execution fails gracefully or natively hits a 404.
