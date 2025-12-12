package dit.hua.gr.greenride.web.ui;

import dit.hua.gr.greenride.core.model.Person;
import dit.hua.gr.greenride.service.PersonService;
import org.apache.tomcat.util.net.openssl.ciphers.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class RegistrationController {

    private final PersonService personService;

    public RegistrationController(final PersonService personService) {
        if(personService == null) throw new NullPointerException();
        this.personService = personService;
    }


    @GetMapping("/register")
    public String showRegistrationForm(
            final Authentication authentication,
            final Model model){
        model.addAttribute();
        return "register";
    }

    @PostMapping("/register")
    public String handleRegistrationForm(
            @ModelAttribute("person") Person person, final Model model
    ) {
        person = this.personRepository.savePerson();
        model.addAttribute("person", person);
        return "register";
    }
}