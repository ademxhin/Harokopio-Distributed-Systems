package dit.hua.gr.greenride.core.port;

import dit.hua.gr.greenride.core.port.model.PhoneNumberValidationResult;

public interface PhoneNumberPort {

    PhoneNumberValidationResult validate(final String rawPhoneNumber);
}