package dit.hua.gr.greenride.web.rest.api;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import dit.hua.gr.greenride.core.model.*;
import dit.hua.gr.greenride.core.repository.*;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * REST API endpoints for Rides / Bookings / Ratings (based on existing UI logic).
 *
 * IMPORTANT:
 * - We do NOT use ApplicationUserDetails in REST.
 * - We use Principal.getName() (email/subject from JWT) and fetch Person from DB.
 */
@Tag(name = "Rides", description = "Ride search, offers, bookings, history and ratings")
@RestController
@RequestMapping("/api/rides")
public class RideRestController {

    private final PersonRepository personRepository;
    private final RideRepository rideRepository;
    private final BookingRepository bookingRepository;
    private final RatingRepository ratingRepository;

    public RideRestController(PersonRepository personRepository,
                              RideRepository rideRepository,
                              BookingRepository bookingRepository,
                              RatingRepository ratingRepository) {
        this.personRepository = personRepository;
        this.rideRepository = rideRepository;
        this.bookingRepository = bookingRepository;
        this.ratingRepository = ratingRepository;
    }

    // =========================================================
    // Helpers
    // =========================================================

    /**
     * From JWT auth filter, principal username == subject (email).
     * So Principal#getName() should return user's email.
     */
    private Person requireUser(final Principal principal) {
        if (principal == null || principal.getName() == null || principal.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }

        final String email = principal.getName();

        // NOTE: If your repo method differs (findByEmailIgnoreCase etc), adjust here.
        return personRepository.findByEmailAddress(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found: " + email));
    }

    private static void requireType(final Person user, final UserType expected) {
        if (user == null || user.getUserType() == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        if (user.getUserType() != expected) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Access denied for user type: " + user.getUserType()
            );
        }
    }

    private static LocalDateTime limitNowPlus10() {
        return LocalDateTime.now().plusMinutes(10);
    }

    // =========================================================
    // 1) Passenger: Search rides
    // =========================================================

    @Operation(summary = "Search available rides (Passenger)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of available rides"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Driver cannot search rides"),
    })
    @GetMapping("/search")
    @PreAuthorize("hasRole('PASSENGER')")
    public List<Ride> searchAvailableRides(final Principal principal) {

        Person currentUser = requireUser(principal);
        requireType(currentUser, UserType.PASSENGER);

        LocalDateTime limit = limitNowPlus10();

        List<Ride> allUpcomingRides = rideRepository.findByDepartureTimeAfter(limit);

        List<Long> alreadyBookedRideIds = bookingRepository.findAllByPerson(currentUser)
                .stream()
                .map(b -> b.getRide().getId())
                .toList();

        return allUpcomingRides.stream()
                .filter(ride -> !alreadyBookedRideIds.contains(ride.getId()))
                .toList();
    }

    // =========================================================
    // 2) Passenger: My bookings (upcoming only)
    // =========================================================

    @Operation(summary = "Get my active bookings (Passenger)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of active bookings"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Driver cannot view passenger bookings"),
    })
    @GetMapping("/bookings")
    @PreAuthorize("hasRole('PASSENGER')")
    public List<Booking> myActiveBookings(final Principal principal) {

        Person currentUser = requireUser(principal);
        requireType(currentUser, UserType.PASSENGER);

        LocalDateTime limit = limitNowPlus10();

        List<Booking> allBookings = bookingRepository.findByPerson(currentUser);

        return allBookings.stream()
                .filter(b -> b.getRide().getDepartureTime().isAfter(limit))
                .toList();
    }

    // =========================================================
    // 3) Driver: Offered rides (upcoming)
    // =========================================================

    @Operation(summary = "Get my offered upcoming rides (Driver)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of upcoming rides offered by driver"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Passenger cannot view offered rides"),
    })
    @GetMapping("/offered")
    @PreAuthorize("hasRole('DRIVER')")
    public List<Ride> myOfferedRides(final Principal principal) {

        Person currentUser = requireUser(principal);
        requireType(currentUser, UserType.DRIVER);

        LocalDateTime limit = limitNowPlus10();

        List<Ride> upcoming = rideRepository.findByDriverAndDepartureTimeAfter(currentUser, limit);

        return upcoming.stream()
                .sorted((r1, r2) -> r1.getDepartureTime().compareTo(r2.getDepartureTime()))
                .toList();
    }

    // =========================================================
    // 4) History: driver
    // =========================================================

    @Operation(summary = "Get my ride history as driver (completed rides)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of completed rides"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Passenger cannot access driver history"),
    })
    @GetMapping("/history/driver")
    @PreAuthorize("hasRole('DRIVER')")
    public List<Ride> driverHistory(final Principal principal) {

        Person currentUser = requireUser(principal);
        requireType(currentUser, UserType.DRIVER);

        LocalDateTime limit = limitNowPlus10();

        List<Ride> drivenRides = rideRepository.findByDriverAndDepartureTimeBefore(currentUser, limit);

        return drivenRides.stream()
                .sorted((r1, r2) -> r2.getDepartureTime().compareTo(r1.getDepartureTime()))
                .toList();
    }

    // =========================================================
    // 5) History: passenger
    // =========================================================

    @Operation(summary = "Get my ride history as passenger (completed booked rides)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of completed rides (distinct)"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Driver cannot access passenger history"),
    })
    @GetMapping("/history/passenger")
    @PreAuthorize("hasRole('PASSENGER')")
    public List<Ride> passengerHistory(final Principal principal) {

        Person currentUser = requireUser(principal);
        requireType(currentUser, UserType.PASSENGER);

        LocalDateTime limit = limitNowPlus10();

        List<Booking> pastBookings =
                bookingRepository.findByPersonAndRide_DepartureTimeBefore(currentUser, limit);

        return pastBookings.stream()
                .map(Booking::getRide)
                .distinct()
                .sorted((r1, r2) -> r2.getDepartureTime().compareTo(r1.getDepartureTime()))
                .toList();
    }

    // =========================================================
    // 6) Driver: Create ride
    // =========================================================

    public record CreateRideRequest(
            @NotBlank String origin,
            @NotBlank String destination,

            @NotNull
            @Schema(type = "string", format = "date", example = "2026-01-11")
            LocalDate date,

            @NotNull
            @Schema(type = "string", format = "time", example = "15:30")
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "HH:mm")
            LocalTime time,

            @Min(1) @Max(8) int seatsAvailable
    ) {}

    @Operation(summary = "Offer a new ride (Driver)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Ride created"),
            @ApiResponse(responseCode = "400", description = "Invalid data (e.g., past departure time)"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Passenger cannot offer rides"),
    })
    @PostMapping("/offer")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('DRIVER')")
    public Ride offerRide(@Valid @RequestBody CreateRideRequest request,
                          final Principal principal) {

        Person currentUser = requireUser(principal);
        requireType(currentUser, UserType.DRIVER);

        LocalDateTime departureDateTime = LocalDateTime.of(request.date(), request.time());
        if (departureDateTime.isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Departure time cannot be in the past");
        }

        Ride ride = new Ride();
        ride.setStartLocation(request.origin());
        ride.setEndLocation(request.destination());
        ride.setDepartureTime(departureDateTime);
        ride.setSeatsAvailable(request.seatsAvailable());
        ride.setDriver(currentUser);

        return rideRepository.save(ride);
    }

    // =========================================================
    // 7) Ride details (reservation page equivalent)
    // =========================================================

    @Operation(summary = "Get ride details by id")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Ride details"),
            @ApiResponse(responseCode = "404", description = "Ride not found")
    })
    @GetMapping("/{rideId}")
    public Ride getRide(@PathVariable Long rideId) {
        return rideRepository.findById(rideId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride not found: " + rideId));
    }

    // =========================================================
    // 8) Passenger: Book ride
    // =========================================================

    @Operation(summary = "Book a ride (Passenger)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Booking created"),
            @ApiResponse(responseCode = "400", description = "Too late / No seats"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Driver cannot book rides"),
            @ApiResponse(responseCode = "404", description = "Ride not found")
    })
    @PostMapping("/{rideId}/book")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('PASSENGER')")
    public Booking bookRide(@PathVariable Long rideId, final Principal principal) {

        Person currentUser = requireUser(principal);
        requireType(currentUser, UserType.PASSENGER);

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride not found: " + rideId));

        boolean alreadyBooked = bookingRepository.existsByRideAndPerson(ride, currentUser);
        if (alreadyBooked) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Already booked this ride");
        }

        if (ride.getDepartureTime().isBefore(LocalDateTime.now().plusMinutes(10))) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Too late to book this ride");
        }

        if (ride.getSeatsAvailable() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No available seats");
        }

        Booking booking = new Booking();
        booking.setRide(ride);
        booking.setPerson(currentUser);
        booking.setCreatedAt(LocalDateTime.now());

        bookingRepository.save(booking);

        ride.setSeatsAvailable(ride.getSeatsAvailable() - 1);
        ride.setBookedSeats(ride.getBookedSeats() + 1);
        rideRepository.save(ride);

        return booking;
    }

    // =========================================================
    // 9) Ratings: list users to rate (driver -> passengers, passenger -> drivers)
    // =========================================================

    @Operation(summary = "List users available for rating (Driver rates passengers OR Passenger rates drivers)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "List of users"),
            @ApiResponse(responseCode = "401", description = "Not authenticated")
    })
    @GetMapping("/ratings/users")
    @PreAuthorize("hasAnyRole('PASSENGER','DRIVER')")
    public List<Person> ratingUsers(@RequestParam(required = false) String search,
                                    final Principal principal) {

        Person currentUser = requireUser(principal);

        UserType targetType = (currentUser.getUserType() == UserType.DRIVER)
                ? UserType.PASSENGER
                : UserType.DRIVER;

        if (search != null && !search.isBlank()) {
            return personRepository.findByFirstNameContainingIgnoreCaseAndUserType(search, targetType);
        }
        return personRepository.findAllByUserType(targetType);
    }

    // =========================================================
    // 10) Submit rating
    // =========================================================

    public record SubmitRatingRequest(
            @NotNull Long userId,
            @Min(1) @Max(5) int score
    ) {}

    @Operation(summary = "Submit a rating (Driver or Passenger)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Rating created"),
            @ApiResponse(responseCode = "400", description = "Already rated / invalid score"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "404", description = "Target user not found")
    })
    @PostMapping("/ratings")
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAnyRole('PASSENGER','DRIVER')")
    public Rating submitRating(@Valid @RequestBody SubmitRatingRequest request,
                               final Principal principal) {

        Person currentUser = requireUser(principal);

        Person targetPerson = personRepository.findById(request.userId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found: " + request.userId()));

        if (ratingRepository.existsByRaterAndRatedPerson(currentUser, targetPerson)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Already rated this user");
        }

        Rating rating = new Rating();
        rating.setRater(currentUser);
        rating.setRatedPerson(targetPerson);
        rating.setScore(request.score());

        ratingRepository.save(rating);

        targetPerson.getRatings().add(rating);
        personRepository.save(targetPerson);

        return rating;
    }

    // =========================================================
    // 11) Driver: cancel offered ride
    // =========================================================

    @Operation(summary = "Cancel (delete) a ride I offered (Driver)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Ride deleted"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not authorized (not owner driver)"),
            @ApiResponse(responseCode = "404", description = "Ride not found")
    })
    @DeleteMapping("/{rideId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('DRIVER')")
    @Transactional
    public void deleteMyRide(@PathVariable Long rideId, final Principal principal) {

        Person currentUser = requireUser(principal);
        requireType(currentUser, UserType.DRIVER);

        Ride ride = rideRepository.findById(rideId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Ride not found: " + rideId));

        if (ride.getDriver() == null || ride.getDriver().getId() == null
                || currentUser.getId() == null
                || !ride.getDriver().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "You can only delete your own rides");
        }

        List<Booking> bookings = bookingRepository.findByRide(ride);
        bookingRepository.deleteAll(bookings);

        rideRepository.delete(ride);
    }
}