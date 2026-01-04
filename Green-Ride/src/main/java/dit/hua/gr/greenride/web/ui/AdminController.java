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

    @GetMapping("/admin")
    public String adminHome(Model model) {
        // Καλούμε το service για να πάρουμε τα στατιστικά και τους flagged χρήστες
        AdminStats stats = adminService.getSystemStatistics();
        List<Person> flaggedUsers = adminService.getFlaggedUsers();

        // Προσθήκη στο μοντέλο για την Thymeleaf
        model.addAttribute("stats", stats);
        model.addAttribute("flaggedUsers", flaggedUsers);

        return "admin"; // templates/admin.html
    }
}