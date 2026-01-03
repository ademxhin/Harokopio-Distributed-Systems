package dit.hua.gr.greenride.web.ui;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DriverController {

    @GetMapping("/driver")
    public String driverPage() {
        return "driver";
    }
}
