package dat.controllers.security;

import dat.entities.enums.Role;
import dk.bugelhartmann.UserDTO;
import io.javalin.http.Context;
import io.javalin.http.UnauthorizedResponse;
import io.javalin.security.RouteRole;

import java.util.Set;

public class AccessController implements IAccessController {

    SecurityController securityController = SecurityController.getInstance();

    public void accessHandler(Context ctx) {

        if (ctx.routeRoles().isEmpty() || ctx.routeRoles().contains(Role.ANYONE)) {
            return;
        }

        try {
            securityController.authenticate().handle(ctx);
        } catch (UnauthorizedResponse e) {
            throw new UnauthorizedResponse(e.getMessage());
        } catch (Exception e) {
            throw new UnauthorizedResponse("You need to log in, dude! Or you token is invalid.");
        }

        UserDTO user = ctx.attribute("user");
        Set<RouteRole> allowedRoles = ctx.routeRoles();
        if (!securityController.authorize(user, allowedRoles)) {
            throw new UnauthorizedResponse("Unauthorized with roles: " + user.getRoles() + ". Needed roles are: " + allowedRoles);
        }
    }
}

