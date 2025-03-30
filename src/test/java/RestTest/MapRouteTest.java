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
public class MapRouteTest {

    private static String authToken;
    private Javalin app;

    @BeforeAll
    public void setup() {
        HibernateConfig.setTest(true);

        app = ApplicationConfig.startServer(7070);

        Populate.populate(HibernateConfig.getEntityManagerFactoryForTest());

        RestAssured.baseURI = "http://localhost:7070/api";

        authToken = given()
                .contentType(io.restassured.http.ContentType.JSON)
                .body("{\"username\":\"admin\", \"password\":\"admin123\"}")
                .post("/auth/login")
                .then()
                .extract().path("token");
    }

    @Test
    @Order(1)
    public void testReadAllMaps() {
        given()
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/maps")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(0));
    }

    @Test
    @Order(2)
    public void testReadMapById() {
        given()
                .header("Authorization", "Bearer " + authToken)
                .pathParam("id", 1)
                .when()
                .get("/maps/{id}")
                .then()
                .statusCode(200)
                .body("id", equalTo(1));
    }

    @Test
    @Order(3)
    public void testCreateMap() {
        String mapJson = """
                {
                    "name": "Dust II",
                    "gameId": 1,
                    "strategyIds": [1, 2]
                }
                """;

        given()
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON)
                .body(mapJson)
                .when()
                .post("/maps")
                .then()
                .statusCode(201)
                .body("name", equalTo("Dust II"));
    }

    @Test
    @Order(4)
    public void testUpdateMap() {
        String updateJson = """
                {
                    "name": "Inferno",
                    "gameId": 1,
                    "strategyIds": [2, 3]
                }
                """;

        given()
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON)
                .pathParam("id", 1)
                .body(updateJson)
                .when()
                .put("/maps/{id}")
                .then()
                .statusCode(200)
                .body("name", equalTo("Inferno"));
    }

    @Test
    @Order(5)
    public void testDeleteMap() {
        given()
                .header("Authorization", "Bearer " + authToken)
                .pathParam("id", 1)
                .when()
                .delete("/maps/{id}")
                .then()
                .statusCode(204);
    }

    @AfterAll
    public void tearDown() {
        ApplicationConfig.stopServer(app);
    }
}