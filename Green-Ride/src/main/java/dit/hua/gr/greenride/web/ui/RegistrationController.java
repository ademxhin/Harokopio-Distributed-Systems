package dit.hua.gr.greenride.web.ui;

import dit.hua.gr.greenride.core.model.PersonType;
import dit.hua.gr.greenride.service.PersonService;
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

    private final PersonService personService;

    public RegistrationController(final PersonService personService) {
        if (personService == null) throw new NullPointerException("personService is null");
        this.personService = personService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(final Authentication authentication, final Model model) {
        if (AuthUtils.isAuthenticated(authentication)) {
            return "redirect:/profile";
        }

        model.addAttribute("createPersonRequest",
                new CreatePersonRequest(
                        "", "", "", "", "", "",
                        PersonType.PASSENGER // default selection
                )
        );

        // (Optional) for dropdown options in Thymeleaf
        model.addAttribute("personTypes", new PersonType[]{PersonType.PASSENGER, PersonType.DRIVER});

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

        // 1) Bean validation errors (includes personType @NotNull)
        if (bindingResult.hasErrors()) {
            model.addAttribute("personTypes", new PersonType[]{PersonType.PASSENGER, PersonType.DRIVER});
            return "register";
        }

        // 2) Password must match confirm
        if (!createPersonRequest.rawPassword().equals(createPersonRequest.confirmRawPassword())) {
            bindingResult.rejectValue(
                    "confirmRawPassword",
                    "password.mismatch",
                    "Password and Confirm Password do not match"
            );
            model.addAttribute("personTypes", new PersonType[]{PersonType.PASSENGER, PersonType.DRIVER});
            return "register";
        }

        try {
            final CreatePersonResult createPersonResult =
                    this.personService.createPerson(createPersonRequest, true);

            if (createPersonResult.created()) {
                return "redirect:/login";
            }

            // business failure (duplicate email, invalid phone, etc.)
            model.addAttribute("errorMessage", createPersonResult.reason());
            model.addAttribute("personTypes", new PersonType[]{PersonType.PASSENGER, PersonType.DRIVER});
            return "register";

        } catch (ExternalServiceUnavailableException ex) {
            bindingResult.rejectValue(
                    "mobilePhoneNumber",
                    "noc.down",
                    "Phone validation service is currently unavailable. Please try again later."
            );
            model.addAttribute("personTypes", new PersonType[]{PersonType.PASSENGER, PersonType.DRIVER});
            return "register";
        }
    }
}
