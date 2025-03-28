package RestTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class MapRouteTest {

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
    public void testCreateMap() {
        String token = given()
                .contentType(ContentType.JSON)
                .body("{\"username\":\"admin\", \"password\":\"admin123\"}")
                .post("/auth/login")
                .then()
                .extract().path("token");

        String mapJson = """
            {
                "name": "Dust II",
                "game_id": "2"
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
    public void testUpdateMap() {
        String updateJson = """
            {
                "name": "Inferno",
                "gameId": 2
            }
            """;

        given()
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON)
                .pathParam("id", 4)
                .body(updateJson)
                .when()
                .put("/maps/{id}")
                .then()
                .statusCode(200)
                .body("name", equalTo("Inferno"))
                .body("gameId", equalTo("2"));
    }

    @Test
    public void testDeleteMap() {
        given()
                .header("Authorization", "Bearer " + authToken)
                .pathParam("id", 1)
                .when()
                .delete("/maps/{id}")
                .then()
                .statusCode(204);
    }
}