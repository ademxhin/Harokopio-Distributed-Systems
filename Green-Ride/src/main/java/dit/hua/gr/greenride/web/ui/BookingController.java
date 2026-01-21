package dit.hua.gr.greenride.web.ui;

import dit.hua.gr.greenride.core.model.Booking;
import dit.hua.gr.greenride.core.model.BookingStatus;
import dit.hua.gr.greenride.core.model.Person;
import dit.hua.gr.greenride.core.model.Ride;
import dit.hua.gr.greenride.core.repository.BookingRepository;
import dit.hua.gr.greenride.core.repository.RideRepository;
import dit.hua.gr.greenride.core.security.ApplicationUserDetails;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@Controller
@RequestMapping("/bookings")
public class BookingController {

    private final BookingRepository bookingRepository;
    private final RideRepository rideRepository;

    public BookingController(BookingRepository bookingRepository,
                             RideRepository rideRepository) {
        this.bookingRepository = bookingRepository;
        this.rideRepository = rideRepository;
    }

    @PostMapping("/create")
    @PreAuthorize("hasAuthority('ROLE_PASSENGER')")
    public String createBooking(@RequestParam("rideId") Long rideId,
                                @AuthenticationPrincipal ApplicationUserDetails userDetails) {

        if (userDetails == null) return "redirect:/login";

        Ride ride = rideRepository.findById(rideId).orElse(null);
        if (ride == null) {
            return "redirect:/rides/search?error=ride-not-found";
        }

        if (ride.getAvailableSeats() <= 0) {
            return "redirect:/rides/search?error=ride-full";
        }

        LocalDateTime now = LocalDateTime.now();
        if (ride.getDepartureTime() == null || !ride.getDepartureTime().isAfter(now.plusMinutes(10))) {
            return "redirect:/rides/search?error=too_late";
        }

        Person passenger = userDetails.person();

        if (bookingRepository.existsByRideAndPerson(ride, passenger)) {
            return "redirect:/rides/search?error=already_booked";
        }

        Booking booking = new Booking();
        booking.setRide(ride);
        booking.setPerson(passenger);
        booking.setStatus(BookingStatus.PENDING);
        booking.setCreatedAt(now);

        bookingRepository.save(booking);

        ride.setBookedSeats(ride.getBookedSeats() + 1);
        rideRepository.save(ride);

        return "redirect:/rides/bookings?success";
    }

    @PostMapping("/cancel")
    @PreAuthorize("hasAuthority('ROLE_PASSENGER')")
    public String cancelBooking(@RequestParam("bookingId") Long bookingId,
                                @AuthenticationPrincipal ApplicationUserDetails userDetails) {

        if (userDetails == null) return "redirect:/login";

        Booking booking = bookingRepository.findById(bookingId).orElse(null);

        if (booking == null ||
                booking.getPerson() == null ||
                booking.getPerson().getId() == null ||
                userDetails.person() == null ||
                userDetails.person().getId() == null ||
                !booking.getPerson().getId().equals(userDetails.person().getId())) {
            return "redirect:/rides/bookings?error=unauthorized";
        }

        Ride ride = booking.getRide();
        if (ride == null || ride.getDepartureTime() == null) {
            return "redirect:/rides/bookings?error=invalid_ride";
        }

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime cutoff = ride.getDepartureTime().minusMinutes(10);

        if (!now.isBefore(cutoff)) {
            return "redirect:/rides/bookings?error=too_late_to_cancel";
        }

        ride.setBookedSeats(Math.max(0, ride.getBookedSeats() - 1));
        rideRepository.save(ride);

        bookingRepository.delete(booking);

        return "redirect:/rides/bookings?canceled";
    }
}
