package RestTest;

import dat.config.ApplicationConfig;
import dat.config.HibernateConfig;
import dat.config.Populate;
import io.javalin.Javalin;
import io.javalin.http.ContentType;
import io.restassured.RestAssured;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GunRouteTest {

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
    @Order(2)
    public void testReadGunById() {
        given()
                .header("Authorization", "Bearer " + authToken)
                .pathParam("id", 1)
                .when()
                .get("/guns/{id}")
                .then()
                .statusCode(200)
                .body("id", equalTo(1));
    }

    @Test
    @Order(3)
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
    @Order(4)
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
                .pathParam("id", 2)
                .body(updateJson)
                .when()
                .put("/guns/{id}")
                .then()
                .statusCode(200)
                .body("name", equalTo("M4A1-S"))
                .body("teamId", equalTo(false));
    }

    @Test
    @Order(6)
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
    @Order(5)
    public void testGetRandomGunByGame() {
        given()
                .header("Authorization", "Bearer " + authToken)
                .pathParam("gameId", 1)
                .when()
                .get("/guns/random/game/{gameId}")
                .then()
                .statusCode(200)
                .body("id", notNullValue());
    }

    @AfterAll
    public void tearDown() {
        ApplicationConfig.stopServer(app);
    }
}