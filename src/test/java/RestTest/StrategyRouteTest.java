package RestTest;

import io.javalin.http.ContentType;
import io.restassured.RestAssured;
import io.restassured.path.json.JsonPath;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class StrategyRouteTest {

    private static String authToken;

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://localhost:7070/api";
        authToken = given()
                .contentType(ContentType.JSON)
                .body("{\"username\":\"admin\", \"password\":\"admin123\"}")
                .post("/auth/login")
                .then()
                .extract().path("token");
    }

    @Test
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
    public void testCreateStrategy() {
        // Først opret et map for at få et ID
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
            "mapIds": [%d],
            "strategyId": 2
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
    public void testUpdateStrategy() {
        // 1. Opret Game (hvis nødvendigt for relationer)
        Long gameId = given()
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON)
                .body("{\"name\":\"Test Game\"}")
                .post("/games")
                .then()
                .statusCode(201)
                .extract().jsonPath().getLong("id");

        // 2. Opret Map først
        Long mapId = given()
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON)
                .body("""
            {
                "name": "Test Map",
                "gameId": %d
            }
            """.formatted(gameId))
                .post("/maps")
                .then()
                .statusCode(201)
                .extract().jsonPath().getLong("id");

        // 3. Opret original strategi
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

        // 4. Opdater strategien
        String updateJson = """
        {
            "title": "Slow Push",
            "type": "DEFENSIVE",
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
                .log().all()  // Vigtigt for debugging
                .statusCode(200)
                .body("title", equalTo("Slow Push"))
                .body("type", equalTo("DEFENSIVE"))
                .body("teamId", equalTo(true));
    }

    @Test
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
    public void testGetRandomStrategyByMapAndType() {
        given()
                .header("Authorization", "Bearer " + authToken)
                .pathParam("mapId", 3)
                .queryParam("type", "SERIOUS")
                .when()
                .get("/strategies/random/map/{mapId}")
                .then()
                .statusCode(200)
                .body("id", notNullValue());
    }

    @Test
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
}