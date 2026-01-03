package dit.hua.gr.greenride.core.port.impl.dto;

/**
 * DTO for the Open-Meteo API response.
 *
 * We only model the fields we need (current_weather.temperature, current_weather.weathercode).
 * The external service is treated as a black box.
 */
public record OpenMeteoResponse(
        CurrentWeather current_weather
) {

    public record CurrentWeather(
            double temperature,
            int weathercode
    ) { }
}
