package dit.hua.gr.greenride.core.port.impl.dto;

import java.util.List;

/**
 * Minimal DTO for OpenRouteService directions response (GeoJSON).
 */
public record OpenRouteServiceResponse(
        List<Feature> features
) {
    public record Feature(
            Properties properties,
            Geometry geometry
    ) { }

    public record Properties(
            Summary summary
    ) { }

    public record Summary(
            double distance,
            double duration
    ) { }

    public record Geometry(
            List<List<Double>> coordinates
    ) { }
}
