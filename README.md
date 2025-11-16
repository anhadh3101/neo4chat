# Neo4Chat

## Overview

Neo4Chat is a Spring Boot + Neo4j application.  
The recommended way to run it is with **Docker Compose** so you and your team don’t need local Java/Maven/Neo4j installed.

---

## Run with Docker Compose (recommended)

### 1. Prerequisites
- **Docker** and **Docker Compose**
- **Git**

Verify they’re installed:

```bash
docker --version
docker compose version
git --version
```

### 2. Clone the repository

```bash
git clone https://github.com/anhadh3101/neo4chat.git
cd neo4chat
```

### 3. Start the stack (Spring Boot + Neo4j)

From the project root (where `docker-compose.yml` is located), run:

```bash
docker compose up --build
```

This will:
- Build the Spring Boot image from the `demo` directory using its `Dockerfile`
- Start the **Spring Boot app** container (`springboot`) on port **8080**
- Start **Neo4j** on:
  - `7474` (Neo4j Browser)
  - `7687` (Bolt protocol)

### 4. Access the services

- Spring Boot API: `http://localhost:8080`
- Neo4j Browser: `http://localhost:7474`

### 5. Test the application

With the stack running, you can hit these endpoints in a browser or with `curl`:

- `http://localhost:8080/` – basic health check
- `http://localhost:8080/hello`
- `http://localhost:8080/api/status`

Example using `curl`:

```bash
curl http://localhost:8080/
curl http://localhost:8080/hello
curl http://localhost:8080/api/status
```

### 6. Stop the stack

From the project root:

```bash
docker compose down
```

This stops and removes the containers (images and volumes remain unless you add extra flags).

---

## (Optional) Run the Spring Boot app locally

If you want to run the app without Docker (for local development):

### Prerequisites
- **Java 21+**
- **Git**

### Steps

```bash
git clone https://github.com/anhadh3101/neo4chat.git
cd neo4chat/demo

./mvnw spring-boot:run
```

The app will start on `http://localhost:8080`.  
Neo4j must be running separately if the app needs to connect to it.
