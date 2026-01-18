package dit.hua.gr.greenride.web.ui;

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

    /**
     * 404 for missing static resources.
     * Special-case Chrome DevTools well-known request to avoid log spam.
     */
    @ExceptionHandler(NoResourceFoundException.class)
    public String handleNoResourceFound(final NoResourceFoundException exception,
                                        final HttpServletRequest request,
                                        final HttpServletResponse response,
                                        final Model model) {

        String uri = request.getRequestURI();

        // ✅ Chrome/DevTools noise: don't warn-log it
        if ("/.well-known/appspecific/com.chrome.devtools.json".equals(uri)) {
            response.setStatus(404);
            return "error/404";
        }

        // For other static 404s, you can log at DEBUG (or keep WARN if you want)
        LOGGER.debug("Static resource not found: {}", uri);

        model.addAttribute("message", "Not Found");
        model.addAttribute("path", uri);

        response.setStatus(404);
        return "error/404";
    }

    @ExceptionHandler(Exception.class)
    public String handleAnyError(final Exception exception,
                                 final HttpServletRequest request,
                                 final HttpServletResponse response,
                                 final Model model) {

        // ✅ do not warn for exceptions that have their own handler
        // (NoResourceFoundException is handled above)
        LOGGER.warn("Handling exception {} {}", exception.getClass(), exception.getMessage());

        model.addAttribute("message", exception.getMessage());
        model.addAttribute("path", request.getRequestURI());

        // 401 - Unauthorized (authentication failure)
        if (exception instanceof AuthenticationException) {
            response.setStatus(401);
            return "error/error";
        }

        // 403 - Forbidden (access denied)
        if (exception instanceof AccessDeniedException) {
            response.setStatus(403);
            return "error/error";
        }

        // ResponseStatusException (e.g. 404)
        if (exception instanceof ResponseStatusException rse) {
            if (rse.getStatusCode().value() == 404) {
                response.setStatus(404);
                return "error/404";
            }
        }

        // Default → 500 Internal Server Error
        response.setStatus(500);
        return "error/error";
    }
}