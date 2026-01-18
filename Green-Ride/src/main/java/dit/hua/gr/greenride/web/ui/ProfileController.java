package dit.hua.gr.greenride.web.ui;

import dit.hua.gr.greenride.core.model.Person;
import dit.hua.gr.greenride.core.repository.PersonRepository;
import dit.hua.gr.greenride.service.AdminService;
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

    private final PersonRepository personRepository;
    private final AdminService adminService;

    public ProfileController(PersonRepository personRepository, AdminService adminService) {
        this.personRepository = personRepository;
        this.adminService = adminService;
    }

    @GetMapping("/profile")
    public String showProfile(
            final Model model,
            final Principal principal,
            @RequestParam(name = "tab", defaultValue = "home") String tab
    ) {
        if (principal == null) return "redirect:/login";

        final String email = principal.getName();
        final Person person = personRepository.findByEmailAddress(email).orElse(null);

        final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        final Set<String> roles = (auth != null)
                ? auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toSet())
                : Set.of();

        final boolean isAdmin = hasAnyRoleLike(roles, "ADMIN");
        final boolean passengerEnabled = hasAnyRoleLike(roles, "PASSENGER");
        final boolean driverEnabled = hasAnyRoleLike(roles, "DRIVER");

        final String roleLabel = resolveRoleLabel(isAdmin, passengerEnabled, driverEnabled);

        final String normalizedTab = normalizeTab(tab);

        model.addAttribute("activeTab", normalizedTab);
        model.addAttribute("fullName", person != null ? person.getFullName() : buildDisplayName(email));
        model.addAttribute("emailAddress", email);
        model.addAttribute("mobilePhoneNumber",
                (person != null && person.getMobilePhoneNumber() != null)
                        ? person.getMobilePhoneNumber()
                        : "Not provided"
        );

        model.addAttribute("roleLabel", roleLabel);
        model.addAttribute("isAdmin", isAdmin);
        model.addAttribute("passengerEnabled", passengerEnabled);
        model.addAttribute("driverEnabled", driverEnabled);

        if (isAdmin && "home".equals(normalizedTab)) {
            model.addAttribute("stats", adminService.getSystemStatistics());
            model.addAttribute("allUsers", adminService.getAllUsersExcludingAdmins());
            model.addAttribute("kickedUserNames", adminService.getKickedUserNames());
            model.addAttribute("flaggedUsers", adminService.getFlaggedUsers());
        }

        return "profile";
    }

    private static String normalizeTab(String tab) {
        if (tab == null) return "home";
        final String t = tab.trim().toLowerCase();
        return switch (t) {
            case "home", "personal", "security" -> t;
            default -> "home";
        };
    }

    private static String resolveRoleLabel(boolean isAdmin, boolean passengerEnabled, boolean driverEnabled) {
        if (isAdmin) return "Administrator";
        if (passengerEnabled && driverEnabled) return "Passenger & Driver";
        if (driverEnabled) return "Driver";
        if (passengerEnabled) return "Passenger";
        return "User";
    }

    private static boolean hasAnyRoleLike(final Set<String> roles, final String needle) {
        if (roles == null || roles.isEmpty()) return false;
        final String n = needle.toUpperCase();
        return roles.stream()
                .filter(r -> r != null)
                .anyMatch(r -> r.toUpperCase().contains(n));
    }

    private static String buildDisplayName(final String username) {
        if (username == null || username.isBlank()) return "User";
        String base = username.contains("@") ? username.substring(0, username.indexOf('@')) : username;
        if (base.isBlank()) return "User";
        return base.substring(0, 1).toUpperCase() + base.substring(1);
    }
}
