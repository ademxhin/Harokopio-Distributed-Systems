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
        this.personService = personService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Authentication authentication, Model model) {
        if (AuthUtils.isAuthenticated(authentication)) {
            return "redirect:/profile";
        }

        model.addAttribute("createPersonRequest",
                new CreatePersonRequest("", "", "", "", "", "",
                        PersonType.PASSENGER)
        );

        model.addAttribute("personTypes",
                new PersonType[]{PersonType.PASSENGER, PersonType.DRIVER});

        return "register";
    }

    @PostMapping("/register")
    public String handleFormSubmission(
            Authentication authentication,
            @Valid @ModelAttribute("createPersonRequest") CreatePersonRequest createPersonRequest,
            BindingResult bindingResult,
            Model model
    ) {
        if (AuthUtils.isAuthenticated(authentication)) {
            return "redirect:/profile";
        }

        if (bindingResult.hasErrors()) {
            model.addAttribute("personTypes",
                    new PersonType[]{PersonType.PASSENGER, PersonType.DRIVER});
            return "register";
        }

        if (!createPersonRequest.rawPassword()
                .equals(createPersonRequest.confirmRawPassword())) {

            bindingResult.rejectValue(
                    "confirmRawPassword",
                    "password.mismatch",
                    "Password and Confirm Password do not match"
            );

            model.addAttribute("personTypes",
                    new PersonType[]{PersonType.PASSENGER, PersonType.DRIVER});
            return "register";
        }

        try {
            CreatePersonResult result =
                    personService.createPerson(createPersonRequest, true);

            if (result.created()) {
                model.addAttribute("signupSuccess", true);
                model.addAttribute("personTypes",
                        new PersonType[]{PersonType.PASSENGER, PersonType.DRIVER});
                return "register";
            }

            model.addAttribute("errorMessage", result.reason());
            model.addAttribute("personTypes",
                    new PersonType[]{PersonType.PASSENGER, PersonType.DRIVER});
            return "register";

        } catch (ExternalServiceUnavailableException ex) {
            bindingResult.rejectValue(
                    "mobilePhoneNumber",
                    "noc.down",
                    "Phone validation service is currently unavailable."
            );
            model.addAttribute("personTypes",
                    new PersonType[]{PersonType.PASSENGER, PersonType.DRIVER});
            return "register";
        }
    }
}