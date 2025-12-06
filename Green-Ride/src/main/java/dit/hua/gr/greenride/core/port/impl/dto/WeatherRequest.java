package dit.hua.gr.greenride.core.port.impl.dto;

/**
 * DTO used to request weather information for a specific location and date.
 */
public record WeatherRequest(
        String location
) { }
