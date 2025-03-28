package RestTest;

import dat.config.ApplicationConfig;
import dat.config.HibernateConfig;
import dat.config.Populate;
import io.javalin.Javalin;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.*;

import static io.restassured.RestAssured.given;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SecurityRoutesTest {

    private Javalin app;

    @BeforeAll
    public void setup() {
        HibernateConfig.setTest(true);
        app = ApplicationConfig.startServer(7070);
        Populate.populate(HibernateConfig.getEntityManagerFactoryForTest());
        RestAssured.baseURI = "http://localhost:7070/api";
    }

    @Test
    @Order(1)
    public void testHealthCheck() {
        given()
                .when()
                .get("/auth/healthcheck")
                .then()
                .statusCode(200);
    }


    @Test
    @Order(2)
    public void testRegister() {
        String registerJson = """
                {
                    "username": "testuser2",
                    "password": "test1234"
                }
                """;

        given()
                .contentType(ContentType.JSON)
                .body(registerJson)
                .when()
                .post("/auth/register")
                .then()
                .statusCode(201);
    }

    @Test
    @Order(3)
    public void testAdminEndpointWithoutAuth() {
        given()
                .when()
                .get("/auth/test")
                .then()
                .statusCode(401);  // Unauthorized
    }

    @Test
    @Order(4)
    public void testAddRole() {
        String token = given()
                .contentType(ContentType.JSON)
                .body("{\"username\":\"admin\", \"password\":\"admin123\"}")
                .post("/auth/login")
                .then()
                .extract().path("token");

        String addRoleJson = """
                {
                    "username": "testuser",
                    "role": "ADMIN"
                }
                """;

        given()
                .header("Authorization", "Bearer " + token)
                .contentType(ContentType.JSON)
                .body(addRoleJson)
                .when()
                .post("/auth/user/addrole")
                .then()
                .statusCode(200);
    }

    @AfterAll
    public void tearDown() {
        ApplicationConfig.stopServer(app);
    }
}