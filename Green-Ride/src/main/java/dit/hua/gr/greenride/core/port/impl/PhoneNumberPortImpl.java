package dit.hua.gr.greenride.core.port.impl;

import dit.hua.gr.greenride.core.port.PhoneNumberPort;
import dit.hua.gr.greenride.core.port.model.PhoneNumberValidationResult;
import dit.hua.gr.greenride.web.ui.exceptions.ExternalServiceUnavailableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;

@Service
public class PhoneNumberPortImpl implements PhoneNumberPort {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public PhoneNumberPortImpl(
            final RestTemplate restTemplate,
            @Value("${app.noc.base-url:http://localhost:8081}") final String baseUrl
    ) {
        if (restTemplate == null) throw new NullPointerException();
        if (baseUrl == null) throw new NullPointerException();
        if (baseUrl.isBlank()) throw new IllegalArgumentException();

        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    @Override
    public PhoneNumberValidationResult validate(final String rawPhoneNumber) {
        if (rawPhoneNumber == null) throw new NullPointerException();
        if (rawPhoneNumber.isBlank()) throw new IllegalArgumentException();

        final String normalized = normalizeGreekMobile(rawPhoneNumber);

        final URI uri = UriComponentsBuilder
                .fromUriString(baseUrl)
                .path("/api/v1/phone-numbers/{raw}/validations")
                .buildAndExpand(normalized)
                .encode()
                .toUri();

        try {
            final ResponseEntity<PhoneNumberValidationResult> response =
                    this.restTemplate.getForEntity(uri, PhoneNumberValidationResult.class);

            if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                return response.getBody();
            }

            return fallbackInvalid(rawPhoneNumber);

        } catch (RestClientException ex) {
            throw new ExternalServiceUnavailableException("NOC phone validation service is unavailable", ex);
        }
    }

    private String normalizeGreekMobile(final String raw) {
        final String trimmed = raw.trim();
        final String digits = trimmed.replaceAll("[^0-9]", "");

        if (digits.length() == 10 && digits.startsWith("69")) {
            return "+30" + digits;
        }
        if (trimmed.startsWith("+")) {
            return trimmed;
        }
        return trimmed;
    }

    private PhoneNumberValidationResult fallbackInvalid(final String rawPhoneNumber) {
        return new PhoneNumberValidationResult(rawPhoneNumber, false, null, null);
    }
}
