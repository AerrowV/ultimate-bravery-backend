Thanks! Here's a more accurate and polished README tailored for a REST API project:

---

# 🧠 UltimateBravery - REST API

**UltimateBravery** is a RESTful API built with Java, Javalin, and JPA that delivers randomized game strategies for competitive shooters like CS2 (Counter-Strike 2). It was developed as part of a semester project focusing on software design, security, and deployment best practices.

## 📌 Features

- 🎮 Get randomized "Ultimate Bravery"-style strategies for selected games and maps
- ✅ Categorize strategies by type: serious, average, or troll
- 🔐 JWT-secured endpoints for user login and registration
- 🗺️ Manage games, maps, guns, strategies, and more via RESTful routes
- 🧪 Integration and unit testing using JUnit, Testcontainers, and RestAssured
- 🚀 Dockerized for easy deployment (via GitHub Actions + DigitalOcean)
- 🔐 HTTPS enabled with Caddy

## ⚙️ Tech Stack

- **Java 17**
- **Javalin** (REST API Framework)
- **Hibernate / JPA**
- **PostgreSQL** (via Testcontainers for testing)
- **JWT** for authentication
- **Maven** for build management
- **Docker** for deployment
- **GitHub Actions** for CI/CD


## 🚀 Running the API Locally

1. **Clone the repo**:
   ```bash
   git clone https://github.com/AerrowV/SP2_UltimateBravery.git
   cd SP2_UltimateBravery
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

## 🔒 Authentication

Use `/auth/register` and `/auth/login` to get a JWT token. Add it to your headers for protected routes:

```
Authorization: Bearer <your_token_here>
```

## 📬 Example Endpoints

| Method | Endpoint             | Description                      |
|--------|----------------------|----------------------------------|
| GET    | `/api/strategies`    | Get all strategies               |
| POST   | `/api/strategies`    | Create new strategy (admin only)|
| GET    | `/api/maps`          | Get all maps                     |
| GET    | `/api/games`         | Get all supported games          |
| POST   | `/auth/login`        | Login and receive JWT            |

## 🧪 Testing

Tests are written with JUnit 5, RestAssured, and Testcontainers.

Run tests with:

```bash
mvn test
```

## 🐳 Docker

Build and run the app via Docker:

```bash
docker build -t ultimatebravery-api .
docker run -p 7070:7070 ultimatebravery-api
```

## 🌍 Deployment

The API is deployed using:

- GitHub Actions (CI/CD pipeline)
- Docker + DigitalOcean Droplet
- Caddy for reverse proxy & HTTPS

## 🤝 Contributing

Pull requests are welcome! Open an issue first to discuss major changes.

## 📄 License

This project is licensed under the MIT License.

---

Let me know if you want to add a Postman collection, API docs (like Swagger), or update with your real endpoint list and payload examples!
