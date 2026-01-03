package dit.hua.gr.greenride.web.ui;

import dit.hua.gr.greenride.core.port.WeatherApiPort;
import dit.hua.gr.greenride.core.port.impl.WeatherServiceException;
import dit.hua.gr.greenride.core.port.impl.dto.WeatherResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

/**
 * REST controller exposing weather information.
 *
 * This endpoint is part of Green Ride and consumes an external Weather API (black box)
 * through the WeatherApiPort.
 */
@RestController
@RequestMapping("/api/weather")
public class WeatherController {

    private final WeatherApiPort weatherApiPort;

    public WeatherController(WeatherApiPort weatherApiPort) {
        this.weatherApiPort = Objects.requireNonNull(weatherApiPort);
    }

    @Operation(
            summary = "Get current weather by location",
            description = "Fetches current weather information for a location by consuming an external Weather API (black box)."
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Weather fetched successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid location parameter"),
            @ApiResponse(responseCode = "502", description = "External Weather API error/unreachable")
    })
    @GetMapping("/current")
    public ResponseEntity<?> getCurrentWeather(
            @Parameter(
                    description = "Location name (for demo purposes, the provider is queried with fixed coordinates)",
                    example = "Athens",
                    schema = @Schema(type = "string")
            )
            @RequestParam String location
    ) {
        try {
            WeatherResult result = weatherApiPort.getCurrentWeather(location);
            return ResponseEntity.ok(result);

        } catch (IllegalArgumentException ex) {
            return ResponseEntity.badRequest().body(new ErrorResponse("Invalid request: " + ex.getMessage()));

        } catch (WeatherServiceException ex) {
            // 502 Bad Gateway is appropriate when an upstream service fails
            return ResponseEntity.status(502).body(new ErrorResponse("Weather provider error: " + ex.getMessage()));
        }
    }

    /**
     * Simple error body for consistent API responses.
     */
    private record ErrorResponse(String message) { }
}
