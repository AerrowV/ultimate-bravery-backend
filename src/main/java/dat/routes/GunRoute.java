package dat.routes;

import dat.controllers.impl.GunController;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class GunRoute {

    private final GunController gunController = new GunController();

    public EndpointGroup getRoutes() {
        return () -> {
            get("/", gunController::readAll);
            get("/{id}", gunController::read);
            post("/", gunController::create);
            put("/{id}", gunController::update);
            delete("/{id}", gunController::delete);
            get("/random/game/{gameId}", gunController::getRandomByGame);
        };
    }
}