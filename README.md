# Neo4Chat

## Overview

Neo4Chat is a Spring Boot + Neo4j application that connects to a shared **Neo4j Aura** cloud database instance.

---

## Quick Start (Neo4j Aura - Recommended)

### 1. Prerequisites
- **Java 21+**
- **Git**
- **Maven** (or use the included `mvnw` wrapper)
- Access to the shared **Neo4j Aura** database credentials

Verify Java is installed:

```bash
java --version
```

### 2. Clone the repository

```bash
git clone https://github.com/anhadh3101/neo4chat.git
cd neo4chat
```

### 3. Set up environment variables

The application requires three environment variables to connect to Neo4j Aura. These are defined in `demo/src/main/resources/application.properties`:

- `NEO4J_URI` - The Neo4j Aura connection URI (format: `neo4j+s://xxxxx.databases.neo4j.io`)
- `NEO4J_USERNAME` - Your Neo4j username (typically `neo4j`)
- `NEO4J_PASSWORD` - Your Neo4j Aura password

**Export the environment variables in your terminal:**

```bash
export NEO4J_URI=neo4j+s://YOUR_INSTANCE_ID.databases.neo4j.io
export NEO4J_USERNAME=neo4j
export NEO4J_PASSWORD=YOUR_AURA_PASSWORD
```

**Note:** 
- Replace `YOUR_INSTANCE_ID` with your actual Neo4j Aura instance ID
- Replace `YOUR_AURA_PASSWORD` with your actual Neo4j Aura password
- The connection URI uses `neo4j+s://` for secure SSL/TLS connections (required for Aura)
- These environment variables must be set **before** running the application

**To make environment variables persistent** (optional):

For **macOS/Linux**, add the export commands to your shell profile:
```bash
# For zsh (default on macOS)
echo 'export NEO4J_URI=neo4j+s://YOUR_INSTANCE_ID.databases.neo4j.io' >> ~/.zshrc
echo 'export NEO4J_USERNAME=neo4j' >> ~/.zshrc
echo 'export NEO4J_PASSWORD=YOUR_AURA_PASSWORD' >> ~/.zshrc
source ~/.zshrc

# For bash
echo 'export NEO4J_URI=neo4j+s://YOUR_INSTANCE_ID.databases.neo4j.io' >> ~/.bashrc
echo 'export NEO4J_USERNAME=neo4j' >> ~/.bashrc
echo 'export NEO4J_PASSWORD=YOUR_AURA_PASSWORD' >> ~/.bashrc
source ~/.bashrc
```

### 4. Run the application

Navigate to the `demo` directory and start the application:

```bash
cd neo4chat/demo
./mvnw spring-boot:run
```

Or if you have Maven installed globally:

```bash
cd neo4chat/demo
mvn spring-boot:run
```

The application will start on `http://localhost:8080` and automatically connect to the shared Neo4j Aura database.

### 5. Verify the connection

Once the application is running, you can test the endpoints:

```bash
curl http://localhost:8080/
curl http://localhost:8080/hello
curl http://localhost:8080/api/status
```

Check the application logs to confirm a successful connection to Neo4j Aura.

---

## Alternative: Run with Docker (Local Neo4j)

If you want to run the app with a local Neo4j instance using Docker Compose:

### Prerequisites
- **Docker** and **Docker Compose**
- **Git**

### Steps

1. Clone the repository:
```bash
git clone https://github.com/anhadh3101/neo4chat.git
cd neo4chat
```

2. Set environment variables for local Neo4j:
```bash
export NEO4J_URI=bolt://neo4j:7687
export NEO4J_USERNAME=neo4j
export NEO4J_PASSWORD=neo4j_2025
```

3. Start the stack:
```bash
docker compose up --build
```

This will:
- Build the Spring Boot image from the `demo` directory
- Start the **Spring Boot app** container on port **8080**
- Start a local **Neo4j** container on ports **7474** (Browser) and **7687** (Bolt)

4. Access the services:
- Spring Boot API: `http://localhost:8080`
- Neo4j Browser: `http://localhost:7474`

5. Stop the stack:
```bash
docker compose down
```

---

## Troubleshooting

### Connection Issues

If you're having trouble connecting to Neo4j Aura:

1. **Verify environment variables are set:**
   ```bash
   echo $NEO4J_URI
   echo $NEO4J_USERNAME
   echo $NEO4J_PASSWORD
   ```

2. **Check the connection URI format:**
   - Must start with `neo4j+s://` for Aura (secure connection)
   - Should look like: `neo4j+s://xxxxx.databases.neo4j.io`

3. **Verify credentials:**
   - Ensure you have the correct username and password from your Neo4j Aura dashboard
   - Check that your IP address is whitelisted in Aura (if IP filtering is enabled)

4. **Check application logs:**
   - Look for connection errors or authentication failures in the Spring Boot startup logs

### Environment Variables Not Loading

If the application can't find the environment variables:

- Make sure you've exported them in the **same terminal session** where you're running the application
- Restart your terminal or IDE if you've added them to your shell profile
- Verify the variable names match exactly: `NEO4J_URI`, `NEO4J_USERNAME`, `NEO4J_PASSWORD`
