package dit.hua.gr.greenride.web.ui;

import dit.hua.gr.greenride.core.model.UserType;
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
        if (personBusinessLogicService == null) throw new NullPointerException("personBusinessLogicService is null");
        this.personBusinessLogicService = personBusinessLogicService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(final Authentication authentication, final Model model) {
        if (AuthUtils.isAuthenticated(authentication)) {
            return "redirect:/profile";
        }

        // ✅ CreatePersonRequest now expects UserType (not String)
        model.addAttribute("createPersonRequest",
                new CreatePersonRequest(
                        "", "", "", "", "", "",
                        UserType.PASSENGER // default selection (change if you want)
                )
        );

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

        // 1) Bean validation errors (includes userType @NotNull)
        if (bindingResult.hasErrors()) {
            return "register";
        }

        // 2) Password must match confirm
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
                    this.personBusinessLogicService.createPerson(createPersonRequest, true);

            if (createPersonResult.created()) {
                return "redirect:/login";
            }

            // business failure (duplicate email, invalid phone, etc.)
            model.addAttribute("errorMessage", createPersonResult.reason());
            return "register";

        } catch (ExternalServiceUnavailableException ex) {
            bindingResult.rejectValue(
                    "mobilePhoneNumber",
                    "noc.down",
                    "Phone validation service is currently unavailable. Please try again later."
            );
            return "register";
        }
    }
}