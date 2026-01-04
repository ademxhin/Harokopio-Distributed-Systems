package dit.hua.gr.greenride.web.ui;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String login(
            final Authentication authentication,
            final HttpServletRequest request,
            final Model model
    ) {
        // If already logged in -> go to profile
        if (AuthUtils.isAuthenticated(authentication)) {
            return "redirect:/profile";
        }

        // Support both styles:
        // - Spring Security default: ?error / ?logout (param.*)
        // - Our model attributes: error/message
        if (request.getParameter("error") != null) {
            model.addAttribute("error", "Invalid email or password.");
        }
        if (request.getParameter("logout") != null) {
            model.addAttribute("message", "You have been logged out.");
        }

        return "login";
    }

    @GetMapping("/admin/login")
    public String adminLogin(
            final Authentication authentication,
            final HttpServletRequest request,
            final Model model
    ) {
        // If already logged in:
        // - Admins -> admin panel
        // - Non-admins -> profile (no reason to be here)
        if (AuthUtils.isAuthenticated(authentication)) {
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));

            return isAdmin ? "redirect:/admin" : "redirect:/profile";
        }

        // Messages (same behavior as /login)
        if (request.getParameter("error") != null) {
            model.addAttribute("error", "Invalid admin email or password.");
        }
        if (request.getParameter("logout") != null) {
            model.addAttribute("message", "You have been logged out.");
        }

        return "admin-login";
    }

    /**
     * NOTE:
     * Spring Security normally handles logout as a POST to /logout.
     * You already have a GET /logout page (probably a confirmation page or redirect).
     * We keep your behavior intact.
     */
    @GetMapping("/logout")
    public String logout(final Authentication authentication) {
        if (AuthUtils.isAnonymous(authentication)) {
            return "redirect:/login";
        }
        return "logout";
    }
}