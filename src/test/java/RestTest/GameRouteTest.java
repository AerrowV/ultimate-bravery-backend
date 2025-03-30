package RestTest;

import dat.config.ApplicationConfig;
import dat.config.HibernateConfig;
import dat.config.Populate;
import io.javalin.Javalin;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;


@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GameRouteTest {

    private static String authToken;
    private Javalin app;

    @BeforeAll
    public void setup() {
        HibernateConfig.setTest(true);

        app = ApplicationConfig.startServer(7070);

        Populate.populate(HibernateConfig.getEntityManagerFactoryForTest());

        RestAssured.baseURI = "http://localhost:7070/api";

        authToken = given()
                .contentType(ContentType.JSON)
                .body("{\"username\":\"admin\", \"password\":\"admin123\"}")
                .post("/auth/login")
                .then()
                .extract().path("token");
    }

    @Test
    @Order(1)
    public void testReadAllGames() {
        given()
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/games")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(0));
    }

    @Test
    @Order(2)
    public void testReadGameById() {
        given()
                .header("Authorization", "Bearer " + authToken)
                .pathParam("id", 1)
                .when()
                .get("/games/{id}")
                .then()
                .statusCode(200)
                .body("id", equalTo(1));
    }

    @Test
    @Order(3)
    public void testCreateGame() {
        String gameJson = """
                {
                    "name": "Valorant"
                }
                """;

        given()
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON)
                .body(gameJson)
                .when()
                .post("/games")
                .then()
                .statusCode(201)
                .body("name", equalTo("Valorant"));
    }

    @Test
    @Order(4)
    public void testUpdateGame() {
        String updateJson = """
                {
                    "name": "CS2 Updated"
                }
                """;

        given()
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON)
                .pathParam("id", 2)
                .body(updateJson)
                .when()
                .put("/games/{id}")
                .then()
                .statusCode(200)
                .body("name", equalTo("CS2 Updated"));
    }

    @Test
    @Order(7)
    public void testDeleteGame() {
        given()
                .header("Authorization", "Bearer " + authToken)
                .pathParam("id", 1)
                .when()
                .delete("/games/{id}")
                .then()
                .statusCode(204);
    }

    @Test
    @Order(5)
    public void testGetGamesByMap() {
        given()
                .header("Authorization", "Bearer " + authToken)
                .pathParam("mapId", 1)
                .when()
                .get("/games/map/{mapId}")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(0));
    }

    @Test
    @Order(6)
    public void testGetGamesByGun() {
        given()
                .header("Authorization", "Bearer " + authToken)
                .pathParam("gunId", 1)
                .when()
                .get("/games/gun/{gunId}")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(0));
    }

    @AfterAll
    public void tearDown() {
        ApplicationConfig.stopServer(app);
    }
}