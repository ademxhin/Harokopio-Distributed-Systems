package dit.hua.gr.greenride.web.ui;

import dit.hua.gr.greenride.core.model.Booking;
import dit.hua.gr.greenride.core.model.Person;
import dit.hua.gr.greenride.core.model.Ride;
import dit.hua.gr.greenride.core.model.UserType;
import dit.hua.gr.greenride.core.repository.BookingRepository;
import dit.hua.gr.greenride.core.repository.PersonRepository;
import dit.hua.gr.greenride.core.repository.RideRepository;
import dit.hua.gr.greenride.core.security.ApplicationUserDetails;
import dit.hua.gr.greenride.web.ui.model.CreateRideForm;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("/rides")
public class RideController {

    private final PersonRepository personRepository;
    private final RideRepository rideRepository;
    private final BookingRepository bookingRepository;

    public RideController(PersonRepository personRepository,
                          RideRepository rideRepository,
                          BookingRepository bookingRepository) {
        this.personRepository = personRepository;
        this.rideRepository = rideRepository;
        this.bookingRepository = bookingRepository;
    }

    // ============================
    // OFFER RIDE (DRIVER)
    // ============================
    @GetMapping("/offer")
    @PreAuthorize("hasAuthority('ROLE_DRIVER')")
    public String showCreateRideForm(Model model) {
        model.addAttribute("rideForm", new CreateRideForm());
        return "new_ride";
    }

    @PostMapping("/offer")
    @PreAuthorize("hasAuthority('ROLE_DRIVER')")
    public String processCreateRide(@ModelAttribute("rideForm") CreateRideForm form,
                                    @AuthenticationPrincipal ApplicationUserDetails userDetails) {

        if (userDetails == null) {
            return "redirect:/login";
        }

        Person driver = userDetails.getPerson();

        Ride ride = new Ride();
        ride.setOrigin(form.getOrigin());
        ride.setDestination(form.getDestination());
        ride.setDepartureTime(LocalDateTime.of(form.getDate(), form.getTime()));
        ride.setSeatsAvailable(form.getSeatsAvailable());
        ride.setDriver(driver);

        rideRepository.save(ride);
        return "redirect:/profile";
    }

    // ============================
    // SEARCH RIDES (PASSENGER)
    // ============================
    @GetMapping("/search")
    @PreAuthorize("hasAuthority('ROLE_PASSENGER')")
    public String showAvailableRides(Model model) {
        List<Ride> rides = rideRepository.findAll();
        model.addAttribute("rides", rides);
        return "search_rides";
    }

    // ============================
    // MY BOOKINGS (PASSENGER)
    // ============================
    @GetMapping("/bookings")
    @PreAuthorize("hasAuthority('ROLE_PASSENGER')")
    public String showMyBookings(Model model,
                                 @AuthenticationPrincipal ApplicationUserDetails userDetails) {

        if (userDetails == null) return "redirect:/login";

        Person currentUser = userDetails.getPerson();
        List<Booking> bookings = bookingRepository.findByPerson(currentUser);

        model.addAttribute("bookings", bookings);
        return "bookings";
    }

    // ============================
    // RATINGS
    // ============================
    @GetMapping("/ratings")
    public String showRatingsPage(
            @RequestParam(value = "search", required = false) String search,
            Model model,
            @AuthenticationPrincipal ApplicationUserDetails userDetails) {

        if (userDetails == null) return "redirect:/login";

        Person currentUser = userDetails.getPerson();
        List<Person> availableUsers;

        if (currentUser.getUserType() == UserType.BOTH) {
            availableUsers = personRepository.findAllByUserType(UserType.DRIVER);
            availableUsers.addAll(personRepository.findAllByUserType(UserType.PASSENGER));
            model.addAttribute("ratingTarget", "Driver & Passenger");
        } else {
            UserType targetType = (currentUser.getUserType() == UserType.DRIVER)
                    ? UserType.PASSENGER
                    : UserType.DRIVER;

            availableUsers = (search != null && !search.isBlank())
                    ? personRepository.findByFirstNameContainingIgnoreCaseAndUserType(search, targetType)
                    : personRepository.findAllByUserType(targetType);

            model.addAttribute("ratingTarget", targetType == UserType.DRIVER ? "Driver" : "Passenger");
        }

        model.addAttribute("users", availableUsers);
        return "ratings";
    }

    @PostMapping("/ratings/submit")
    public String submitRating(@RequestParam("userId") Long userId,
                               @RequestParam("score") int score) {
        System.out.println("Submitted: User ID " + userId + " with Score " + score);
        return "redirect:/profile";
    }

    // ============================
    // RIDE HISTORY
    // ============================
    @GetMapping("/history")
    public String showRideHistory(Model model) {
        model.addAttribute("rides", rideRepository.findAll());
        return "history";
    }
}
