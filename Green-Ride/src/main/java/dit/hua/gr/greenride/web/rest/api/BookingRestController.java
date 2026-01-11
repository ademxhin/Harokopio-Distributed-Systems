package dit.hua.gr.greenride.web.rest.api;

import dit.hua.gr.greenride.core.model.Booking;
import dit.hua.gr.greenride.core.model.BookingStatus;
import dit.hua.gr.greenride.core.model.Person;
import dit.hua.gr.greenride.core.model.Ride;
import dit.hua.gr.greenride.core.repository.BookingRepository;
import dit.hua.gr.greenride.core.repository.RideRepository;
import dit.hua.gr.greenride.core.security.ApplicationUserDetails;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;

@Tag(name = "Bookings", description = "Create and cancel bookings")
@RestController
@RequestMapping("/api/bookings")
public class BookingRestController {

    private final BookingRepository bookingRepository;
    private final RideRepository rideRepository;

    public BookingRestController(BookingRepository bookingRepository,
                                 RideRepository rideRepository) {
        this.bookingRepository = bookingRepository;
        this.rideRepository = rideRepository;
    }

    private static Person requireUser(ApplicationUserDetails userDetails) {
        if (userDetails == null || userDetails.getPerson() == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Not authenticated");
        }
        return userDetails.getPerson();
    }

    // =========================
    // Create booking
    // =========================

    public record CreateBookingRequest(@NotNull Long rideId) {}

    @Operation(summary = "Create a booking (Passenger)")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Booking created"),
            @ApiResponse(responseCode = "400", description = "Ride not found or full"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not authorized (not PASSENGER)")
    })
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    @PreAuthorize("hasAuthority('ROLE_PASSENGER')")
    @Transactional
    public Booking createBooking(@Valid @RequestBody CreateBookingRequest request,
                                 @AuthenticationPrincipal ApplicationUserDetails userDetails) {

        Person passenger = requireUser(userDetails);

        Ride ride = rideRepository.findById(request.rideId()).orElse(null);
        if (ride == null || ride.getSeatsAvailable() <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Ride not found or full");
        }

        Booking booking = new Booking();
        booking.setRide(ride);
        booking.setPerson(passenger);
        booking.setStatus(BookingStatus.PENDING);
        booking.setCreatedAt(LocalDateTime.now());

        bookingRepository.save(booking);

        ride.setSeatsAvailable(ride.getSeatsAvailable() - 1);
        rideRepository.save(ride);

        return booking;
    }

    // =========================
    // Cancel booking
    // =========================

    public record CancelBookingRequest(@NotNull Long bookingId) {}

    @Operation(summary = "Cancel a booking (Passenger)")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Booking cancelled"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not authorized (not owner / not PASSENGER)"),
            @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    @PostMapping("/cancel")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ROLE_PASSENGER')")
    @Transactional
    public void cancelBooking(@Valid @RequestBody CancelBookingRequest request,
                              @AuthenticationPrincipal ApplicationUserDetails userDetails) {

        Person currentUser = requireUser(userDetails);

        Booking booking = bookingRepository.findById(request.bookingId()).orElse(null);
        if (booking == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found: " + request.bookingId());
        }

        if (!booking.getPerson().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to cancel this booking");
        }

        Ride ride = booking.getRide();
        ride.setSeatsAvailable(ride.getSeatsAvailable() + 1);
        rideRepository.save(ride);

        bookingRepository.delete(booking);
    }

    @Operation(summary = "Cancel a booking by id (Passenger) - RESTful")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Booking cancelled"),
            @ApiResponse(responseCode = "401", description = "Not authenticated"),
            @ApiResponse(responseCode = "403", description = "Not authorized (not owner / not PASSENGER)"),
            @ApiResponse(responseCode = "404", description = "Booking not found")
    })
    @DeleteMapping("/{bookingId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("hasAuthority('ROLE_PASSENGER')")
    @Transactional
    public void cancelBookingById(@PathVariable Long bookingId,
                                  @AuthenticationPrincipal ApplicationUserDetails userDetails) {

        Person currentUser = requireUser(userDetails);

        Booking booking = bookingRepository.findById(bookingId).orElse(null);
        if (booking == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Booking not found: " + bookingId);
        }

        if (!booking.getPerson().getId().equals(currentUser.getId())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "Not allowed to cancel this booking");
        }

        Ride ride = booking.getRide();
        ride.setSeatsAvailable(ride.getSeatsAvailable() + 1);
        rideRepository.save(ride);

        bookingRepository.delete(booking);
    }
}