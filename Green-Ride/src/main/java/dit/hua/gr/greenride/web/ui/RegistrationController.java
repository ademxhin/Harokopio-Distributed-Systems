package dit.hua.gr.greenride.web.ui;

import dit.hua.gr.greenride.service.PersonService;
import dit.hua.gr.greenride.service.model.CreatePersonRequest;
import dit.hua.gr.greenride.service.model.CreatePersonResult;

import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

/**
 * UI controller for managing user registration in GreenRide.
 */
@Controller
public class RegistrationController {

    private final PersonService personService;

    public RegistrationController(final PersonService personService) {
        if (personService == null) throw new NullPointerException("personService is null");
        this.personService = personService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(
            final Authentication authentication,
            final Model model
    ) {
        // If user is already authenticated, redirect to profile page.
        if (AuthUtils.isAuthenticated(authentication)) {
            return "redirect:/profile";
        }

        // Initial data for the form.
        final CreatePersonRequest createPersonRequest =
                new CreatePersonRequest("", "", "", "", "");
        model.addAttribute("createPersonRequest", createPersonRequest);

        return "register";
    }

    @PostMapping("/register")
    public String handleFormSubmission(
            final Authentication authentication,
            @Valid @ModelAttribute("createPersonRequest") final CreatePersonRequest createPersonRequest,
            final BindingResult bindingResult, // IMPORTANT: must come immediately after @Valid
            final Model model
    ) {
        // If user is already authenticated, redirect to profile page.
        if (AuthUtils.isAuthenticated(authentication)) {
            return "redirect:/profile";
        }

        // If form validation failed, return to registration form.
        if (bindingResult.hasErrors()) {
            return "register";
        }

        // Call the service to create a new Person (with SMS notification enabled).
        final CreatePersonResult createPersonResult =
                this.personService.createPerson(createPersonRequest, true);

        // If registration succeeded, redirect to login page.
        if (createPersonResult.created()) {
            return "redirect:/login";
        }

        // Registration failed: show error message and keep the submitted data.
        model.addAttribute("createPersonRequest", createPersonRequest);
        model.addAttribute("errorMessage", createPersonResult.reason());

        return "register";
    }
}