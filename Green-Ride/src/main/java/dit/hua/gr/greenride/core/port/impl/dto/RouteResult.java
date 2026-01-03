package dit.hua.gr.greenride.core.port.impl.dto;

import java.util.List;

/**
 * Result returned by our system for map route info.
 * distanceMeters: total route length in meters
 * durationSeconds: estimated travel time in seconds
 * polyline: list of [lon, lat] pairs (GeoJSON-style) for drawing on the map
 */
public record RouteResult(
        double distanceMeters,
        double durationSeconds,
        List<List<Double>> polyline
) { }
