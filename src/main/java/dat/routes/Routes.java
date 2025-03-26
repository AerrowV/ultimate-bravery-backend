package dat.routes;

import io.javalin.apibuilder.EndpointGroup;
import static io.javalin.apibuilder.ApiBuilder.*;

public class Routes {

    private final GameRoute gameRoute = new GameRoute();
    private final GunRoute gunRoute = new GunRoute();
    private final MapRoute mapRoute = new MapRoute();
    private final StrategyRoute strategyRoute = new StrategyRoute();
    private final SecurityRoutes securityRoutes = new SecurityRoutes();

    public EndpointGroup getRoutes() {
        return () -> {
            // Security routes (no authentication required)
            path("/api", () -> {
                addSecurityRoutes();
            });

            // Protected API routes
            path("/api/protected", () -> {
                addGameRoutes();
                addGunRoutes();
                addMapRoutes();
                addStrategyRoutes();
            });
        };
    }

    private void addSecurityRoutes() {
        securityRoutes.getSecurityRoutes().addEndpoints();
    }

    private void addGameRoutes() {
        path("/games", gameRoute.getRoutes());
    }

    private void addGunRoutes() {
        path("/guns", gunRoute.getRoutes());
    }

    private void addMapRoutes() {
        path("/maps", mapRoute.getRoutes());
    }

    private void addStrategyRoutes() {
        path("/strategies", strategyRoute.getRoutes());
    }
}