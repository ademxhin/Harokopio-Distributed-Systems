package dit.hua.gr.greenride.web.ui;

import dit.hua.gr.greenride.core.port.MapsApiPort;
import dit.hua.gr.greenride.core.port.WeatherApiPort;
import dit.hua.gr.greenride.core.port.impl.MapsServiceException;
import dit.hua.gr.greenride.core.port.impl.WeatherServiceException;
import dit.hua.gr.greenride.core.port.impl.dto.RouteRequest;
import dit.hua.gr.greenride.core.port.impl.dto.RouteResult;
import dit.hua.gr.greenride.core.port.impl.dto.WeatherResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.Objects;

@Controller
public class HomepageController {

    private static final Logger log = LoggerFactory.getLogger(HomepageController.class);

    private final WeatherApiPort weatherApiPort;
    private final MapsApiPort mapsApiPort;

    public HomepageController(WeatherApiPort weatherApiPort, MapsApiPort mapsApiPort) {
        this.weatherApiPort = Objects.requireNonNull(weatherApiPort);
        this.mapsApiPort = Objects.requireNonNull(mapsApiPort);
    }

    @GetMapping({"/", "/homepage"})
    public String homepage(Model model) {

        final String location = "Athens";

        // Weather
        try {
            WeatherResult weather = weatherApiPort.getCurrentWeather(location);
            model.addAttribute("weatherLocation", location);
            model.addAttribute("weather", weather);
        } catch (WeatherServiceException | IllegalArgumentException ex) {
            log.warn("Weather API failed: {}", ex.getMessage(), ex);
            model.addAttribute("weatherLocation", location);
            model.addAttribute("weatherError", "Weather service unavailable");
        }

        // Route demo: Harokopio University -> Syntagma (example coords)
        double fromLat = 37.962219;
        double fromLon = 23.700788;
        double toLat = 37.975436;
        double toLon = 23.735724;

        model.addAttribute("routeFromLat", fromLat);
        model.addAttribute("routeFromLon", fromLon);
        model.addAttribute("routeToLat", toLat);
        model.addAttribute("routeToLon", toLon);

        try {
            RouteResult route = mapsApiPort.getRoute(new RouteRequest(fromLat, fromLon, toLat, toLon));
            model.addAttribute("route", route);
        } catch (MapsServiceException ex) {
            log.error("Maps API failed: {}", ex.getMessage(), ex);

            model.addAttribute("routeError", ex.getMessage());
        }

        return "homepage";
    }
}