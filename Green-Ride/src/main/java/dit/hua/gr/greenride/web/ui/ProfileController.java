package dit.hua.gr.greenride.web.ui;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

/**
 * UI controller for managing profile.
 */
@Controller
public class ProfileController {

    @GetMapping("/profile")
    public String showProfile(final Model model, final Principal principal) {

        // principal.getName() συνήθως είναι το username/email (ό,τι έβαλες στο UserDetails)
        String username = (principal != null) ? principal.getName() : null;

        // Προαιρετικά: παίρνουμε και roles για εμφάνιση
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        model.addAttribute("username", username);
        model.addAttribute("authorities", (auth != null) ? auth.getAuthorities() : null);

        return "profile";
    }
}