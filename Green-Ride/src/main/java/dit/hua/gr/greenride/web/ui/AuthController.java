package dit.hua.gr.greenride.web.ui;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AuthController {

    @GetMapping("/login")
    public String login(Authentication authentication, HttpServletRequest request, Model model) {
        if (AuthUtils.isAuthenticated(authentication)) {
            return "redirect:/profile";
        }

        if (request.getParameter("error") != null) {
            model.addAttribute("error", "Invalid email or password.");
        }
        if (request.getParameter("logout") != null) {
            model.addAttribute("message", "You have been logged out.");
        }

        return "login";
    }

    @GetMapping("/admin/login")
    public String adminLogin(Authentication authentication, HttpServletRequest request, Model model) {
        if (AuthUtils.isAuthenticated(authentication)) {
            boolean isAdmin = authentication.getAuthorities().stream()
                    .anyMatch(a -> "ROLE_ADMIN".equals(a.getAuthority()));
            return isAdmin ? "redirect:/admin" : "redirect:/profile";
        }

        if (request.getParameter("error") != null) {
            model.addAttribute("error", "Invalid admin email or password.");
        }
        if (request.getParameter("logout") != null) {
            model.addAttribute("message", "You have been logged out.");
        }

        return "admin-login";
    }

    // ✅ keep only this
    @GetMapping("/logged-out")
    public String loggedOut() {
        return "logout";
    }
}
