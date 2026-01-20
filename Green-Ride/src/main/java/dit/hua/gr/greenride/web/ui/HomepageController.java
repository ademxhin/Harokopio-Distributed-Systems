package dit.hua.gr.greenride.web.ui;

import dit.hua.gr.greenride.core.port.MapsApiPort;
import dit.hua.gr.greenride.core.port.WeatherApiPort;
import dit.hua.gr.greenride.core.port.exception.ExternalServiceException;
import dit.hua.gr.greenride.core.port.model.RouteRequest;
import dit.hua.gr.greenride.core.port.model.RouteResult;
import dit.hua.gr.greenride.core.port.model.WeatherResult;
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

        try {
            WeatherResult weather = weatherApiPort.getCurrentWeather(location);
            model.addAttribute("weatherLocation", location);
            model.addAttribute("weather", weather);
        } catch (IllegalArgumentException ex) {
            log.warn("Weather request invalid: {}", ex.getMessage());
            model.addAttribute("weatherLocation", location);
            model.addAttribute("weatherError", "Weather request invalid");
        } catch (ExternalServiceException ex) {
            log.warn("Weather external service failed [{}]: {}", ex.getServiceName(), ex.getMessage(), ex);
            model.addAttribute("weatherLocation", location);
            model.addAttribute("weatherError", "Weather service unavailable");
        }

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
        } catch (IllegalArgumentException ex) {
            log.warn("Route request invalid: {}", ex.getMessage());
            model.addAttribute("routeError", "Route request invalid");
        } catch (ExternalServiceException ex) {
            log.warn("Maps external service failed [{}]: {}", ex.getServiceName(), ex.getMessage(), ex);

            model.addAttribute("routeError", "Route service unavailable");
        }

        return "homepage";
    }
}