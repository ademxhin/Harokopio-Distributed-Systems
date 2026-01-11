package dit.hua.gr.greenride.web.ui;

import dit.hua.gr.greenride.core.model.Person;
import dit.hua.gr.greenride.core.repository.PersonRepository;
import dit.hua.gr.greenride.service.AdminService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.transaction.annotation.Transactional;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final PersonRepository personRepository;

    public AdminController(AdminService adminService, PersonRepository personRepository) {
        this.adminService = adminService;
        this.personRepository = personRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("stats", adminService.getSystemStatistics());
        model.addAttribute("flaggedUsers", adminService.getFlaggedUsers());
        model.addAttribute("allUsers", adminService.getAllUsersExcludingAdmins());
        model.addAttribute("kickedUserNames", adminService.getKickedUserNames());
        return "admin";
    }

    @Transactional
    @GetMapping("/kick/{id}")
    public String kickUser(@PathVariable Long id) {
        personRepository.findById(id).ifPresent(u -> {
            adminService.logKickedUser(u.getFullName());
            personRepository.delete(u);
        });
        return "redirect:/admin/dashboard";
    }
}