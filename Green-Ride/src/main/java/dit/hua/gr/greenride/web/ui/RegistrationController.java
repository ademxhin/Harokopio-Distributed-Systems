package dit.hua.gr.greenride.web.ui;

import dit.hua.gr.greenride.service.PersonBusinessLogicService;
import dit.hua.gr.greenride.service.model.CreatePersonRequest;
import dit.hua.gr.greenride.service.model.CreatePersonResult;
import dit.hua.gr.greenride.web.ui.exceptions.ExternalServiceUnavailableException;
import jakarta.validation.Valid;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistrationController {

    private final PersonBusinessLogicService personBusinessLogicService;

    public RegistrationController(final PersonBusinessLogicService personBusinessLogicService) {
        if (personBusinessLogicService == null) throw new NullPointerException();
        this.personBusinessLogicService = personBusinessLogicService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(final Authentication authentication, final Model model) {
        if (AuthUtils.isAuthenticated(authentication)) {
            return "redirect:/profile";
        }

        // record -> provide initial values
        model.addAttribute("createPersonRequest", new CreatePersonRequest("", "", "", "", "", ""));
        return "register";
    }

    @PostMapping("/register")
    public String handleFormSubmission(
            final Authentication authentication,
            @Valid @ModelAttribute("createPersonRequest") final CreatePersonRequest createPersonRequest,
            final BindingResult bindingResult,
            final Model model
    ) {
        if (AuthUtils.isAuthenticated(authentication)) {
            return "redirect:/profile";
        }

        // 1) Bean validation errors
        if (bindingResult.hasErrors()) {
            return "register";
        }

        // 2) Additional rule: password must match confirm
        if (!createPersonRequest.rawPassword().equals(createPersonRequest.confirmRawPassword())) {
            bindingResult.rejectValue(
                    "confirmRawPassword",
                    "password.mismatch",
                    "Password and Confirm Password do not match"
            );
            return "register";
        }

        try {
            final CreatePersonResult createPersonResult =
                    this.personBusinessLogicService.createPerson(createPersonRequest);

            if (createPersonResult.created()) {
                return "redirect:/login";
            }

            // business failure (duplicate email, invalid phone, etc.)
            model.addAttribute("errorMessage", createPersonResult.reason());
            return "register";

        } catch (ExternalServiceUnavailableException ex) {
            // NOC is down (explicit message)
            bindingResult.rejectValue(
                    "mobilePhoneNumber",
                    "noc.down",
                    "Phone validation service is currently unavailable. Please try again later."
            );
            return "register";
        }
    }
}