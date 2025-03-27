package dat.routes;

import dat.controllers.impl.GunController;
import dat.entities.enums.Role;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class GunRoute {

    private final GunController gunController = new GunController();

    public EndpointGroup getRoutes() {
        return () -> {
            get("/", gunController::readAll);
            get("/{id}", gunController::read);
            post("/", gunController::create, Role.ADMIN);
            put("/{id}", gunController::update, Role.ADMIN);
            delete("/{id}", gunController::delete, Role.ADMIN);
            get("/random/game/{gameId}", gunController::getRandomByGame);
        };
    }
}