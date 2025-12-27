package dit.hua.gr.greenride.web.rest;

import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;

@RestControllerAdvice(basePackages = "dit.hua.gr.greenride.web.rest")
@Order(1)
public class GlobalErrorHandlerRestControllerAdvice {

    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalErrorHandlerRestControllerAdvice.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleAnyError(final Exception exception,
                                                   final HttpServletRequest request) {

        HttpStatus httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;

        if (exception instanceof NoResourceFoundException) {
            httpStatus = HttpStatus.NOT_FOUND;

        } else if (exception instanceof AuthenticationException) {
            httpStatus = HttpStatus.UNAUTHORIZED;

        } else if (exception instanceof AccessDeniedException) {
            httpStatus = HttpStatus.FORBIDDEN;

        } else if (exception instanceof ResponseStatusException responseStatusException) {
            try {
                httpStatus = HttpStatus.valueOf(responseStatusException.getStatusCode().value());
            } catch (Exception ignored) {}
        }

        LOGGER.warn("REST error [{} {}] -> status={} cause={}: {}",
                request.getMethod(),
                request.getRequestURI(),
                httpStatus.value(),
                exception.getClass().getSimpleName(),
                exception.getMessage()
        );

        final ApiError apiError = new ApiError(
                Instant.now(),
                httpStatus.value(),
                httpStatus.getReasonPhrase(),
                exception.getMessage(),
                request.getRequestURI()
        );

        return ResponseEntity.status(httpStatus).body(apiError);
    }
}
