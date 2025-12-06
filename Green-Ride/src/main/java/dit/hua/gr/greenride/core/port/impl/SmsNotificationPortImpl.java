package dit.hua.gr.greenride.core.port.impl;

import dit.hua.gr.greenride.config.RestApiClientConfig;
import dit.hua.gr.greenride.core.port.SmsNotificationPort;
import dit.hua.gr.greenride.core.port.impl.dto.SendSmsRequest;
import dit.hua.gr.greenride.core.port.impl.dto.SendSmsResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

/**
 * Default implementation of {@link SmsNotificationPort}.
 * It uses an external SMS service (simulated for now).
 */
@Service
public class SmsNotificationPortImpl implements SmsNotificationPort {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmsNotificationPortImpl.class);

    /**
     * Flag indicating whether the external SMS service is active or not.
     * Currently set to false so that no real HTTP call is performed.
     */
    private static final boolean ACTIVE = false; // @future Get from application properties.

    private final RestTemplate restTemplate;

    public SmsNotificationPortImpl(final RestTemplate restTemplate) {
        if (restTemplate == null) {
            throw new NullPointerException();
        }
        this.restTemplate = restTemplate;
    }

    @Override
    public boolean sendSms(final String e164, final String content) {
        if (e164 == null) {
            throw new NullPointerException();
        }
        if (e164.isBlank()) {
            throw new IllegalArgumentException();
        }
        if (content == null) {
            throw new NullPointerException();
        }
        if (content.isBlank()) {
            throw new IllegalArgumentException();
        }

        // --------------------------------------------------

        if (!ACTIVE) {
            LOGGER.warn("SMS Notification is not active");
            // For now, we consider this as a "successful" operation.
            return true;
        }

        // Optional: ignore test or not allocated numbers (example logic)
        if (e164.startsWith("+30692") || e164.startsWith("+30690000")) {
            LOGGER.warn("Not allocated E164 {}. Aborting...", e164);
            return true;
        }

        // Headers
        // --------------------------------------------------

        final HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        // Payload
        // --------------------------------------------------

        final SendSmsRequest body = new SendSmsRequest(e164, content);

        // HTTP Request
        // --------------------------------------------------

        final String baseUrl = RestApiClientConfig.BASE_URL;
        final String url = baseUrl + "/api/v1/sms";

        final HttpEntity<SendSmsRequest> entity = new HttpEntity<>(body, httpHeaders);
        final ResponseEntity<SendSmsResult> response =
                this.restTemplate.postForEntity(url, entity, SendSmsResult.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            final SendSmsResult sendSmsResult = response.getBody();
            if (sendSmsResult == null) {
                throw new NullPointerException();
            }
            return sendSmsResult.sent();
        }

        throw new RuntimeException("External SMS service responded with " + response.getStatusCode());
    }
}