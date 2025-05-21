package dat.routes;

import dat.controllers.impl.GameController;
import dat.entities.enums.Role;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class GameRoute {

    private final GameController gameController = new GameController();

    public EndpointGroup getRoutes() {
        return () -> {
            get("/populate", gameController::populate);
            get("/", gameController::readAll);
            get("/{id}", gameController::read);
            post("/", gameController::create, Role.ADMIN);
            put("/{id}", gameController::update, Role.ADMIN);
            delete("/{id}", gameController::delete, Role.ADMIN);
            get("/map/{mapId}", gameController::getByMap);
            get("/gun/{gunId}", gameController::getByGun);
            get("/{id}/strategies", gameController::getStrategiesByGameId);
        };
    }
}