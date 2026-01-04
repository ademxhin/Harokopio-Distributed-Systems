package dit.hua.gr.greenride.web.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class LogoutPageController {

    @GetMapping("/logged-out")
    public String loggedOut() {
        return "logout"; // templates/logout.html
    }
}
