# UltimateBravery – REST API

**UltimateBravery** is a RESTful API built with Java, Javalin, and JPA that delivers randomized game strategies for competitive shooters like CS2 (Counter-Strike 2).  
It was developed as part of a semester project focusing on software design, security, and deployment best practices.

---

**Frontend Repository**:  
[github.com/AerrowV/ultimate-bravery-frontend](https://github.com/AerrowV/ultimate-bravery-frontend)

**Live Demo**:  
[https://ultimatebravery.yumiya.dk](https://ultimatebravery.yumiya.dk)

## Features

- Get randomized "Ultimate Bravery"-style strategies for selected games and maps
- Categorize strategies by type: serious, average, or troll
- JWT-secured endpoints for user login and registration
- Role-based access (USER / ADMIN)
- Manage games, maps, guns, strategies, and more via RESTful routes
- Integration and unit testing using JUnit, Testcontainers, and RestAssured
- Dockerized for easy deployment (CI/CD with GitHub Actions + DigitalOcean)
- HTTPS enabled with Caddy

---

## Tech Stack

- **Java 17**
- **Javalin** (REST API Framework)
- **Hibernate / JPA**
- **PostgreSQL** (via Testcontainers for testing)
- **JWT** for authentication
- **Maven** for build management
- **Docker** for deployment
- **GitHub Actions** for CI/CD


## Running the API Locally

1. **Clone the repo**:
   ```bash
   git clone https://github.com/AerrowV/ultimate-bravery-backend.git
   cd ultimate-bravery-backend 
   ```

2. **Build the project**:
   ```bash
   mvn clean install
   ```

3. **Run the application**:
   ```bash
   mvn exec:java
   ```

4. **Access the API**:
   Navigate to `http://localhost:7070/api`
   
## Authentication

Use the following endpoints to register or log in and receive a JWT token:

- `POST /auth/register` – Create a new user
- `POST /auth/login` – Log in and receive a token

Include the token in the `Authorization` header for all protected routes:
```
Authorization: Bearer <your_token_here>
```

### Roles

- `USER`: Can view and request random strategies
- `ADMIN`: Can create, update, and delete strategies, games, maps, and more

## Example Endpoints

| Method | Endpoint             | Description                      |
|--------|----------------------|----------------------------------|
| GET    | `/api/strategies`    | Get all strategies               |
| POST   | `/api/strategies`    | Create new strategy (admin only)|
| GET    | `/api/maps`          | Get all maps                     |
| GET    | `/api/games`         | Get all supported games          |
| POST   | `/auth/login`        | Login and receive JWT            |

## Testing

Tests are written with JUnit 5, RestAssured, and Testcontainers.

Run tests with:

```bash
mvn test
```
## Deployment

The API is deployed using:

- GitHub Actions (CI/CD pipeline)
- Docker + DigitalOcean Droplet
- Caddy for reverse proxy & HTTPS

---
