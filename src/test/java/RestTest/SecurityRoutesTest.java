package RestTest;

import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.equalTo;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class SecurityRoutesTest {

    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = "http://localhost:7070/api";
    }

    @Test
    public void testHealthCheck() {
        given()
                .when()
                .get("/auth/healthcheck")
                .then()
                .statusCode(200);
    }


    @Test
    public void testRegister() {
        String registerJson = """
            {
                "username": "testuser",
                "password": "test123",
                "email": "test@example.com"
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
    public void testAdminEndpointWithoutAuth() {
        given()
                .when()
                .get("/auth/test")
                .then()
                .statusCode(401);  // Unauthorized
    }

    @Test
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
                "role": "MODERATOR"
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
}