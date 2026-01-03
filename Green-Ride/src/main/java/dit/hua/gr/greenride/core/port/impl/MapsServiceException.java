package dit.hua.gr.greenride.core.port.impl;

/**
 * Thrown when the external Maps API is unreachable or returns invalid response.
 */
public class MapsServiceException extends RuntimeException {

    public MapsServiceException(String message) {
        super(message);
    }

    public MapsServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
