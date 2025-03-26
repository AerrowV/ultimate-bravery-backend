package dat.routes;

import com.fasterxml.jackson.databind.ObjectMapper;
import dat.controllers.security.SecurityController;
import dat.entities.Roles;
import dat.utils.Utils;
import io.javalin.apibuilder.EndpointGroup;


import static io.javalin.apibuilder.ApiBuilder.*;
import static io.javalin.apibuilder.ApiBuilder.post;

public class SecurityRoutes {
    private static ObjectMapper jsonMapper = new Utils().getObjectMapper();
    private static SecurityController securityController = SecurityController.getInstance();
    public static EndpointGroup getSecurityRoutes() {
        return ()->{
            path("/auth", ()->{
                get("/healthcheck", securityController::healthCheck);
                get("/test", ctx->ctx.json(jsonMapper.createObjectNode().put("msg",  "Hello from Open Deployment")));
                post("/login", securityController.login());
                post("/register", securityController.register());
                post("/user/addrole", securityController.addRole());
            });
        };
    }
}
