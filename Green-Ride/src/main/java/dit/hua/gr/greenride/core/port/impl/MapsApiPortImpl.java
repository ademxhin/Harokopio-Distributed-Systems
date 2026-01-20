package dit.hua.gr.greenride.core.port.impl;

import dit.hua.gr.greenride.core.port.MapsApiPort;
import dit.hua.gr.greenride.core.port.impl.dto.OpenRouteServiceResponse;
import dit.hua.gr.greenride.core.port.impl.dto.RouteRequest;
import dit.hua.gr.greenride.core.port.impl.dto.RouteResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class MapsApiPortImpl implements MapsApiPort {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String apiKey;

    public MapsApiPortImpl(
            RestTemplate restTemplate,
            @Value("${app.maps.base-url}") String baseUrl,
            @Value("${app.maps.api-key}") String apiKey
    ) {
        this.restTemplate = Objects.requireNonNull(restTemplate, "restTemplate must not be null");
        this.baseUrl = Objects.requireNonNull(baseUrl, "baseUrl must not be null");
        this.apiKey = (apiKey == null) ? "" : apiKey;
    }

    @Override
    public RouteResult getRoute(RouteRequest request) {
        if (request == null) throw new NullPointerException("request must not be null");

        if (!isValidLonLat(request.fromLon(), request.fromLat())
                || !isValidLonLat(request.toLon(), request.toLat())) {
            throw new MapsServiceException("Invalid coordinates in RouteRequest");
        }

        if (apiKey.trim().isEmpty()) {
            return mockRoute(request);
        }

        String start = request.fromLon() + "," + request.fromLat();
        String end = request.toLon() + "," + request.toLat();

        String url = UriComponentsBuilder
                .fromHttpUrl(baseUrl)
                .path("/v2/directions/driving-car")
                .queryParam("start", start)
                .queryParam("end", end)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", apiKey.trim());
        headers.setAccept(List.of(
                MediaType.parseMediaType("application/geo+json"),
                MediaType.APPLICATION_JSON
        ));
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<OpenRouteServiceResponse> response =
                    restTemplate.exchange(url, HttpMethod.GET, entity, OpenRouteServiceResponse.class);

            if (!response.getStatusCode().is2xxSuccessful()
                    || response.getBody() == null
                    || response.getBody().features() == null
                    || response.getBody().features().isEmpty()) {
                return mockRoute(request);
            }

            OpenRouteServiceResponse.Feature feature = response.getBody().features().get(0);

            double distance = feature.properties().summary().distance();
            double duration = feature.properties().summary().duration();
            List<List<Double>> coordinates = feature.geometry().coordinates();

            return new RouteResult(distance, duration, coordinates);

        } catch (Exception ex) {
            return mockRoute(request);
        }
    }

    private boolean isValidLonLat(double lon, double lat) {
        return lon >= -180.0 && lon <= 180.0 && lat >= -90.0 && lat <= 90.0;
    }

    private RouteResult mockRoute(RouteRequest request) {
        double fromLon = request.fromLon();
        double fromLat = request.fromLat();
        double toLon = request.toLon();
        double toLat = request.toLat();

        List<List<Double>> coords = new ArrayList<>();
        coords.add(List.of(fromLon, fromLat));

        for (int i = 1; i <= 3; i++) {
            double t = i / 4.0;
            double lon = fromLon + (toLon - fromLon) * t;
            double lat = fromLat + (toLat - fromLat) * t;

            double wiggle = 0.002 * Math.sin(t * Math.PI);
            coords.add(List.of(lon, lat + wiggle));
        }

        coords.add(List.of(toLon, toLat));

        double approxDistanceMeters = approximateDistanceMeters(fromLon, fromLat, toLon, toLat);
        double approxDurationSeconds = approxDistanceMeters / 13.9;

        return new RouteResult(approxDistanceMeters, approxDurationSeconds, coords);
    }

    private double approximateDistanceMeters(double lon1, double lat1, double lon2, double lat2) {
        final double R = 6371000.0;
        double phi1 = Math.toRadians(lat1);
        double phi2 = Math.toRadians(lat2);
        double dPhi = Math.toRadians(lat2 - lat1);
        double dLambda = Math.toRadians(lon2 - lon1);

        double a = Math.sin(dPhi / 2) * Math.sin(dPhi / 2)
                + Math.cos(phi1) * Math.cos(phi2)
                * Math.sin(dLambda / 2) * Math.sin(dLambda / 2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c;
    }
}