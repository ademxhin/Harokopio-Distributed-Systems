package dit.hua.gr.greenride.web.ui;

import dit.hua.gr.greenride.core.model.Person;
import dit.hua.gr.greenride.service.AdminService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import java.util.List;

@Controller
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // Μέσα στο AdminController.java
    @GetMapping("/admin")
    public String adminHome(Model model) {
        AdminStats stats = adminService.getSystemStatistics();

        // ✅ Αυτό προκαλεί το σφάλμα αν η μέθοδος λείπει από το Service
        List<Person> flaggedUsers = adminService.getFlaggedUsers();

        model.addAttribute("stats", stats);
        model.addAttribute("flaggedUsers", flaggedUsers);
        return "admin";
    }
}