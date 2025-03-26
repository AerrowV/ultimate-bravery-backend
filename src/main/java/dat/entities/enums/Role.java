package dat.entities.enums;

import io.javalin.security.RouteRole;

public enum Role implements RouteRole {
    ANYONE, USER, ADMIN;
}

