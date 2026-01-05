package dit.hua.gr.greenride.web.ui;

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

    @GetMapping("/profile")
    public String showProfile(
            final Model model,
            final Principal principal,
            @RequestParam(name = "tab", defaultValue = "home") String tab
    ) {
        final String username = (principal != null && principal.getName() != null && !principal.getName().isBlank())
                ? principal.getName()
                : "User";

        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        final Set<String> roles = (auth != null && auth.getAuthorities() != null)
                ? auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet())
                : Set.of();

        // Προσπάθεια να “βγάλουμε” userType από roles (μέχρι να το παίρνεις από DB)
        final boolean passengerEnabled = hasAnyRoleLike(roles, "PASSENGER");
        final boolean driverEnabled = hasAnyRoleLike(roles, "DRIVER");

        final String roleLabel;
        if (passengerEnabled && driverEnabled) roleLabel = "Passenger & Driver";
        else if (driverEnabled) roleLabel = "Driver";
        else if (passengerEnabled) roleLabel = "Passenger";
        else roleLabel = "User";

        // ✅ Full name: αν είναι email, πάρε πριν το @ και κάνε capitalize
        final String fullName = buildDisplayName(username);

        // Model for sidebar + top
        model.addAttribute("activeTab", tab);
        model.addAttribute("fullName", fullName);
        model.addAttribute("emailAddress", username);          // κράτα το email/username
        model.addAttribute("mobilePhoneNumber", "—");          // placeholder μέχρι DB
        model.addAttribute("roleLabel", roleLabel);

        // Model for actions
        model.addAttribute("passengerEnabled", passengerEnabled);
        model.addAttribute("driverEnabled", driverEnabled);

        // choose view by tab
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
                .filter(r -> r != null)
                .map(String::toUpperCase)
                .anyMatch(r -> r.contains(n));
    }

    /**
     * Builds a nice display name without DB:
     * - If username looks like email => part before '@'
     * - Capitalize first letter
     * - Fallback "User"
     */
    private static String buildDisplayName(final String username) {
        if (username == null || username.isBlank()) return "User";

        String base = username;
        final int at = username.indexOf('@');
        if (at > 0) base = username.substring(0, at);

        base = base.trim();
        if (base.isBlank()) return "User";

        // Capitalize first letter (simple)
        if (base.length() == 1) return base.toUpperCase();
        return base.substring(0, 1).toUpperCase() + base.substring(1);
    }
}
