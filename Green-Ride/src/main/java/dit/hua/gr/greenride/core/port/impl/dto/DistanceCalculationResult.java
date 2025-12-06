package dit.hua.gr.greenride.core.port.impl.dto;

/**
 * DTO returned by the Maps API containing distance and duration information.
 */
public record DistanceCalculationResult(
        double distanceKm,
        int estimatedMinutes
) { }
