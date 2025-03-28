package RestTest;

import dat.config.ApplicationConfig;
import dat.config.HibernateConfig;
import dat.config.Populate;
import io.javalin.Javalin;
import io.javalin.http.ContentType;
import io.restassured.RestAssured;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StrategyRouteTest {

    private Javalin app;
    private static String authToken;

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
    public void testReadAllStrategies() {
        given()
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/strategies")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(0));
    }

    @Test
    @Order(2)
    public void testReadStrategyById() {
        given()
                .header("Authorization", "Bearer " + authToken)
                .pathParam("id", 2)
                .when()
                .get("/strategies/{id}")
                .then()
                .statusCode(200)
                .body("id", equalTo(2));
    }

    @Test
    @Order(3)
    public void testCreateStrategy() {
        Long mapId = given()
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON)
                .body("{\"name\":\"Test Map\"}")
                .post("/maps")
                .then()
                .extract().path("id");

        String strategyJson = """
                {
                    "title": "Rush B",
                    "type": "SERIOUS",
                    "teamId": false,
                    "description": "din far",
                    "mapIds": [%d]
                }
                """.formatted(mapId);

        given()
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON)
                .body(strategyJson)
                .when()
                .post("/strategies")
                .then()
                .log().all()
                .statusCode(201)
                .body("title", equalTo("Rush B"));
    }

    @Test
    @Order(4)
    public void testUpdateStrategy() {
        Long gameId = given()
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON)
                .body("{\"name\":\"Test Game\"}")
                .post("/games")
                .then()
                .statusCode(201)
                .extract().jsonPath().getLong("id");

        Long mapId = given()
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "name": "Test Map",
                            "gameId": %d,
                            "strategyIds": []
                        
                        }
                        """.formatted(gameId))
                .post("/maps")
                .then()
                .statusCode(201)
                .extract().jsonPath().getLong("id");

        Long strategyId = given()
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON)
                .body("""
                        {
                            "title": "Initial Strategy",
                            "type": "SERIOUS",
                            "teamId": false,
                            "description": "Initial",
                            "mapIds": [%d]
                        }
                        """.formatted(mapId))
                .post("/strategies")
                .then()
                .statusCode(201)
                .extract().jsonPath().getLong("id");

        String updateJson = """
                {
                    "title": "Slow Push",
                    "type": "SERIOUS",
                    "teamId": true,
                    "description": "Opdateret beskrivelse",
                    "mapIds": [%d]
                }
                """.formatted(mapId);

        given()
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON)
                .pathParam("id", strategyId)
                .body(updateJson)
                .when()
                .put("/strategies/{id}")
                .then()
                .log().all()
                .statusCode(200)
                .body("title", equalTo("Slow Push"))
                .body("type", equalTo("SERIOUS"))
                .body("teamId", equalTo(true));
    }

    @Test
    @Order(7)
    public void testDeleteStrategy() {
        given()
                .header("Authorization", "Bearer " + authToken)
                .pathParam("id", 1)
                .when()
                .delete("/strategies/{id}")
                .then()
                .statusCode(204);
    }

    @Test
    @Order(5)
    public void testGetRandomStrategyByMapAndType() {
        given()
                .header("Authorization", "Bearer " + authToken)
                .pathParam("mapId", 1)
                .queryParam("type", "SERIOUS")
                .when()
                .get("/strategies/random/map/{mapId}")
                .then()
                .statusCode(200)
                .body("id", notNullValue());
    }

    @Test
    @Order(6)
    public void testGetStrategiesByMapId() {
        given()
                .header("Authorization", "Bearer " + authToken)
                .pathParam("mapId", 1)
                .when()
                .get("/strategies/map/{mapId}")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(0));
    }

    @AfterAll
    public void tearDown() {
        ApplicationConfig.stopServer(app);
    }
}