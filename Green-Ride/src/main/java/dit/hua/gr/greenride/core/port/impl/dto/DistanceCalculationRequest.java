package dit.hua.gr.greenride.core.port.impl.dto;

/**
 * DTO used to request distance estimation from the Maps API.
 */
public record DistanceCalculationRequest(
        String startLocation,
        String endLocation
) { }
