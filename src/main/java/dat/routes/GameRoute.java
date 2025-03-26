package dat.routes;

import dat.controllers.impl.GameController;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class GameRoute {

    private final GameController gameController = new GameController();

    public EndpointGroup getRoutes() {
        return () -> {
            get("/populate", gameController::populate);
            get("/", gameController::readAll);
            get("/{id}", gameController::read);
            post("/", gameController::create);
            put("/{id}", gameController::update);
            delete("/{id}", gameController::delete);
            get("/map/{mapId}", gameController::getByMap);
            get("/gun/{gunId}", gameController::getByGun);
        };
    }
}