package dit.hua.gr.greenride.core.port;

import dit.hua.gr.greenride.core.port.impl.dto.PhoneNumberValidationResult;

public interface PhoneNumberPort {

    PhoneNumberValidationResult validate(final String rawPhoneNumber);
}