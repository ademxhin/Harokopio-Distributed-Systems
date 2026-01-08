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

        // ✅ ΕΛΕΓΧΟΣ ΓΙΑ ADMIN
        final boolean isAdmin = hasAnyRoleLike(roles, "ADMIN");
        final boolean passengerEnabled = hasAnyRoleLike(roles, "PASSENGER");
        final boolean driverEnabled = hasAnyRoleLike(roles, "DRIVER");

        String roleLabel;
        if (isAdmin) {
            roleLabel = "Administrator"; // ✅ Αυτό θα εμφανίζεται τώρα
        } else if (passengerEnabled && driverEnabled) {
            roleLabel = "Passenger & Driver";
        } else if (driverEnabled) {
            roleLabel = "Driver";
        } else if (passengerEnabled) {
            roleLabel = "Passenger";
        } else {
            roleLabel = "User";
        }

        final String fullName = buildDisplayName(username);

        model.addAttribute("activeTab", tab);
        model.addAttribute("fullName", fullName);
        model.addAttribute("emailAddress", username);
        model.addAttribute("mobilePhoneNumber", "—");
        model.addAttribute("roleLabel", roleLabel);
        model.addAttribute("isAdmin", isAdmin); // Προσθήκη για το UI

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
                .filter(r -> r != null)
                .map(String::toUpperCase)
                .anyMatch(r -> r.contains(n));
    }

    private static String buildDisplayName(final String username) {
        if (username == null || username.isBlank()) return "User";
        String base = username;
        final int at = username.indexOf('@');
        if (at > 0) base = username.substring(0, at);
        base = base.trim();
        if (base.isBlank()) return "User";
        if (base.length() == 1) return base.toUpperCase();
        return base.substring(0, 1).toUpperCase() + base.substring(1);
    }
}