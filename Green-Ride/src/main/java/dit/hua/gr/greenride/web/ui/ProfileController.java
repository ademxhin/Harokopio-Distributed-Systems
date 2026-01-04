package dit.hua.gr.greenride.web.ui;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.security.Principal;

@Controller
public class ProfileController {

    @GetMapping("/profile")
    public String showProfile(final Model model, final Principal principal) {

        // Συνήθως είναι email/username (ό,τι έχεις στο UserDetails/JWT subject)
        String username = (principal != null) ? principal.getName() : null;

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        model.addAttribute("username", username);
        model.addAttribute("authorities", (auth != null) ? auth.getAuthorities() : null);

        return "profile";
    }
}