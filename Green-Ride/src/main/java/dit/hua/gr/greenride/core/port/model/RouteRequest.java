package dit.hua.gr.greenride.core.port.model;

public record RouteRequest(
        double fromLat,
        double fromLon,
        double toLat,
        double toLon
) { }
