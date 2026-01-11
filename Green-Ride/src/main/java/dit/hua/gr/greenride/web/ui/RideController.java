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

    /**
     * Passenger: Search available rides
     * Driver: friendly message
     */
    @GetMapping("/search")
    public String showAvailableRides(Model model,
                                     @AuthenticationPrincipal ApplicationUserDetails userDetails) {

        if (userDetails == null) {
            return "redirect:/login";
        }

        Person currentUser = userDetails.getPerson();

        if (currentUser.getUserType() == UserType.DRIVER) {
            model.addAttribute("title", "Passenger access required");
            model.addAttribute("message", "Please login as a passenger to search rides.");
            model.addAttribute("ctaText", "Logout");
            model.addAttribute("ctaHref", "/logout");
            return "rides_not_passenger";
        }

        LocalDateTime limit = LocalDateTime.now().plusMinutes(10);

        List<Ride> allUpcomingRides = rideRepository.findByDepartureTimeAfter(limit);

        List<Long> alreadyBookedRideIds = bookingRepository.findAllByPerson(currentUser)
                .stream()
                .map(b -> b.getRide().getId())
                .toList();

        List<Ride> filteredRides = allUpcomingRides.stream()
                .filter(ride -> !alreadyBookedRideIds.contains(ride.getId()))
                .toList();

        model.addAttribute("rides", filteredRides);
        return "rides";
    }

    /**
     * Passenger bookings page
     * Driver: friendly message
     */
    @GetMapping("/bookings")
    public String showMyBookings(Model model,
                                 @AuthenticationPrincipal ApplicationUserDetails userDetails) {

        if (userDetails == null) {
            return "redirect:/login";
        }

        Person currentUser = userDetails.getPerson();

        if (currentUser.getUserType() == UserType.DRIVER) {
            model.addAttribute("title", "Passenger access required");
            model.addAttribute("message", "Please login as a passenger to view bookings.");
            model.addAttribute("ctaText", "Logout");
            model.addAttribute("ctaHref", "/logout");
            return "rides_not_passenger";
        }

        LocalDateTime limit = LocalDateTime.now().plusMinutes(10);

        List<Booking> allBookings = bookingRepository.findByPerson(currentUser);

        List<Booking> activeBookings = allBookings.stream()
                .filter(b -> b.getRide().getDepartureTime().isAfter(limit))
                .toList();

        model.addAttribute("bookings", activeBookings);
        return "bookings";
    }

    /**
     * ✅ NO combined history.
     * /rides/history redirects based on the current user's UserType.
     */
    @GetMapping("/history")
    public String historyRedirect(@AuthenticationPrincipal ApplicationUserDetails userDetails) {

        if (userDetails == null) return "redirect:/login";

        Person currentUser = userDetails.getPerson();

        if (currentUser.getUserType() == UserType.DRIVER) {
            return "redirect:/rides/history/driver";
        }
        return "redirect:/rides/history/passenger";
    }

    /**
     * Driver-only history: completed rides you offered as a driver.
     * Uses existing history.html/css.
     */
    @GetMapping("/history/driver")
    public String showDriverHistory(Model model,
                                    @AuthenticationPrincipal ApplicationUserDetails userDetails) {

        if (userDetails == null) return "redirect:/login";

        Person currentUser = userDetails.getPerson();

        if (currentUser.getUserType() != UserType.DRIVER) {
            model.addAttribute("title", "Driver access required");
            model.addAttribute("message", "Please login as a driver to view driver ride history.");
            model.addAttribute("ctaText", "Logout");
            model.addAttribute("ctaHref", "/logout");
            return "rides_not_passenger";
        }

        LocalDateTime limit = LocalDateTime.now().plusMinutes(10);

        List<Ride> drivenRides = rideRepository.findByDriverAndDepartureTimeBefore(currentUser, limit);

        List<Ride> sorted = drivenRides.stream()
                .sorted((r1, r2) -> r2.getDepartureTime().compareTo(r1.getDepartureTime()))
                .toList();

        model.addAttribute("rides", sorted);
        return "history";
    }

    /**
     * ✅ Passenger-only history: completed rides you booked as a passenger.
     * Uses existing history.html/css.
     */
    @GetMapping("/history/passenger")
    public String showPassengerHistory(Model model,
                                       @AuthenticationPrincipal ApplicationUserDetails userDetails) {

        if (userDetails == null) return "redirect:/login";

        Person currentUser = userDetails.getPerson();

        if (currentUser.getUserType() != UserType.PASSENGER) {
            model.addAttribute("title", "Passenger access required");
            model.addAttribute("message", "Please login as a passenger to view passenger ride history.");
            model.addAttribute("ctaText", "Logout");
            model.addAttribute("ctaHref", "/logout");
            return "rides_not_passenger";
        }

        LocalDateTime limit = LocalDateTime.now().plusMinutes(10);

        List<Booking> pastBookings =
                bookingRepository.findByPersonAndRide_DepartureTimeBefore(currentUser, limit);

        List<Ride> bookedRides = pastBookings.stream()
                .map(Booking::getRide)
                .distinct()
                .sorted((r1, r2) -> r2.getDepartureTime().compareTo(r1.getDepartureTime()))
                .toList();

        model.addAttribute("rides", bookedRides);
        return "history";
    }

    /**
     * Driver-only: upcoming rides offered (not completed yet)
     */
    @GetMapping("/offered")
    public String showMyOfferedRides(Model model,
                                     @AuthenticationPrincipal ApplicationUserDetails userDetails) {

        if (userDetails == null) {
            return "redirect:/login";
        }

        Person currentUser = userDetails.getPerson();

        if (currentUser.getUserType() != UserType.DRIVER) {
            model.addAttribute("title", "Driver access required");
            model.addAttribute("message", "Please login as a driver to view your offered rides.");
            model.addAttribute("ctaText", "Logout");
            model.addAttribute("ctaHref", "/logout");
            return "rides_not_passenger";
        }

        LocalDateTime limit = LocalDateTime.now().plusMinutes(10);

        List<Ride> upcoming = rideRepository.findByDriverAndDepartureTimeAfter(currentUser, limit);

        List<Ride> sorted = upcoming.stream()
                .sorted((r1, r2) -> r1.getDepartureTime().compareTo(r2.getDepartureTime()))
                .toList();

        model.addAttribute("rides", sorted);
        return "offered_rides";
    }

    @GetMapping("/ratings")
    public String ratingsRedirect(@AuthenticationPrincipal ApplicationUserDetails userDetails) {
        if (userDetails == null) return "redirect:/login";

        Person currentUser = userDetails.getPerson();

        if (currentUser.getUserType() == UserType.DRIVER) {
            return "redirect:/rides/ratings/driver";
        }
        return "redirect:/rides/ratings/passenger";
    }

    /**
     * DRIVER: rate passengers
     */
    @GetMapping("/ratings/driver")
    public String showRatingsAsDriver(@RequestParam(value = "search", required = false) String search,
                                      Model model,
                                      @AuthenticationPrincipal ApplicationUserDetails userDetails) {

        if (userDetails == null) return "redirect:/login";
        Person currentUser = userDetails.getPerson();

        UserType targetType = UserType.PASSENGER;
        List<Person> availableUsers = (search != null && !search.isBlank())
                ? personRepository.findByFirstNameContainingIgnoreCaseAndUserType(search, targetType)
                : personRepository.findAllByUserType(targetType);

        List<Long> alreadyRatedIds = availableUsers.stream()
                .filter(p -> ratingRepository.existsByRaterAndRatedPerson(currentUser, p))
                .map(Person::getId)
                .toList();

        model.addAttribute("users", availableUsers);
        model.addAttribute("alreadyRatedIds", alreadyRatedIds); // ✅ Προσθήκη στο model
        model.addAttribute("search", search);

        return "ratings";
    }

    /**
     * PASSENGER: rate drivers
     */
    @GetMapping("/ratings/passenger")
    public String showRatingsAsPassenger(@RequestParam(value = "search", required = false) String search,
                                         Model model,
                                         @AuthenticationPrincipal ApplicationUserDetails userDetails) {

        if (userDetails == null) return "redirect:/login";
        Person currentUser = userDetails.getPerson();

        UserType targetType = UserType.DRIVER;
        List<Person> availableUsers = (search != null && !search.isBlank())
                ? personRepository.findByFirstNameContainingIgnoreCaseAndUserType(search, targetType)
                : personRepository.findAllByUserType(targetType);

        List<Long> alreadyRatedIds = availableUsers.stream()
                .filter(p -> ratingRepository.existsByRaterAndRatedPerson(currentUser, p))
                .map(Person::getId)
                .toList();

        model.addAttribute("users", availableUsers);
        model.addAttribute("alreadyRatedIds", alreadyRatedIds); // ✅ Προσθήκη στο model
        model.addAttribute("search", search);

        return "ratings";
    }
    @PostMapping("/ratings/submit")
    @PreAuthorize("hasAuthority('ROLE_PASSENGER') or hasAuthority('ROLE_DRIVER')")
    public String submitRating(@RequestParam("userId") Long userId,
                               @RequestParam("score") int score,
                               @AuthenticationPrincipal ApplicationUserDetails userDetails) {

        Person currentUser = userDetails.getPerson();
        Person targetPerson = personRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (ratingRepository.existsByRaterAndRatedPerson(currentUser, targetPerson)) {
            return "redirect:/rides/ratings?error=already_rated";
        }

        Rating rating = new Rating();
        rating.setRater(currentUser);
        rating.setRatedPerson(targetPerson);
        rating.setScore(score);

        ratingRepository.save(rating);

        targetPerson.getRatings().add(rating);
        personRepository.save(targetPerson);

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

        LocalDateTime departureDateTime = LocalDateTime.of(form.getDate(), form.getTime());
        if (departureDateTime.isBefore(LocalDateTime.now())) {
            return "redirect:/rides/offer?error=past-date";
        }

        Ride ride = new Ride();
        ride.setStartLocation(form.getOrigin());
        ride.setEndLocation(form.getDestination());
        ride.setDepartureTime(departureDateTime);
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

        boolean alreadyBooked = bookingRepository.existsByRideAndPerson(ride, userDetails.getPerson());
        if (alreadyBooked) {
            return "redirect:/rides/search?error=already_booked";
        }

        if (ride.getDepartureTime().isBefore(LocalDateTime.now().plusMinutes(10))) {
            return "redirect:/rides/search?error=too_late";
        }

        if (ride.getSeatsAvailable() > 0) {
            Booking booking = new Booking();
            booking.setRide(ride);
            booking.setPerson(userDetails.getPerson());
            booking.setCreatedAt(LocalDateTime.now());

            bookingRepository.save(booking);

            ride.setSeatsAvailable(ride.getSeatsAvailable() - 1);
            ride.setBookedSeats(ride.getBookedSeats() + 1);
            rideRepository.save(ride);

            // ✅ ΔΙΟΡΘΩΣΗ: Ανακατεύθυνση στη σελίδα των κρατήσεων
            return "redirect:/rides/bookings?success";
        }

        return "redirect:/rides/search?error=no_seats";
    }
}