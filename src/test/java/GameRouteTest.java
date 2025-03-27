import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;

public class GameRouteTest {

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://localhost:7070";  // Din server URL
    }

    @Test
    public void testGetAllGames() {
        given()
                .when()
                .get("api/games")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(0));
    }

    @Test
    public void testGetGameById() {
        given()
                .pathParam("id", 1)
                .when()
                .get("api/games/{id}")
                .then()
                .statusCode(200)
                .body("id", equalTo(1));
    }


    @Test
    public void testGetGamesByMap() {
        given()
                .pathParam("mapId", 1)
                .when()
                .get("api/games/map/{mapId}")
                .then()
                .statusCode(200)
                .body("size()", greaterThanOrEqualTo(0));
    }
}