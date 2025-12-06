package dit.hua.gr.greenride.core.port.impl.dto;

/**
 * DTO returned by the Weather API providing basic weather information.
 */
public record WeatherResult(
        String condition,
        double temperatureCelsius
) { }
