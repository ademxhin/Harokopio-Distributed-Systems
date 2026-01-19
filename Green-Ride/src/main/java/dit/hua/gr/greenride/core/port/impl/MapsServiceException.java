package dit.hua.gr.greenride.core.port.impl;

public class MapsServiceException extends RuntimeException {

    public MapsServiceException(String message) {
        super(message);
    }

    public MapsServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}
