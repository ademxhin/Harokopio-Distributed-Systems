package dit.hua.gr.greenride.web.ui;

import dit.hua.gr.greenride.core.model.Person;
import dit.hua.gr.greenride.service.AdminService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/admin")
    public String adminHome(Model model) {
        // Γεμίζουμε το μοντέλο με τα δεδομένα για να μην κρασάρει η Thymeleaf
        model.addAttribute("stats", adminService.getSystemStatistics());
        model.addAttribute("flaggedUsers", adminService.getFlaggedUsers());
        return "admin";
    }
}