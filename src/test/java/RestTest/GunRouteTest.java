package RestTest;

import io.javalin.http.ContentType;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class GunRouteTest {

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
    public void testReadAllGuns() {
        given()
                .header("Authorization", "Bearer " + authToken)
                .when()
                .get("/guns")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(0));
    }

    @Test
    public void testReadGunById() {
        given()
                .header("Authorization", "Bearer " + authToken)
                .pathParam("id", 2)
                .when()
                .get("/guns/{id}")
                .then()
                .statusCode(200)
                .body("id", equalTo(2));
    }

    @Test
    public void testCreateGun() {
        String gunJson = """
            {
                "name": "AK-47",
                "teamId":  false,
                "gameId": 2
            }
            """;

        given()
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON)
                .body(gunJson)
                .when()
                .post("/guns")
                .then()
                .statusCode(201)
                .body("name", equalTo("AK-47"))
                .body("teamId", equalTo(false));
    }

    @Test
    public void testUpdateGun() {
        String updateJson = """
            {
                "name": "M4A1-S",
                "teamId":  false,
                "gameId": 2
            }
            """;

        given()
                .header("Authorization", "Bearer " + authToken)
                .contentType(ContentType.JSON)
                .pathParam("id", 3)
                .body(updateJson)
                .when()
                .put("/guns/{id}")
                .then()
                .statusCode(200)
                .body("name", equalTo("M4A1-S"))
                .body("teamId", equalTo(false));
    }

    @Test
    public void testDeleteGun() {
        given()
                .header("Authorization", "Bearer " + authToken)
                .pathParam("id", 1)
                .when()
                .delete("/guns/{id}")
                .then()
                .statusCode(204);
    }

    @Test
    public void testGetRandomGunByGame() {
        given()
                .header("Authorization", "Bearer " + authToken)
                .pathParam("gameId", 2)
                .when()
                .get("/guns/random/game/{gameId}")
                .then()
                .statusCode(200)
                .body("id", notNullValue());
    }
}