package dit.hua.gr.greenride.core.port.impl;

import dit.hua.gr.greenride.core.port.SmsNotificationPort;
import dit.hua.gr.greenride.core.port.impl.dto.SendSmsRequest;
import dit.hua.gr.greenride.core.port.impl.dto.SendSmsResult;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@Service
public class SmsNotificationPortImpl implements SmsNotificationPort {

    private static final Logger LOGGER = LoggerFactory.getLogger(SmsNotificationPortImpl.class);

    /**
     * Flag indicating whether the external SMS service is active or not.
     */
    private static final boolean ACTIVE = false; // later → from properties

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public SmsNotificationPortImpl(
            final RestTemplate restTemplate,
            @Value("${app.sms.base-url}") final String baseUrl
    ) {
        if (restTemplate == null) throw new NullPointerException();
        if (baseUrl == null || baseUrl.isBlank()) throw new IllegalArgumentException();

        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
    }

    @Override
    public boolean sendSms(final String e164, final String content) {
        if (e164 == null) throw new NullPointerException();
        if (e164.isBlank()) throw new IllegalArgumentException();
        if (content == null) throw new NullPointerException();
        if (content.isBlank()) throw new IllegalArgumentException();

        if (!ACTIVE) {
            LOGGER.warn("SMS Notification is not active");
            return true;
        }

        // Optional ignore logic
        if (e164.startsWith("+30692") || e164.startsWith("+30690000")) {
            LOGGER.warn("Not allocated E164 {}. Aborting...", e164);
            return true;
        }

        final HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        final SendSmsRequest body = new SendSmsRequest(e164, content);
        final HttpEntity<SendSmsRequest> entity = new HttpEntity<>(body, headers);

        final String url = UriComponentsBuilder
                .fromUriString(baseUrl)
                .pathSegment("api", "v1", "sms")
                .build()
                .toUriString();

        final ResponseEntity<SendSmsResult> response =
                this.restTemplate.postForEntity(url, entity, SendSmsResult.class);

        if (response.getStatusCode().is2xxSuccessful()) {
            final SendSmsResult result = response.getBody();
            if (result == null) throw new NullPointerException();
            return result.sent();
        }

        throw new RuntimeException("External SMS service responded with " + response.getStatusCode());
    }
}
