package dat.routes;

import dat.controllers.impl.MapController;
import dat.entities.enums.Role;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class MapRoute {

    private final MapController mapController = new MapController();

    public EndpointGroup getRoutes() {
        return () -> {
            get("/", mapController::readAll);
            get("/{id}", mapController::read);
            post("/", mapController::create, Role.ADMIN);
            put("/{id}", mapController::update, Role.ADMIN);
            delete("/{id}", mapController::delete, Role.ADMIN);
        };
    }
}