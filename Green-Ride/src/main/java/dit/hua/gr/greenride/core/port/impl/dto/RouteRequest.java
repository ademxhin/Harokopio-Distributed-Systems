package dit.hua.gr.greenride.core.port.impl.dto;

/**
 * DTO used to request distance & duration between two points.
 */
public record RouteRequest(
        double fromLat,
        double fromLon,
        double toLat,
        double toLon
) { }
