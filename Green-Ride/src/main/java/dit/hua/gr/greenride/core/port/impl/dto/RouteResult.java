package dit.hua.gr.greenride.core.port.impl.dto;

import java.util.List;

public record RouteResult(
        double distanceMeters,
        double durationSeconds,
        List<List<Double>> polyline
) { }
