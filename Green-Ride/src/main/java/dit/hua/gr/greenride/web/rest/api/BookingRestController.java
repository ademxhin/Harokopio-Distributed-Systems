package dit.hua.gr.greenride.web.rest.api;

import dit.hua.gr.greenride.core.model.Booking;
import dit.hua.gr.greenride.core.model.BookingStatus;
import dit.hua.gr.greenride.core.model.Person;
import dit.hua.gr.greenride.core.model.PersonType;
import dit.hua.gr.greenride.core.model.Ride;
import dit.hua.gr.greenride.core.repository.BookingRepository;
import dit.hua.gr.greenride.core.repository.PersonRepository;
import dit.hua.gr.greenride.core.repository.RideRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.security.Principal;
import java.time.LocalDateTime;
import java.util.List;

@Tag(name = "Bookings", description = "Create, list and cancel bookings")
@RestController
@RequestMapping("/api/bookings")
public class BookingRestController {

    private final BookingRepository bookingRepository;
    private final RideRepository rideRepository;
    private final PersonRepository personRepository;

    public BookingRestController(BookingRepository bookingRepository,
                                 RideRepository rideRepository,
                                 PersonRepository personRepository) {
        this.bookingRepository = bookingRepository;
        this.rideRepository = rideRepository;
        this.personRepository = personRepository;
    }

    public record CreateBookingRequest(@NotNull Long rideId) {}

    public record BookingResponse(
            Long id,
            Long rideId,
            LocalDateTime createdAt,
            BookingStatus status
    ) {}

    private BookingResponse toBookingResponse(Booking b) {
        if (b == null) return null;
        return new BookingResponse(
                b.getId(),
                b.getRide() != null ? b.getRide().getId() : null,
                b.getCreatedAt(),
                b.getStatus()
        );
    }

    private Person requireUser(final Principal principal) {
        if (principal == null || principal.getName() == null || principal.getName().isBlank()) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        final String email = principal.getName();
        return personRepository.findByEmailAddress(email)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found: " + email));
    }

    private static void requirePassenger(final Person user) {
        if (user == null || user.getPersonType() == null) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Access denied");
        }
        if (user.getPersonType() != PersonType.PASSENGER) {
            throw new ResponseStatusException(
                    HttpStatus.FORBIDDEN,
                    "Access denied for user type: " + user.getPersonType()
            );
        }
    }

    private static LocalDateTime nowPlus10() {
        return LocalDateTime.now().plusMinutes(10);
    }

    @Operation(summary = "Get my bookings (Passenger)")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "My bookings returned"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not authorized (not PASSENGER)")
    })
    @GetMapping("/my")
    @PreAuthorize("hasRole('PASSENGER')")
    public List<BookingResponse> myBookings(final Principal principal) {

        Person passenger = requireUser(principal);
        requirePassenger(passenger);

        List<Booking> bookings = bookingRepository.findByPerson(passenger);

        return bookings.stream()
                .sorted((a, b) -> b.getCreatedAt().compareTo(a.getCreatedAt()))
                .map(this::toBookingResponse)
                .toList();
    }

    @Operation(summary = "Create a booking (Passenger) - same logic as UI")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Booking created"),
            @ApiResponse(responseCode = "400", description = "Ride not found / full / too late / already booked"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not authorized (not PASSENGER)")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasRole('PASSENGER')")
    @Transactional
    public BookingResponse createBooking(@Valid @RequestBody CreateBookingRequest request,
                                         final Principal principal) {

        Person passenger = requireUser(principal);
        requirePassenger(passenger);

        Ride ride = rideRepository.findById(request.rideId())
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Ride not found: " + request.rideId()
                ));

        if (ride.getDepartureTime() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ride has no departure time");
        }
        if (ride.getDepartureTime().isBefore(nowPlus10())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Too late to book this ride");
        }

        if (bookingRepository.existsByRideAndPerson(ride, passenger)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Already booked this ride");
        }

        if (ride.getSeatsAvailable() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "No available seats");
        }

        Booking booking = new Booking();
        booking.setRide(ride);
        booking.setPerson(passenger);
        booking.setStatus(BookingStatus.PENDING);
        booking.setCreatedAt(LocalDateTime.now());

        bookingRepository.save(booking);

        ride.setSeatsAvailable(ride.getSeatsAvailable() - 1);
        rideRepository.save(ride);

        return toBookingResponse(booking);
    }

    @Operation(summary = "Cancel a booking by id (Passenger) - same logic as UI")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Booking cancelled"),
            @ApiResponse(responseCode = "400", description = "Too late to cancel"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not authorized (not PASSENGER)"),
            @ApiResponse(responseCode = "404", description = "Booking not found (or not yours)")
    })
    @DeleteMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasRole('PASSENGER')")
    @Transactional
    public void cancelBookingById(@PathVariable Long bookingId,
                                  final Principal principal) {

        Person passenger = requireUser(principal);
        requirePassenger(passenger);

        Booking booking = bookingRepository.findByIdAndPerson(bookingId, passenger)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND,
                        "Booking not found (or not yours): " + bookingId
                ));

        Ride ride = booking.getRide();
        if (ride == null || ride.getDepartureTime() == null) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Booking has invalid ride/departure time");
        }

        LocalDateTime cutoff = ride.getDepartureTime().minusMinutes(10);
        if (!LocalDateTime.now().isBefore(cutoff)) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Too late to cancel this booking");
        }

        ride.setSeatsAvailable(ride.getSeatsAvailable() + 1);
        rideRepository.save(ride);

        bookingRepository.delete(booking);
    }
}