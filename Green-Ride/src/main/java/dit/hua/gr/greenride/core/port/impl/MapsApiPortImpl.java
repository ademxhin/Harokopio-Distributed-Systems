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
        this.restTemplate = Objects.requireNonNull(restTemplate);
        this.baseUrl = Objects.requireNonNull(baseUrl);
        this.apiKey = Objects.requireNonNull(apiKey);
    }

    @Override
    public RouteResult getRoute(RouteRequest request) {
        if (request == null) throw new NullPointerException("request must not be null");

        // ORS expects: "lon,lat"
        String start = request.fromLon() + "," + request.fromLat();
        String end = request.toLon() + "," + request.toLat();

        String url = UriComponentsBuilder
                .fromHttpUrl(baseUrl)
                .path("/v2/directions/driving-car")
                .queryParam("start", start)
                .queryParam("end", end)
                .toUriString();

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", apiKey);

        // ✅ FIX: ORS wants GeoJSON response type
        headers.setAccept(List.of(MediaType.parseMediaType("application/geo+json")));
        // (safe addition)
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Void> entity = new HttpEntity<>(headers);

        try {
            ResponseEntity<OpenRouteServiceResponse> response =
                    restTemplate.exchange(url, HttpMethod.GET, entity, OpenRouteServiceResponse.class);

            if (!response.getStatusCode().is2xxSuccessful()
                    || response.getBody() == null
                    || response.getBody().features() == null
                    || response.getBody().features().isEmpty()) {
                throw new MapsServiceException("Invalid response from Maps API");
            }

            OpenRouteServiceResponse.Feature feature = response.getBody().features().get(0);

            double distance = feature.properties().summary().distance();
            double duration = feature.properties().summary().duration();
            List<List<Double>> coordinates = feature.geometry().coordinates(); // [lon, lat]

            return new RouteResult(distance, duration, coordinates);

        } catch (Exception ex) {
            throw new MapsServiceException("Failed to fetch route data", ex);
        }
    }
}