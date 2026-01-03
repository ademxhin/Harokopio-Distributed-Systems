package dit.hua.gr.greenride.core.port.impl;

import dit.hua.gr.greenride.core.port.WeatherApiPort;
import dit.hua.gr.greenride.core.port.impl.dto.WeatherResult;
import dit.hua.gr.greenride.core.port.impl.dto.OpenMeteoResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.Objects;

/**
 * Default implementation of {@link WeatherApiPort}.
 *
 * It consumes an external Weather API (Open-Meteo),
 * which is treated as a black box REST service.
 */
@Service
public class WeatherApiPortImpl implements WeatherApiPort {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public WeatherApiPortImpl(
            RestTemplate restTemplate,
            @Value("${app.weather.base-url}") String baseUrl
    ) {
        this.restTemplate = Objects.requireNonNull(restTemplate);
        this.baseUrl = Objects.requireNonNull(baseUrl);
    }

    @Override
    public WeatherResult getCurrentWeather(String location) {

        if (location == null) {
            throw new NullPointerException("location must not be null");
        }
        if (location.isBlank()) {
            throw new IllegalArgumentException("location must not be blank");
        }

        /*
         * Static coordinates for demo purposes (Athens).
         * This is acceptable for the assignment and keeps the focus
         * on REST communication and system distribution.
         */
        double latitude = 37.9838;
        double longitude = 23.7275;

        String url = UriComponentsBuilder
                .fromHttpUrl(baseUrl)
                .path("/v1/forecast")
                .queryParam("latitude", latitude)
                .queryParam("longitude", longitude)
                .queryParam("current_weather", true)
                .toUriString();

        try {
            OpenMeteoResponse response =
                    restTemplate.getForObject(url, OpenMeteoResponse.class);

            if (response == null || response.current_weather() == null) {
                throw new WeatherServiceException("Invalid response from Weather API");
            }

            return new WeatherResult(
                    mapWeatherCode(response.current_weather().weathercode()),
                    response.current_weather().temperature()
            );

        } catch (Exception ex) {
            throw new WeatherServiceException("Failed to fetch weather data", ex);
        }
    }

    /**
     * Maps Open-Meteo weather codes to human-readable conditions.
     */
    private String mapWeatherCode(int code) {
        return switch (code) {
            case 0 -> "Clear";
            case 1, 2, 3 -> "Partly Cloudy";
            case 45, 48 -> "Fog";
            case 51, 53, 55, 61, 63, 65 -> "Rain";
            case 71, 73, 75 -> "Snow";
            default -> "Unknown";
        };
    }
}