package dit.hua.gr.greenride.service.model;

import dit.hua.gr.greenride.core.model.RideStatus;

import java.time.LocalDateTime;

/**
 * Public view model for the Ride entity.
 *
 * Used to display ride information to drivers and passengers.
 */
public record RideView(
        Long id,
        String driverUserId,
        String startLocation,
        String endLocation,
        LocalDateTime departureTime,
        int availableSeats,
        int bookedSeats,
        RideStatus status
) {
}
