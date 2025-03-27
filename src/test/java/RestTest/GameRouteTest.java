package RestTest;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;



@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GameRouteTest {

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
        public void testReadGameById() {
            given()
                    .header("Authorization", "Bearer " + authToken)
                    .pathParam("id", 2)
                    .when()
                    .get("/games/{id}")
                    .then()
                    .statusCode(200)
                    .body("id", equalTo(2));
        }

    @Test
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
        public void testDeleteGame() {
            given()
                    .header("Authorization", "Bearer " + authToken)
                    .pathParam("id", 1)
                    .when()
                    .delete("/games/{id}")
                    .then()
                    .statusCode(204);  // No Content
        }

        @Test
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
    }