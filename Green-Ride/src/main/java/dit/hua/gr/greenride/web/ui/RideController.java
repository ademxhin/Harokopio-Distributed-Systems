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
    public String showAvailableRides(Model model, @AuthenticationPrincipal ApplicationUserDetails userDetails) {
        if (userDetails == null) return "redirect:/login";

        Person currentUser = userDetails.getPerson();
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

    @GetMapping("/bookings")
    @PreAuthorize("hasAuthority('ROLE_PASSENGER')")
    public String showMyBookings(Model model, @AuthenticationPrincipal ApplicationUserDetails userDetails) {
        if (userDetails == null) return "redirect:/login";

        java.time.LocalDateTime limit = java.time.LocalDateTime.now().plusMinutes(10);

        List<Booking> allBookings = bookingRepository.findByPerson(userDetails.getPerson());

        List<Booking> activeBookings = allBookings.stream()
                .filter(b -> b.getRide().getDepartureTime().isAfter(limit))
                .toList();

        model.addAttribute("bookings", activeBookings);
        return "bookings";
    }

    @GetMapping("/history")
    public String showRideHistory(Model model, @AuthenticationPrincipal ApplicationUserDetails userDetails) {
        if (userDetails == null) return "redirect:/login";

        Person currentUser = userDetails.getPerson();
        java.time.LocalDateTime limit = java.time.LocalDateTime.now().plusMinutes(10);

        List<Ride> drivenRides = rideRepository.findByDriverAndDepartureTimeBefore(currentUser, limit);

        List<Booking> pastBookings = bookingRepository.findByPersonAndRide_DepartureTimeBefore(currentUser, limit);
        List<Ride> bookedRides = pastBookings.stream().map(Booking::getRide).toList();

        List<Ride> allHistory = new java.util.ArrayList<>();
        allHistory.addAll(drivenRides);
        allHistory.addAll(bookedRides);

        List<Ride> finalHistory = allHistory.stream().distinct()
                .sorted((r1, r2) -> r2.getDepartureTime().compareTo(r1.getDepartureTime()))
                .toList();

        model.addAttribute("rides", finalHistory);
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

        // averages per userId
        java.util.Map<Long, Double> ratingAverages = new java.util.HashMap<>();
        for (Person p : availableUsers) {
            Double avg = ratingRepository.findAverageScoreForPerson(p.getId()); // μπορεί να είναι null
            ratingAverages.put(p.getId(), avg);
        }

        model.addAttribute("users", availableUsers);
        model.addAttribute("ratingTarget", targetType.name());
        model.addAttribute("ratingAverages", ratingAverages);

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

        if (ride.getDepartureTime().isBefore(java.time.LocalDateTime.now().plusMinutes(10))) {
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
        }
        return "redirect:/profile";
    }
}