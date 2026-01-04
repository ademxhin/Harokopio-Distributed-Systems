package dit.hua.gr.greenride.service.model;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;

import java.util.List;

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
        @Email
        @Size(max = 40)
        String emailAddress,

        @NotBlank
        @Size(min = 6, max = 50)
        String rawPassword,

        @NotBlank
        @Size(min = 6, max = 50)
        String confirmRawPassword,

        // ✅ Dropdown επιλογή (PASSENGER / DRIVER / BOTH)
        @NotBlank(message = "Please select a role")
        String roleSelection
) { }