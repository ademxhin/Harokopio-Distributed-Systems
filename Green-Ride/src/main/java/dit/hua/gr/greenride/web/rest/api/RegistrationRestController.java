package dit.hua.gr.greenride.web.rest.api;

import dit.hua.gr.greenride.service.PersonService;
import dit.hua.gr.greenride.service.model.CreatePersonRequest;
import dit.hua.gr.greenride.service.model.CreatePersonResult;
import dit.hua.gr.greenride.web.ui.exceptions.ExternalServiceUnavailableException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@Tag(name = "Auth", description = "Authentication & registration endpoints")
@RestController
@RequestMapping("/api/auth")
public class RegistrationRestController {

    private final PersonService personService;

    public RegistrationRestController(final PersonService personService) {
        this.personService = personService;
    }

    public record RegisterResponse(
            boolean created,
            String reason
    ) {}

    @Operation(
            summary = "Register a new user",
            description = "Creates a new user account (Passenger or Driver).",
            security = {}
    )
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "User created"),
            @ApiResponse(responseCode = "400", description = "Validation / business error (e.g. duplicate email, password mismatch)"),
            @ApiResponse(responseCode = "503", description = "External validation service unavailable")
    })
    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public RegisterResponse register(@Valid @RequestBody CreatePersonRequest request) {

        if (request.rawPassword() == null || request.confirmRawPassword() == null
                || !request.rawPassword().equals(request.confirmRawPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Password and Confirm Password do not match");
        }

        try {
            CreatePersonResult result = personService.createPerson(request, true);

            if (!result.created()) {
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, result.reason());
            }

            return new RegisterResponse(true, null);

        } catch (ExternalServiceUnavailableException ex) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE,
                    "Phone validation service is currently unavailable. Please try again later.");
        }
    }
}