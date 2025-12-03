# Neo4Chat

## Overview

Neo4Chat is a full-stack social networking application built with:

- **Frontend**: React + Vite with Tailwind CSS and shadcn/ui components
- **Backend**: Spring Boot REST API
- **Database**: Neo4j (Aura cloud or local instance)

The application connects to a shared **Neo4j Aura** cloud database instance.

---

## Quick Start (Neo4j Aura - Recommended)

### 1. Prerequisites

- **Java 21+**
- **Node.js 18+** and **npm** (for frontend)
- **Git**
- **Maven** (or use the included `mvnw` wrapper)
- Access to the shared **Neo4j Aura** database credentials

Verify installations:

```bash
java --version
node --version
npm --version
```

### 2. Clone the repository

```bash
git clone https://github.com/anhadh3101/neo4chat.git
cd neo4chat
```

### 3. Set up environment variables

The backend application requires three environment variables to connect to Neo4j Aura. These are defined in `backend/src/main/resources/application.properties`:

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

### 4. Install frontend dependencies

Navigate to the `frontend` directory and install dependencies:

```bash
cd frontend
npm install
```

### 5. Start the backend

Open a new terminal window and navigate to the `backend` directory:

```bash
cd backend
./mvnw spring-boot:run
```

Or if you have Maven installed globally:

```bash
cd backend
mvn spring-boot:run
```

The backend API will start on `http://localhost:8080` and automatically connect to the shared Neo4j Aura database.

**Note**: Keep this terminal window open while the backend is running.

### 6. Start the frontend

In your original terminal (or a new one), navigate to the `frontend` directory and start the development server:

```bash
cd frontend
npm run dev
```

The frontend will start on `http://localhost:5173` (Vite's default port).

### 7. Access the application

Once both servers are running:

- **Frontend**: Open `http://localhost:5173` in your browser
- **Backend API**: Available at `http://localhost:8080`

You can now use the Neo4Chat application! The frontend will communicate with the backend API.

### 8. Verify the setup

You can test the backend endpoints:

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

- Build the Spring Boot image from the `backend` directory
- Start the **Spring Boot app** container on port **8080**
- Start a local **Neo4j** container on ports **7474** (Browser) and **7687** (Bolt)

4. Start the frontend (in a separate terminal):

```bash
cd frontend
npm install
npm run dev
```

5. Access the services:

- Frontend: `http://localhost:5173`
- Spring Boot API: `http://localhost:8080`
- Neo4j Browser: `http://localhost:7474`

6. Stop the stack:

```bash
docker compose down
```

---

## Project Structure

```
neo4chat/
├── frontend/          # React + Vite frontend application
│   ├── src/
│   │   ├── components/   # React components (Header, Layout, UI components)
│   │   ├── pages/        # Page components (Home, Explore, Profile)
│   │   └── lib/          # Utilities (API client, user storage)
│   └── package.json
├── backend/          # Spring Boot backend API
│   ├── src/main/java/com/example/
│   │   ├── controllers/  # REST API controllers
│   │   ├── service/      # Business logic services
│   │   └── config/       # Configuration (CORS, Neo4j, Security)
│   └── pom.xml
└── README.md
```

## Troubleshooting

### Frontend Issues

If the frontend can't connect to the backend:

1. **Check that the backend is running** on `http://localhost:8080`
2. **Verify CORS configuration** - The backend should allow requests from `http://localhost:5173`
3. **Check browser console** for any CORS or network errors
4. **Verify API base URL** - The frontend uses `http://localhost:8080/api` by default (configurable via `VITE_API_BASE_URL` environment variable)

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
