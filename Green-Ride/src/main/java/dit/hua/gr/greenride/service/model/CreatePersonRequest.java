package dit.hua.gr.greenride.service.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Request DTO used when registering a new user (person).
 */
public record CreatePersonRequest(

        @NotBlank
        @Size(max = 20)
        String firstName,

        @NotBlank
        @Size(max = 20)
        String lastName,

        @NotBlank
        @Size(max = 18)
        String mobilePhoneNumber,

        @NotBlank
        @Size(max = 40)
        @Email
        String emailAddress,

        @NotBlank
        @Size(min = 8, max = 100)
        String rawPassword
) {
}
