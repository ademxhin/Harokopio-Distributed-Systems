package dit.hua.gr.greenride.core.port.impl.dto;

public record RouteRequest(
        double fromLat,
        double fromLon,
        double toLat,
        double toLon
) { }
