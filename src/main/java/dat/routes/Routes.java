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
            path("/games", gameRoute.getRoutes());
            path("/guns", gunRoute.getRoutes());
            path("/maps", mapRoute.getRoutes());
            path("/strategies", strategyRoute.getRoutes());
            SecurityRoutes.getSecurityRoutes().addEndpoints();
        };
    }
}