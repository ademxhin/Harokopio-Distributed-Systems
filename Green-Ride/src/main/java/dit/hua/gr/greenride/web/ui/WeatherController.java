package dit.hua.gr.greenride.web.ui;

import dit.hua.gr.greenride.core.port.WeatherApiPort;
import dit.hua.gr.greenride.core.port.exception.ExternalServiceException;
import dit.hua.gr.greenride.core.port.model.WeatherResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

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
            @ApiResponse(responseCode = "503", description = "External Weather API error/unreachable")
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
        WeatherResult result = weatherApiPort.getCurrentWeather(location);
        return ResponseEntity.ok(result);
    }

    private record ErrorResponse(String message) { }
}
