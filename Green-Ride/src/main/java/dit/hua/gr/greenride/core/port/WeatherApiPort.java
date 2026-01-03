package dit.hua.gr.greenride.core.port;

import dit.hua.gr.greenride.core.port.impl.dto.WeatherResult;

/**
 * Port for fetching weather information from an external Weather Service.
 *
 * The external service is treated as a black box and accessed via REST.
 */
public interface WeatherApiPort {

    /**
     * Fetches the current weather for a given location.
     *
     * @param location location name (e.g., "Athens", "Marousi")
     * @return WeatherResult containing simplified weather information
     */
    WeatherResult getCurrentWeather(String location);
}
