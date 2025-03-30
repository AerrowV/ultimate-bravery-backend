package dat.controllers.security;

import dk.bugelhartmann.UserDTO;
import io.javalin.http.Handler;
import io.javalin.security.RouteRole;

import java.util.Set;

/**
 * Purpose: To handle security in the API
 * Author: Thomas Hartmann
 */
public interface ISecurityController {
    Handler login();

    Handler register();

    Handler authenticate();

    boolean authorize(UserDTO userDTO, Set<RouteRole> allowedRoles);

    String createToken(UserDTO user) throws Exception;

    UserDTO verifyToken(String token) throws Exception;
}

