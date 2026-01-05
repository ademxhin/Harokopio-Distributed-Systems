package dit.hua.gr.greenride.web.ui;

import dit.hua.gr.greenride.core.model.Person;
import dit.hua.gr.greenride.core.model.Ride;
import dit.hua.gr.greenride.core.model.UserType;
import dit.hua.gr.greenride.core.repository.PersonRepository;
import dit.hua.gr.greenride.core.repository.RideRepository;
import dit.hua.gr.greenride.web.ui.model.CreateRideForm;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import dit.hua.gr.greenride.core.security.ApplicationUserDetails;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/rides")
public class RideController {

    private final PersonRepository personRepository;
    private final RideRepository rideRepository;

    public RideController(PersonRepository personRepository, RideRepository rideRepository) {
        this.personRepository = personRepository;
        this.rideRepository = rideRepository;
    }

    // ✅ Διορθώνει το σφάλμα 'rideForm' στη σελίδα Offer
    @GetMapping("/offer")
    @PreAuthorize("hasAuthority('ROLE_DRIVER')")
    public String showCreateRideForm(Model model) {
        model.addAttribute("rideForm", new CreateRideForm());
        return "new_ride";
    }

    @PostMapping("/offer")
    @PreAuthorize("hasAuthority('ROLE_DRIVER')")
    public String processCreateRide(@ModelAttribute("rideForm") CreateRideForm form) {
        Ride ride = new Ride();
        ride.setOrigin(form.getOrigin());
        ride.setDestination(form.getDestination());
        ride.setDepartureTime(LocalDateTime.of(form.getDate(), form.getTime()));
        ride.setSeatsAvailable(form.getSeatsAvailable());
        this.rideRepository.save(ride);
        return "redirect:/profile";
    }

    @GetMapping("/ratings")
    public String showRatingsPage(
            @RequestParam(value = "search", required = false) String search,
            Model model,
            @AuthenticationPrincipal ApplicationUserDetails userDetails) {

        if (userDetails == null) return "redirect:/login";

        Person currentUser = userDetails.getPerson();

        UserType targetType = (currentUser.getUserType() == UserType.DRIVER)
                ? UserType.PASSENGER
                : UserType.DRIVER;

        List<Person> availableUsers = (search != null && !search.isBlank())
                ? personRepository.findByFirstNameContainingIgnoreCaseAndUserType(search, targetType)
                : personRepository.findAllByUserType(targetType);

        model.addAttribute("ratingTarget", targetType == UserType.PASSENGER ? "Passenger" : "Driver");
        model.addAttribute("users", availableUsers);

        return "ratings";
    }

    @PostMapping("/ratings/submit")
    public String submitRating(@RequestParam("userId") Long userId, @RequestParam("score") int score) {
        System.out.println("Submitted: User ID " + userId + " with Score " + score);
        return "redirect:/profile";
    }

    @GetMapping("/history")
    public String showRideHistory(Model model) {
        model.addAttribute("rides", rideRepository.findAll());
        return "history";
    }
}