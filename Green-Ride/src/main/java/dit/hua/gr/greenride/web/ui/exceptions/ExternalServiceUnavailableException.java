package dit.hua.gr.greenride.web.ui.exceptions;

public class ExternalServiceUnavailableException extends RuntimeException {
    public ExternalServiceUnavailableException(final String message, final Throwable cause) {
        super(message, cause);
    }
}
