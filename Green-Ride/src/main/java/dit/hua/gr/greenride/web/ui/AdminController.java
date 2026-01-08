package dit.hua.gr.greenride.web.ui;

import dit.hua.gr.greenride.core.model.Person;
import dit.hua.gr.greenride.core.repository.PersonRepository;
import dit.hua.gr.greenride.service.AdminService;
import dit.hua.gr.greenride.web.ui.AdminStats;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final AdminService adminService;
    private final PersonRepository personRepository;
    private final PasswordEncoder passwordEncoder;

    public AdminController(AdminService adminService, PersonRepository personRepository, PasswordEncoder passwordEncoder) {
        this.adminService = adminService;
        this.personRepository = personRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        // ✅ Λήψη στατιστικών και λιστών από το Service
        model.addAttribute("stats", adminService.getSystemStatistics());
        model.addAttribute("flaggedUsers", adminService.getFlaggedUsers()); // Λίστα με Strings
        model.addAttribute("allUsers", adminService.getAllUsersExcludingAdmins()); // Λίστα με Person αντικείμενα
        model.addAttribute("kickedUserNames", adminService.getKickedUserNames()); // Λίστα με Strings
        return "admin";
    }

    @GetMapping("/kick/{id}")
    public String kickUser(@PathVariable Long id) {
        personRepository.findById(id).ifPresent(u -> {
            adminService.logKickedUser(u.getFullName());
            personRepository.delete(u);
        });
        return "redirect:/admin/dashboard";
    }
}