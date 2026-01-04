package dit.hua.gr.greenride.web.ui;

import dit.hua.gr.greenride.core.model.Ride;
import dit.hua.gr.greenride.core.repository.RideRepository;
import dit.hua.gr.greenride.web.ui.model.CreateRideForm;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;

@Controller
public class RideController {

    private final RideRepository rideRepository;

    public RideController(RideRepository rideRepository) {
        this.rideRepository = rideRepository;
    }

    @GetMapping("/new_ride")
    @PreAuthorize("hasAuthority('ROLE_DRIVER')") // Μόνο οδηγοί
    public String showCreateRideForm(Model model) {
        model.addAttribute("rideForm", new CreateRideForm());
        return "new_ride";
    }

    @PostMapping("/new_ride")
    @PreAuthorize("hasAuthority('ROLE_DRIVER')")
    public String processCreateRide(@ModelAttribute("rideForm") CreateRideForm form) {
        Ride ride = new Ride();
        ride.setOrigin(form.getOrigin());
        ride.setDestination(form.getDestination());
        ride.setDepartureTime(LocalDateTime.of(form.getDate(), form.getTime()));

        // ✅ ΑΛΛΑΓΗ: Κλήση της μεθόδου setAvailableSeats που αντιστοιχεί στο νέο όνομα
        ride.setAvailableSeats(form.getSeatsAvailable());

        rideRepository.save(ride);
        return "redirect:/profile";
    }
}