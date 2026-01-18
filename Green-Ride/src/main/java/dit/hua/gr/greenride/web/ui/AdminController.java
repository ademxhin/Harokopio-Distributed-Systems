package dit.hua.gr.greenride.web.ui;

import dit.hua.gr.greenride.core.repository.PersonRepository;
import dit.hua.gr.greenride.service.AdminService;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final PersonRepository personRepository;

    public AdminController(AdminService adminService, PersonRepository personRepository) {
        this.adminService = adminService;
        this.personRepository = personRepository;
    }

    @Transactional
    @GetMapping("/kick/{id}")
    public String kickUser(@PathVariable Long id) {
        personRepository.findById(id).ifPresent(u -> {
            adminService.logKickedUser(u.getFullName());
            personRepository.delete(u);
        });

        return "redirect:/profile?tab=home";
    }
}