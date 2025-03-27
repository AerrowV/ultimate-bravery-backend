package dat.routes;

import dat.controllers.impl.StrategyController;
import dat.entities.enums.Role;
import io.javalin.apibuilder.EndpointGroup;

import static io.javalin.apibuilder.ApiBuilder.*;

public class StrategyRoute {

    private final StrategyController strategyController = new StrategyController();

    public EndpointGroup getRoutes() {
        return () -> {
            get("/", strategyController::readAll);
            get("/{id}", strategyController::read);
            post("/", strategyController::create, Role.ADMIN);
            put("/{id}", strategyController::update, Role.ADMIN);
            delete("/{id}", strategyController::delete, Role.ADMIN);
            get("/random/map/{mapId}", ctx -> {
                String type = ctx.queryParam("type");
                ctx.queryParamAsClass("type", String.class)
                        .check(t -> t != null && strategyController.isValidStrategyType(t),
                                "Invalid strategy type");
                strategyController.getRandomByMapAndType(ctx);
            });
            get("/map/{mapId}", strategyController::getByMapId);
        };
    }
}