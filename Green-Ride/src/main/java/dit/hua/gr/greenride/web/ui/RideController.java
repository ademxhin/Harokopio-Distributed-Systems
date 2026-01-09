package dit.hua.gr.greenride.web.ui;

import dit.hua.gr.greenride.core.model.*;
import dit.hua.gr.greenride.core.repository.*;
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
    private final RatingRepository ratingRepository;

    public RideController(PersonRepository personRepository,
                          RideRepository rideRepository,
                          BookingRepository bookingRepository,
                          RatingRepository ratingRepository) {
        this.personRepository = personRepository;
        this.rideRepository = rideRepository;
        this.bookingRepository = bookingRepository;
        this.ratingRepository = ratingRepository;
    }

    @GetMapping("/search")
    @PreAuthorize("hasAuthority('ROLE_PASSENGER')")
    public String showAvailableRides(Model model) {
        List<Ride> rides = rideRepository.findAll();
        model.addAttribute("rides", rides);
        return "rides";
    }

    @GetMapping("/bookings")
    @PreAuthorize("hasAuthority('ROLE_PASSENGER')")
    public String showMyBookings(Model model, @AuthenticationPrincipal ApplicationUserDetails userDetails) {
        if (userDetails == null) return "redirect:/login";
        List<Booking> bookings = bookingRepository.findByPerson(userDetails.getPerson());
        model.addAttribute("bookings", bookings);
        return "bookings";
    }

    @GetMapping("/history")
    public String showRideHistory(Model model) {
        List<Ride> rides = rideRepository.findAll();
        model.addAttribute("rides", rides);
        return "history";
    }

    @GetMapping("/ratings")
    public String showRatingsPage(@RequestParam(value = "search", required = false) String search,
                                  Model model,
                                  @AuthenticationPrincipal ApplicationUserDetails userDetails) {
        if (userDetails == null) return "redirect:/login";

        Person currentUser = userDetails.getPerson();
        UserType targetType = (currentUser.getUserType() == UserType.DRIVER) ? UserType.PASSENGER : UserType.DRIVER;

        List<Person> availableUsers = (search != null && !search.isBlank())
                ? personRepository.findByFirstNameContainingIgnoreCaseAndUserType(search, targetType)
                : personRepository.findAllByUserType(targetType);

        model.addAttribute("users", availableUsers);
        model.addAttribute("ratingTarget", targetType.name());
        return "ratings";
    }

    @PostMapping("/ratings/submit")
    @PreAuthorize("hasAuthority('ROLE_PASSENGER') or hasAuthority('ROLE_DRIVER')")
    public String submitRating(@RequestParam("userId") Long userId,
                               @RequestParam("score") int score) {
        Person person = personRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Rating rating = new Rating();
        rating.setRatedPerson(person);
        rating.setScore(score);

        ratingRepository.save(rating);
        person.getRatings().add(rating);
        personRepository.save(person);

        return "redirect:/rides/ratings?success";
    }

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
        if (userDetails == null) return "redirect:/login";
        Ride ride = new Ride();
        // ✅ ΔΙΟΡΘΩΣΗ: startLocation & endLocation
        ride.setStartLocation(form.getOrigin());
        ride.setEndLocation(form.getDestination());
        ride.setDepartureTime(LocalDateTime.of(form.getDate(), form.getTime()));
        ride.setSeatsAvailable(form.getSeatsAvailable());
        ride.setDriver(userDetails.getPerson());
        rideRepository.save(ride);
        return "redirect:/profile";
    }

    @GetMapping("/reservation")
    public String showReservationPage(@RequestParam("id") Long rideId, Model model) {
        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ride Id:" + rideId));
        model.addAttribute("ride", ride);
        return "reservation";
    }

    @PostMapping("/book/{id}")
    @PreAuthorize("hasAuthority('ROLE_PASSENGER')")
    public String processBooking(@PathVariable Long id,
                                 @AuthenticationPrincipal ApplicationUserDetails userDetails) {
        if (userDetails == null) return "redirect:/login";

        Ride ride = rideRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid ride Id:" + id));

        // ✅ ΔΙΟΡΘΩΣΗ: seatsAvailable
        if (ride.getSeatsAvailable() > 0) {
            Booking booking = new Booking();
            booking.setRide(ride);
            booking.setPerson(userDetails.getPerson());
            booking.setCreatedAt(LocalDateTime.now());
            bookingRepository.save(booking);

            ride.setSeatsAvailable(ride.getSeatsAvailable() - 1);
            ride.setBookedSeats(ride.getBookedSeats() + 1);
            rideRepository.save(ride);
        }
        return "redirect:/rides/bookings";
    }
}