package dit.hua.gr.greenride.web.rest.api;

import dit.hua.gr.greenride.core.model.Person;
import dit.hua.gr.greenride.core.repository.PersonRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Set;
import java.util.stream.Collectors;

@Tag(name = "Me", description = "Current authenticated user")
@RestController
@RequestMapping("/api/me")
public class MeRestController {

    private final PersonRepository personRepository;

    public MeRestController(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    public record MeResponse(
            String fullName,
            String emailAddress,
            String mobilePhoneNumber,
            Set<String> roles
    ) {}

    @Operation(summary = "Get current user profile (from JWT/session)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Current user returned"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @GetMapping
    public MeResponse me(Authentication authentication) {
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }

        String email = authentication.getName();

        Person person = personRepository.findByEmailAddress(email).orElse(null);

        Set<String> roles = authentication.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet());

        String fullName = (person != null && person.getFullName() != null && !person.getFullName().isBlank())
                ? person.getFullName()
                : buildDisplayName(email);

        String phone = (person != null && person.getMobilePhoneNumber() != null && !person.getMobilePhoneNumber().isBlank())
                ? person.getMobilePhoneNumber()
                : "Not provided";

        return new MeResponse(fullName, email, phone, roles);
    }

    private static String buildDisplayName(final String username) {
        if (username == null || username.isBlank()) return "User";
        String base = username.contains("@") ? username.substring(0, username.indexOf('@')) : username;
        return base.substring(0, 1).toUpperCase() + base.substring(1);
    }
}