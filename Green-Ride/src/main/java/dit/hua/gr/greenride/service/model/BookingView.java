package dit.hua.gr.greenride.service.model;

import dit.hua.gr.greenride.core.model.BookingStatus;

import java.time.LocalDateTime;

/**
 * Public view model for the Booking entity.
 */
public record BookingView(
        Long id,
        Long rideId,
        String driverUserId,
        String passengerUserId,
        String startLocation,
        String endLocation,
        LocalDateTime departureTime,
        BookingStatus status
) {
}
