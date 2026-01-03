package dit.hua.gr.greenride.core.port.impl;

/**
 * Thrown when the external Weather API is unreachable
 * or returns an invalid/unexpected response.
 */
public class WeatherServiceException extends RuntimeException {

    public WeatherServiceException(String message) {
        super(message);
    }

    public WeatherServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}