package dat.routes;

import dat.controllers.impl.MapController;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class MapRoute {

    private final MapController mapController = new MapController();

    public EndpointGroup getRoutes() {
        return () -> {
            get("/", mapController::readAll);
            get("/{id}", mapController::read);
            post("/", mapController::create);
            put("/{id}", mapController::update);
            delete("/{id}", mapController::delete);
            get("/{id}/with-strategies", mapController::readWithStrategies);
        };
    }
}