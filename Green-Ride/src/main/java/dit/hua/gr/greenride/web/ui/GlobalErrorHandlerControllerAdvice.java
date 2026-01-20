package dit.hua.gr.greenride.web.ui;

import dit.hua.gr.greenride.core.port.exception.ExternalServiceException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

@ControllerAdvice
public class GlobalErrorHandlerControllerAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalErrorHandlerControllerAdvice.class);

    @ExceptionHandler(NoResourceFoundException.class)
    public String handleNoResourceFound(final NoResourceFoundException exception,
                                        final HttpServletRequest request,
                                        final HttpServletResponse response,
                                        final Model model) {

        String uri = request.getRequestURI();

        if ("/.well-known/appspecific/com.chrome.devtools.json".equals(uri)) {
            response.setStatus(404);
            return "error/404";
        }

        LOGGER.debug("Static resource not found: {}", uri);

        model.addAttribute("message", "Not Found");
        model.addAttribute("path", uri);

        response.setStatus(404);
        return "error/404";
    }

    @ExceptionHandler(ExternalServiceException.class)
    public String handleExternalService(final ExternalServiceException exception,
                                        final HttpServletRequest request,
                                        final HttpServletResponse response,
                                        final Model model) {

        LOGGER.warn("External service failed [{}] at {}: {}",
                exception.getServiceName(),
                request.getRequestURI(),
                exception.getMessage());

        model.addAttribute(
                "message",
                "An external service is temporarily unavailable (" + exception.getServiceName() + "). Please try again."
        );
        model.addAttribute("path", request.getRequestURI());

        response.setStatus(503);
        return "error/error";
    }

    @ExceptionHandler(Exception.class)
    public String handleAnyError(final Exception exception,
                                 final HttpServletRequest request,
                                 final HttpServletResponse response,
                                 final Model model) {

        LOGGER.warn("Handling exception {} {}", exception.getClass(), exception.getMessage());

        String msg = (exception.getMessage() != null && !exception.getMessage().isBlank())
                ? exception.getMessage()
                : "Unexpected error";

        model.addAttribute("message", msg);
        model.addAttribute("path", request.getRequestURI());

        if (exception instanceof AuthenticationException) {
            response.setStatus(401);
            return "error/error";
        }

        if (exception instanceof AccessDeniedException) {
            response.setStatus(403);
            return "error/error";
        }

        if (exception instanceof ResponseStatusException rse) {
            if (rse.getStatusCode().value() == 404) {
                response.setStatus(404);
                return "error/404";
            }
        }

        response.setStatus(500);
        return "error/error";
    }
}