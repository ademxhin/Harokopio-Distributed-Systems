package dit.hua.gr.greenride.web.ui;

import dit.hua.gr.greenride.core.model.Person;
import dit.hua.gr.greenride.core.repository.PersonRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.security.Principal;
import java.util.Set;
import java.util.stream.Collectors;

@Controller
public class ProfileController {

    // ✅ Προσθήκη του Repository
    private final PersonRepository personRepository;

    public ProfileController(PersonRepository personRepository) {
        this.personRepository = personRepository;
    }

    @GetMapping("/profile")
    public String showProfile(
            final Model model,
            final Principal principal,
            @RequestParam(name = "tab", defaultValue = "home") String tab
    ) {
        if (principal == null) return "redirect:/login";

        final String email = principal.getName();

        Person person = personRepository.findByEmailAddress(email)
                .orElse(null);

        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        final Set<String> roles = (auth != null)
                ? auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet())
                : Set.of();

        final boolean isAdmin = hasAnyRoleLike(roles, "ADMIN");
        final boolean passengerEnabled = hasAnyRoleLike(roles, "PASSENGER");
        final boolean driverEnabled = hasAnyRoleLike(roles, "DRIVER");

        String roleLabel;
        if (isAdmin) {
            roleLabel = "Administrator";
        } else if (passengerEnabled && driverEnabled) {
            roleLabel = "Passenger & Driver";
        } else if (driverEnabled) {
            roleLabel = "Driver";
        } else if (passengerEnabled) {
            roleLabel = "Passenger";
        } else {
            roleLabel = "User";
        }

        model.addAttribute("activeTab", tab);
        model.addAttribute("fullName", person != null ? person.getFullName() : buildDisplayName(email));
        model.addAttribute("emailAddress", email);

        model.addAttribute("mobilePhoneNumber", (person != null && person.getMobilePhoneNumber() != null)
                ? person.getMobilePhoneNumber()
                : "Not provided");

        model.addAttribute("roleLabel", roleLabel);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("passengerEnabled", passengerEnabled);
        model.addAttribute("driverEnabled", driverEnabled);

        return switch (tab.toLowerCase()) {
            case "personal" -> "profile-personal";
            case "security" -> "profile-security";
            default -> "profile-home";
        };
    }

    private static boolean hasAnyRoleLike(final Set<String> roles, final String needle) {
        if (roles == null || roles.isEmpty()) return false;
        final String n = needle.toUpperCase();
        return roles.stream()
                .anyMatch(r -> r.toUpperCase().contains(n));
    }

    private static String buildDisplayName(final String username) {
        if (username == null || username.isBlank()) return "User";
        String base = username.contains("@") ? username.substring(0, username.indexOf('@')) : username;
        return base.substring(0, 1).toUpperCase() + base.substring(1);
    }
}