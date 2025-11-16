Neo4Chat

## Getting Started

### Prerequisites
- **Java**: Install Java 21 (or a compatible JDK). Verify with:

```bash
java -version
```

- **Git**: Install Git and verify with:

```bash
git --version
```

You do **not** need Maven installed globally because the project uses the Maven Wrapper (`mvnw`).

### 1. Clone the repository

```bash
git clone https://github.com/anhadh3101/neo4chat.git
```

### 2. Navigate to the Spring Boot app

The Spring Boot application lives in the `demo` directory:

```bash
cd demo
```

### 3. Run the application (dev mode)

Using the Maven Wrapper:

```bash
./mvnw spring-boot:run
```

On Windows (PowerShell or cmd):

```bash
mvnw.cmd spring-boot:run
```

The application will start on `http://localhost:8080`.

### 4. Build a runnable JAR (optional)

```bash
./mvnw clean package
```

Then run the packaged JAR:

```bash
java -jar target/demo-0.0.1-SNAPSHOT.jar
```

### 5. Test the application

Once the app is running, you can hit these endpoints in a browser or with `curl`:

- `http://localhost:8080/` – basic health check
- `http://localhost:8080/hello`
- `http://localhost:8080/api/status`
